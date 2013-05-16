(ns quotes.views
  (:require [compojure.core :as compojure :refer [defroutes GET]]))

(defn home
  []
  "<h1>Hello, Cape Town!</h1>")

(defroutes routes
  (GET "/" [] (home)))

