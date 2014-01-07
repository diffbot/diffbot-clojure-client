(ns diffbot.requests
  (:require [midje.sweet :refer :all]
            [clj-http.fake :refer :all]
            [clojure.string :as str]
            [clojurewerkz.urly.core :refer [url-like protocol-of host-of path-of query-of]]
            [diffbot.core :refer [build-request-url]]))

(facts "Building requests"
       (fact "The protocol is http"
             (-> (build-request-url "article" "token" "http://www.diffbot.com")
                 url-like
                 protocol-of)
             => "http")
       (fact "The default host is diffbot"
             (-> (build-request-url "article" "token" "http://www.diffbot.com")
                 url-like
                 host-of)
             => "api.diffbot.com")
       (fact "The host uri can be overridden"
             (-> (build-request-url "article" "token" "http://www.diffbot.com" :api-url "localhost")
                 url-like
                 host-of)
             => "localhost")
       (fact "The default api version is 2"
             (-> (build-request-url "article" "token" "http://www.diffbot.com")
                 url-like
                 path-of)
             => "/v2/article")
       (fact "The api version can be overridden"
             (-> (build-request-url "article" "token" "http://www.diffbot.com" :api-version "v3")
                 url-like
                 path-of)
             => "/v3/article")
)

(defn split-query-string [query-string]
  (apply hash-map (str/split query-string #"[&|=]")))

(facts "Mandatory parameters"
       (fact "The token is added as a query parameter"
             (-> (build-request-url "article" "some-token" "http://www.diffbot.com")
                 url-like
                 query-of
                 split-query-string
                 (get-in ["token"]))
             => "some-token")
       (fact "The url is added as a query parameter"
             (-> (build-request-url "article" "token" "http://www.diffbot.com")
                 url-like
                 query-of
                 split-query-string
                 (get-in ["url"]))
             => "http://www.diffbot.com"))

(facts "Optional parameters"
       (fact "A callback can be added as a query parameter"
             (-> (build-request-url "article" "token" "http://www.diffbot.com" :callback "callbackfn")
                 url-like
                 query-of
                 split-query-string
                 (get-in ["callback"]))
             => "callbackfn")
       (fact "Not adding a callback doesn't add a query string"
             (-> (build-request-url "article" "token" "http://www.diffbot.com")
                 url-like
                 query-of
                 split-query-string
                 (get-in ["callback"]))
             => nil)
       (fact "A timeout can be added as a query parameter"
             (-> (build-request-url "article" "token" "http://www.diffbot.com" :timeout 100)
                 url-like
                 query-of
                 split-query-string
                 (get-in ["timeout"]))
             => "100")
       (fact "Not adding a timeout doesn't add a query string"
             (-> (build-request-url "article" "token" "http://www.diffbot.com")
                 url-like
                 query-of
                 split-query-string
                 (get-in ["timeout"]))
             => nil)
       (fact "A list of fields can be added as a query parameter - one field"
             (-> (build-request-url "article" "token" "http://www.diffbot.com" :fields ["a-field"])
                 url-like
                 query-of
                 split-query-string
                 (get-in ["fields"]))
             => "a-field")
       (fact "Multiple fields are comma separated"
             (-> (build-request-url "article" "token" "http://www.diffbot.com" :fields ["a-field" "b-field" "c-field"])
                 url-like
                 query-of
                 split-query-string
                 (get-in ["fields"]))
             => "a-field,b-field,c-field")
       (fact "Fields can contain wildcards and parems"
             (-> (build-request-url "article" "token" "http://www.diffbot.com" :fields ["a-field" "b-field(b-1,b-2)" "c-field(*)"])
                 url-like
                 query-of
                 split-query-string
                 (get-in ["fields"]))
             => "a-field,b-field(b-1,b-2),c-field(*)")
       (fact "Not adding any fields doesn't add a query string"
             (-> (build-request-url "article" "token" "http://www.diffbot.com")
                 url-like
                 query-of
                 split-query-string
                 (get-in ["fields"]))
             => nil))
