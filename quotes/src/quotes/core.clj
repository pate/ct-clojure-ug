(ns quotes.core
  (:gen-class)
  (:require [ring.server.standalone :refer [serve]]
            [quotes.views :as v]))

(def handler
  v/routes)

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

(defn restart-server!
  []
  (stop-server!)
  (start-server!))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  ;; work around dangerous default behaviour in Clojure
  (alter-var-root #'*read-eval* (constantly false))
  (start-server!))

