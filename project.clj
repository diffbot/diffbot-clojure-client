(defproject com.diffbot/diffbot "0.1.0-SNAPSHOT"
  :description "Clojure interface to the Diffbot API"
  :url "http://www.diffbot.com"
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [clj-http "0.7.8"]]
  :profiles {:dev {:source-paths ["src-dev"]
                   :dependencies [[midje "1.6.0"]
                                  [org.clojure/tools.namespace "0.2.4"]]
                   :plugins  [[lein-midje "3.0.0"]]}})
