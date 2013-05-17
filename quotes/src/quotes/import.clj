(ns quotes.import
  (:gen-class)
  (:require [datomic.api :as d]
            [clojure.string :as string]
            [clojure.data.csv :refer [read-csv]]
            [clojure.java.io :refer [reader]]))

(def tag-txes* (atom {}))

(defn add-tag-tx
  [tag]
  (let [tag-tx {:db/id (d/tempid :db.part/user)
                :tag tag}]
    (swap! tag-txes* assoc tag tag-tx)
    tag-tx))

(defn id-for-tag
  [tag]
  (:db/id (or (get @tag-txes* tag)
              (add-tag-tx tag))))

(defn find-or-make-tags
  [tags]
  (->> tags
       read-csv
       first
       (map string/trim)
       (map id-for-tag)))

(defn import-quotes
  []
  (reset! tag-txes* {})
  (let [csv-data (read-csv (slurp "resources/quotes.csv"))
        quote-txes (doall (for [[_ author content image tags _] csv-data]
                            {:db/id (d/tempid :db.part/user)
                             :content content
                             :author author
                             :image image
                             :quote/tags (doall (find-or-make-tags tags))}))
        tag-txes (vals @tag-txes*)]
    (reset! tag-txes* {})
    (concat quote-txes tag-txes)))


