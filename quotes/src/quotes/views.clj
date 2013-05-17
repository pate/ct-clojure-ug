(ns quotes.views)

(defn layout
  [content]
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
    [:script {:src "/js/debug.js"}]]])

(defn render-tag
  [tag]
  [:a {:href (str "/?search=" tag)} tag])

(defn render-tags
  [tags]
  (->> (map :tag tags)
       shuffle
       (take 5)
       (map render-tag)))

(defn render-quote
  [{:keys [content author image tags] :as quote}]
  [:blockquote
   [:p.quote  "&ldquo;" content "&rdquo;"]
   [:p.tags (render-tags (:quote/tags quote))]
   [:p
    [:img {:src (str "/images/" image)}]
    author]])

(defn render-quotes
  [quotes]
  (map render-quote quotes))

(defn add-quote-form
  []
  [:form {:action "/add-quote" :method "POST"}
   [:fieldset [:legend "Add Quote"]
    [:label {:for "content"} "Quote"]
    [:textarea.input-xlarge {:id "content" :name "content"}]
    [:label {:for "author"} "Author"]
    [:input.input-xlarge {:id "author" :name "author" :type "text"}]
    [:label {:for "image"} "Author picture file name"]
    [:input.input-xlarge {:id "image" :name "image" :type "text"}]]
   [:button.btn-primary {:type "submit"} "Save"]])

(defn search-tag-form
  [search]
  [:form {:action "/" :method "GET"}
   [:fieldset [:legend "Search tags"]
    [:input.input-xlarge {:name "search" :type "text" :value search}]]
   [:button.btn-primary {:type "submit"} "Search"]])
