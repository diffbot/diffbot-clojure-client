(ns diffbot.core
  (import [java.net URLEncoder])
  (:require [clj-http.client :as client]))

(defn ^:private add-fields [url fields]
  (if fields
    (str url "&fields=" (URLEncoder/encode (clojure.string/join "," fields)))
    url))

(defn ^:private add-time-out [url time-out]
  (if time-out
    (str url "&timeout=" time-out)
    url))

(defn ^:private add-callback [url callback]
  (if callback
    (str url "&callback=" callback)
    url))

(defn build-request-url [call-type token url & {:keys [api-url api-version fields timeout callback]}]
  "The Diffbot API is fairly structured with two mandatory parameters and three optional. Mandatory:
  * token - to access the API
  * url - to parse
  Optional:
  * callback - for JSONP requests
  * timeout - in ms, default is no timeout
  * fields - a list of fields to be returned in the response.

  The accepted fields depends on the choice of API in `call-type`."
  (-> (format "http://%s/%s/%s?token=%s&url=%s"
              (or api-url "api.diffbot.com")
              (or api-version "v2")
              call-type
              token
              (URLEncoder/encode url))
      (add-fields fields)
      (add-time-out timeout)
      (add-callback callback)))

(defn ^:private call-api [call-type token url & opts]
  (-> (apply (partial build-request-url call-type token url) opts)
      (client/get (merge {:as :json}
                         (:req (apply hash-map opts))))
      :body))

(def article
  (partial call-api "article"))

(def frontpage
  ; The documention for frontpage shows a differant url (diffbot.com/api instead
  ; of api.diffbot.com/v2) and returning DML (XML) instead of JSON. However,
  ; this does work with the v2 url and retuns valid JSON.
  ; TODO confirm whether v2 JSON suceeds the DML response
  (partial call-api "frontpage"))

(def product
  (partial call-api "product"))

(def image
  "doc"
  (partial call-api "image"))
