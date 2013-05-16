(ns quotes.core
  (:gen-class)
  (:require [ring.server.standalone :refer [serve]]
            [compojure.handler :as handler]
            [ring.middleware.resource :refer [wrap-resource]]
            [quotes.views :as v]))

(def handler
  (-> #'v/routes
      handler/site
      (wrap-resource "public")))

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
