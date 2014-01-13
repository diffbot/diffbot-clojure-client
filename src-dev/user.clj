(ns user
  (:require [clojure.repl :refer [doc source]]
            [clojure.pprint :refer [pprint]]
            [clojure.string :as str]
            [clojure.tools.namespace.repl :refer (refresh refresh-all)]
            [midje.repl :refer [load-facts check-facts]]
            [diffbot.core :refer [article frontpage product image]]
            [diffbot.crawlbot :refer [make-crawl get-crawl pause resume restart delete get-crawl get-crawl-data get-crawl-urls]]))
