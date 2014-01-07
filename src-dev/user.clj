(ns user
  (:require [clojure.repl :refer [doc source]]
            [clojure.pprint :refer [pprint]]
            [clojure.string :as str]
            [clojure.tools.namespace.repl :refer (refresh refresh-all)]
            [midje.repl :refer [load-facts check-facts]]
            [diffbot.core :refer (call-api)]))

(def token "dev-token")

(defn example []
  (call-api token "http://google.com"))

(defn example-fields []
  (call-api token "http://danmidwood.com"
            :fields ["meta" "querystring" "images(*)"]))
