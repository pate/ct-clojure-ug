(ns quotes.core
  (:gen-class)
  (:require [ring.server.standalone :refer [serve]]
            [compojure.handler :refer [site]]
            [ring.middleware.file-info :refer [wrap-file-info]]
            [ring.middleware.resource :refer [wrap-resource]]
            [compojure.core :refer [defroutes GET POST]]
            [hiccup.core :refer [html]]
            [ring.util.response :refer [redirect]]
            [datomic.api :as d]
            [clojure.string :as string]
            [quotes.views :as v]
            [quotes.import :as i]))

(def db-uri "datomic:mem://quotes")

(def schema
  [{:db/id (d/tempid :db.part/db)
    :db/ident :content
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/one
    :db.install/_attribute :db.part/db}
   {:db/id (d/tempid :db.part/db)
    :db/ident :author
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/one
    :db.install/_attribute :db.part/db}
   {:db/id (d/tempid :db.part/db)
    :db/ident :image
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/one
    :db.install/_attribute :db.part/db}
   {:db/id (d/tempid :db.part/db)
    :db/ident :quote/tags
    :db/valueType :db.type/ref
    :db/cardinality :db.cardinality/many
    :db.install/_attribute :db.part/db}
   {:db/id (d/tempid :db.part/db)
    :db/ident :tag
    :db/index true
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/one
    :db.install/_attribute :db.part/db}])

(defn conn
  []
  (d/connect db-uri))

(defn db
  []
  (d/db (conn)))

(defn entity
  [id]
  (d/entity (db) id))

(defn transact
  [lists]
  (d/transact (conn) lists))

(defn transact-schema
  []
  (d/create-database db-uri)
  (transact schema))

(comment
  (require '[quotes.import :refer [import-quotes]])
  (transact (import-quotes))
  )

(defn add-quote
  [content author image]
  (transact [{:db/id #db/id[:db.part/user]
              :content content
              :author author
              :image image}]))

(defn get-quotes
  []
  (->> (d/q '[:find ?q :where [?q :author]]
            (db))
       shuffle
       (take 10)
       (map first)
       (map entity)))

(defn search-tags
  [search]
  (->> (d/q '[:find ?t :in $ ?pattern :where
              [?t :tag ?tag]
              [(re-find ?pattern ?tag)]]
            (db)
            (re-pattern (str "(?i)\\b" search)))
       (map first)
       (map entity)))

(defn get-quotes-by-tag
  [tag]
  (-> (d/q '[:find ?t :in $ ?s :where [?t :tag ?s]] (db) tag)
      ffirst
      entity
      :quote/_tags))

(defn layout
  [content]
  (html (v/layout content)))

(defn render-quotes-remote
  []
  (html (v/render-quotes (get-quotes))))

(defn add-quote-form-page
  []
  (layout (v/add-quote-form)))

(defn process-quote-form
  [{:keys [content author image]}]
  (add-quote content author image)
  (redirect "/"))

(defn home
  [{:keys [search]}]
  (layout
   (list
    (v/search-tag-form search)
    (v/render-quotes
     (if search
       (->> (search-tags search)
            (map :quote/_tags)
            (mapcat identity)
            (shuffle))
       (get-quotes))))))

(defroutes routes
  (GET "/" {params :params} (home params))
  (GET "/add-quote" [] (add-quote-form-page))
  (POST "/add-quote" {params :params} (process-quote-form params)))

(def handler
  (-> #'routes
      site
      (wrap-resource "public")
      (wrap-file-info)))

(defonce server-process (atom nil))

(defn stop-server!
  []
  (when-let [s @server-process]
    (.stop s)
    (reset! server-process nil)))

(defn start-server!
  []
  (stop-server!)
  (transact-schema)
  (reset! server-process (serve handler {:port 3000 :open-browser? false :join? false})))

(defn -main
  [& args]
  (start-server!)
  (transact (i/import-quotes)))
