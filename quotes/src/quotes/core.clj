(ns quotes.core
  (:gen-class)
  (:require [ring.server.standalone :refer [serve]]
            [compojure.handler :refer [site]]
            [ring.middleware.file-info :refer [wrap-file-info]]
            [ring.middleware.resource :refer [wrap-resource]]
            [compojure.core :refer [defroutes GET POST]]
            [hiccup.core :refer [html]]
            [ring.util.response :refer [redirect]]))

(defonce quotes (atom []))

(defn add-quote
  [content author image]
  (swap! quotes conj {:content content :author author :image image}))

(defn layout
  [content]
  (html
   [:html
    [:head
     [:link {:rel "stylesheet" :href "/bootstrap/css/bootstrap.min.css"}]
     [:link {:rel "stylesheet" :href "/css/style.css"}]]
    [:body
     [:div.container-fluid
      [:div.row-fluid
       [:a.btn {:href "/"} "Quotes"]
       [:a.btn {:href "/add-quote"} "Add quote"]]
      [:div.row-fluid content]]
     [:script {:src "/js/debug.js"}]]]))

(defn render-quote
  [{:keys [content author image]}]
  [:blockquote
   [:p.quote  "&ldquo;" content "&rdquo;"]
   [:p
    [:img {:src (str "/images/" image)}]
    author]])

(defn home
  []
  (layout
   (map render-quote @quotes)))

(defn add-quote-form
  []
  (layout
   [:form {:action "/add-quote" :method "POST"}
    [:fieldset [:legend "Add Quote"]
     [:label {:for "content"} "Quote"]
     [:textarea.input-xlarge {:id "content" :name "content"}]
     [:label {:for "author"} "Author"]
     [:input.input-xlarge {:id "author" :name "author" :type "text"}]
     [:label {:for "image"} "Author picture file name"]
     [:input.input-xlarge {:id "image" :name "image" :type "text"}]]
    [:button.btn-primary {:type "Submit"} "Save"]]))

(defn process-quote-form
  [{:keys [content author image]}]
  (add-quote content author image)
  (redirect "/"))

(defroutes routes
  (GET "/" [] (home))
  (GET "/add-quote" [] (add-quote-form))
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
  (reset! server-process (serve handler {:port 3000 :open-browser? false :join? false})))

(defn -main
  [& args]
  (start-server!))
