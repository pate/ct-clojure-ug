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
    [:head
     [:link {:rel "stylesheet" :href "/bootstrap/css/bootstrap.min.css"}]
     [:style "
img { width: 50px; height: 50px; margin: 0 0.5em; }
.row-fluid {margin-top: 2em;}
.quote { margin: 1em 0;  font-size: 1.6em; font-family: serif; }"]]
    [:body
     [:div.container-fluid
      [:div.row-fluid
       [:div.btn-group
        [:a.btn {:href "/"} "Quotes"]
        [:a.btn {:href "/add-quote"} "Add quote"]]]
      [:div.row-fluid content]]]]))

(defn home
  []
  (layout
   (for [{:keys [content author image]} @quotes]
     [:blockquote
      [:p.quote "&ldquo;" content "&rdquo;"]
      [:p
       (when image
         [:img {:src (str "/images/" image)}])
       author]])))

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
  (response/redirect "/"))

(defroutes routes
  (GET "/" [] (home))
  (GET "/add-quote" [] (add-quote-form))
  (POST "/add-quote" {params :params} (process-quote-form params)))

