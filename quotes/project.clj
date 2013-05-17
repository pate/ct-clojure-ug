(defproject quotes "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [ring-server "0.2.8"]
                 [hiccup "1.0.1"]
                 [compojure "1.1.5"]
                 [org.clojure/data.csv "0.1.2"]
                 [com.datomic/datomic-free "0.8.3952"]]
  :main quotes.core
  :profiles {:dev {:plugins [[lein-cljsbuild "0.3.2"]]}}
  :cljsbuild {:builds
              [{:id "debug"
                :source-paths ["src-cljs"]
                :compiler
                {:pretty-print true
                 :output-to "resources/public/js/debug.js"
                 :optimizations :whitespace}
                :jar true}
               {:id "advanced"
                :source-paths ["src-cljs"]
                :compiler
                {:pretty-print false
                 :output-to "resources/public/js/advanced.js"
                 :optimizations :advanced}}]
              :crossover-jar false})

