(ns diffbot.core
  (import [java.net URLEncoder])
  (:require [clj-http.client :as client]))

(defn add-fields [url fields]
  (if fields
    (str url "&fields=" (URLEncoder/encode (clojure.string/join "," fields)))
    url))

(defn add-time-out [url time-out]
  (if time-out
    (str url "&timeout=" time-out)
    url))

(defn add-callback [url callback]
  (if callback
    (str url "&callback=" callback)
    url))

(defn call-api [token url & {:keys [fields time-out callback]}]
  (-> (format "http://api.diffbot.com/v2/article?token=%s&url=%s"
              token
              (URLEncoder/encode url))
      (add-fields fields)
      (add-time-out time-out)
      (add-callback callback)
      client/get))
