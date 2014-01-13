(ns diffbot.crawlbot
  (import [java.net URLEncoder])
  (:require [clj-http.client :as client]
            [diffbot.core :refer [build-request-url]]))

(defn ^:private add-fields [url fields]
  (if fields
    (str url "&fields=" (clojure.string/join "," fields))
    url))

(defn ^:private add-time-out [url time-out]
  (if time-out
    (str url "&timeout=" time-out)
    url))

(defn ^:private add-callback [url callback]
  (if callback
    (str url "&callback=" callback)
    url))

(defn add-api-url [url api-url-type]
  (if api-url-type
    (str url "&apiUrl=" api-url-type)
    url))

(defn build-api-url [call-type {:keys [api-url api-version fields timeout callback]}]
  (when call-type
    (-> (format "http://%s/%s/%s"
                (or api-url "api.diffbot.com")
                (or api-version "v2")
                call-type)
        (add-fields fields)
        (add-time-out timeout)
        (add-callback callback)
        (URLEncoder/encode))))

(defn to-api-key [k]
  (-> (name k)
      (clojure.string/replace #"-(\w)"
                              #(clojure.string/upper-case (second %1)))))

(defn add-args [url args]
  (let [q-params (for [[k v] args
                       :when k
                       :when v]
                   (if (or (string? v)
                           (number? v))
                      (str (to-api-key k) "=" v)
                      (str (to-api-key k) "=" (clojure.string/join "||" v))))]
    (str url "&" (clojure.string/join "&" q-params))))

(defn add-urls [url urls]
  (if urls
    (->> (clojure.string/join " " urls)
         URLEncoder/encode
         (str url "&seeds="))
    url))

(defn build-crawl-url [call-type urls token & {:keys [api-url api-version analyze-opts] :as opts}]
  (-> (format "http://%s/%s/crawl?token=%s"
              (or api-url "api.diffbot.com")
              (or api-version "v2")
              token)
      (add-api-url (build-api-url call-type (merge opts analyze-opts)))
      (add-urls urls)
      (add-args (dissoc opts :req :api-url :api-version :analyze-opts))))

(defn make-crawl [name crawl-type urls token & crawl-opts]
  (clojure.pprint/pprint crawl-opts)
  (let [crawl-url (apply build-crawl-url crawl-type urls token :name name crawl-opts)
        req nil]
    (-> (client/get crawl-url (merge {:as :json}
                                     req))
        :body)))

(defn pause [name token]
  (make-crawl name nil nil token :pause 1))

(defn resume [name token]
  (make-crawl name nil nil token :pause 0))

(defn restart [name token]
  (make-crawl name nil nil token :restart 1))

(defn delete [name token]
  (make-crawl name nil nil token :delete 1))

(defn get-crawl
  ([name token]
     (make-crawl nil nil nil token))
  ([token] (get-crawl nil token)))

(defn ^:private read-data [type name token]
  (client/get (format "http://api.diffbot.com/v2/crawl/download/%s-%s_%s"
                      token
                      name
                      type)))

(defn crawl-data [name token]
  (read-data "data.json" name token))

(defn crawl-urls [name token]
  (read-data "urls.csv" name token))
