(ns quotes.views
  (:require [compojure.core :as compojure :refer [defroutes GET POST]]
            [hiccup.core :as h]
            [ring.util.response :as response]))

(defonce quotes (atom []))

(defn add-quote
  [content author image]
  (swap! quotes conj {:content content :author author :image image}))

(defn layout
  [content]
  (h/html
   [:html
    [:head]
    [:body
     [:p
      [:a {:href "/quotes"} "Quotes"]
      " "
      [:a {:href "/add-quote"} "Add quote"]]
     content]]))

(defn home
  []
  (layout [:h1 "Hello, Cape Town!"]))

(defn add-quote-form
  []
  (layout
   [:form {:action "/add-quote" :method "POST"}
    [:div
     [:label {:for "content"} "Quote"]]
    [:div
     [:textarea {:id "content" :name "content" :rows 4 :cols 50}]]
    [:div
     [:label {:for "author"} "Author"]]
    [:div
     [:input {:id "author" :name "author" :type "text"}]]
    [:div
     [:label {:for "image"} "Author picture url"]]
    [:div
     [:input {:id "image" :name "image" :type "text"}]]
    [:button "Save"]]))

(defn process-quote-form
  [{:keys [content author image]}]
  (add-quote content author image)
  (response/redirect "/quotes"))

(defn view-quotes
  []
  (layout
   (for [{:keys [content author image]} @quotes]
     (list [:blockquote
            [:p content]
            [:p
             (when image
               (list [:img {:src (str "/images/" image)}]
                     " "))
             author]]))))

(defroutes routes
  (GET "/" [] (home))
  (GET "/quotes" [] (view-quotes))
  (GET "/add-quote" [] (add-quote-form))
  (POST "/add-quote" {params :params} (process-quote-form params)))

