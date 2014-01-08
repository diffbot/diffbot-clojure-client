(ns diffbot.core-test
  "All tests set the api-url to localhost to avoid calling spamming the diffbot servers."
  (import [java.net URLEncoder])
  (:require [midje.sweet :refer :all]
            [clj-http.fake :refer :all]
            [diffbot.core :refer :all]
            [clojure.string :as str]
            [diffbot.stock :refer [article-response frontpage-response image-response product-response]]
            [slingshot.slingshot :refer [try+]]))

(def ^:private token "token")

(defn build-matchers [& keyvals]
  (let [build (fn [[[call-type url] response]]
                [{:address (str "http://localhost/v2/" call-type)
                  :query-params {:token "token"
                      :url url}}
                  (fn [_] response)])]
    (into {}
          (map build (apply hash-map keyvals)))))



(facts "Successful analysis"
      (with-fake-routes
         (build-matchers ["article" "http://blog.diffbot.com/diffbots-new-product-api-teaches-robots-to-shop-online/"] article-response
                         ["frontpage" "http://huffingtonpost.com/"] frontpage-response
                         ["image" "http://www.statesymbolsusa.org/National_Symbols/National_flower.html"] image-response
                         ["product" "http://www.overstock.com/Home-Garden/iRobot-650-Roomba-Vacuuming-Robot/7886009/product.html"] product-response)

         (let [analysis (article token
                                 "http://blog.diffbot.com/diffbots-new-product-api-teaches-robots-to-shop-online/"
                                 :api-url "localhost")]
           analysis => map?
           (every? (comp not nil?)
                   (map analysis [:html :text :date :supertags :author :cid
                                  :title :date_created :url :categories :type])) => true)

         (let [analysis (frontpage token
                                 "http://huffingtonpost.com/"
                                 :api-url "localhost")]
           analysis => map?
           (every? (comp not nil?)
                   (map analysis [:icon :title :url :sections])) => true)

         (let [analysis (image token
                                 "http://www.statesymbolsusa.org/National_Symbols/National_flower.html"
                                 :api-url "localhost")]
           analysis => map?
           (every? (comp not nil?)
                   (map analysis [:title :images :date_created :type :url])) => true)

         (let [analysis (product token
                                 "http://www.overstock.com/Home-Garden/iRobot-650-Roomba-Vacuuming-Robot/7886009/product.html"
                                 :api-url "localhost")]
           analysis => map?
           (every? (comp not nil?)
                   (map analysis [:leafPage :date_created :type :products :url])) => true)))



(facts "Unsuccessful API calls throw slingshots"
      (with-fake-routes
         (build-matchers ["article" "http://blog.diffbot.com/diffbots-new-product-api-teaches-robots-to-shop-online/"] {:status 500 :headers {"bla" "bla2"} :body "error"})

         (try+
           (article token
                                 "http://blog.diffbot.com/diffbots-new-product-api-teaches-robots-to-shop-online/"
                                 :api-url "localhost")
           (catch [:status 500] e
             (:body e) => "error"
             (:headers e) => {"bla" "bla2"})))



      (article token "without-a-valid-matcher" :api-url "localhost")
      => (throws java.net.ConnectException)

)
