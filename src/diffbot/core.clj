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
  (-> (format "http://%s/%s/%s?token=%s&url=%s"
              (or api-url "api.diffbot.com")
              (or api-version "v2")
              call-type
              token
              (URLEncoder/encode url))
      (add-fields fields)
      (add-time-out timeout)
      (add-callback callback)))

(defn call-api [url req content]
  (println url)
  (if content
    (client/post url (merge {:as :json
                             :body content
                             :content-type :html}
                            req))
    (client/get url (merge {:as :json}
                           req))))

(defmacro defapi [name documentation]
  (let [stock-doc "The Diffbot API is fairly structured with two mandatory parameters and three optional. Mandatory:
  * token - to access the API
  * url - to parse
  Optional:
  * callback - for JSONP requests
  * timeout - in ms, default is no timeout
  * fields - a list of fields to be returned in the response."]
    `(defn ~name ~(str documentation \newline \newline stock-doc) [token# url# & opts#]
       (let [opts-map# (apply hash-map opts#)]
         (-> (apply (partial build-request-url (str '~name) token# url#) opts#)
             (call-api (:req opts-map#) (:content opts-map#))
             :body)))))

(defapi article
  "Extract clean article text from news article, blog post and similar text-heavy web pages.
The list of available fields can be found at http://www.diffbot.com/products/automatic/article/")

(defapi frontpage
  "Take in a multifaceted “homepage” and returns individual page elements.
The list of available fields can be found at http://www.diffbot.com/products/automatic/frontpage/")

(defapi product
  "Analyze a shopping or e-commerce product page and returns information on the product.
The list of available fields can be found at http://www.diffbot.com/products/automatic/product/")

(defapi image
  "Analyze a web page and returns its primary image(s).
The list of available fields can be found at http://www.diffbot.com/products/automatic/image/")
