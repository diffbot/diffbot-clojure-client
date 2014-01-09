# Clojure Diffbot API

A Clojure library for working with the [Diffbot](http://www/diffbot.com) [API v2](http://www.diffbot.com/products/automatic/).

## Installation

Add `[com.diffbot/diffbot "0.1.0"]` to your Leiningen project `:dependencies`.

## Usage

Diffbot has four API calls that can be accessed by functions in the `diffbot.core` namespace, these are `article`, `frontpage`, `product` and `image` and correspond to the identically name Diffbot APIs.

Require the functions in the REPL:

```clojure
user> (require '[diffbot.core :refer [article frontpage product image]])
```

Require in your namespace

```clojure
(ns app.core
    (:require [diffbot.core :refer [article frontpage product image]]))
```

All function calls require a Diffbot API token ([sign up](http://www.diffbot.com/pricing) to get one) and the url you want to parse. The Diffbot responses are parsed and returned as Clojure maps.

An example article response, pretty printed for readability:

```clojure
user> (article token "http://blog.diffbot.com/diffbots-new-product-api-teaches-robots-to-shop-online/")
{:html
 "<div><p>Diffbot&rsquo;s human wranglers are proud today to announce the release of our newest product: an API for&hellip; products!</p><p>The&nbsp;<a href=\"http://www.diffbot.com/products/automatic/product\" title=\"Diffbot's Product API\">Product API</a>&nbsp;can be used for extracting clean, structured data from any e-commerce product page. It&nbsp;automatically makes available all the product data you&rsquo;d expect: price, discount/savings amount, shipping cost, product description, any relevant product images, SKU and/or other product IDs.</p><p>Even cooler: pair the Product API with <a href=\"http://www.diffbot.com/products/crawlbot\" title=\"Crawlbot from Diffbot\">Crawlbot</a>, our intelligent site-spidering tool, and let Diffbot determine which pages are products, then automatically structure the entire catalog. Here&rsquo;s a quick demonstration of Crawlbot at work:</p><p>We&rsquo;ve developed the Product API over the course of two years, building upon our core vision technology that&rsquo
;s extracted structured data from billions of web pages, and training our machine learning systems using data from tens of thousands of unique shopping sites. We can&rsquo;t wait for you to try it out.</p><p>What are you waiting for? Check out the <a href=\"http://www.diffbot.com/products/automatic/product\" title=\"Diffbot's Product API\">Product API documentation</a>&nbsp;and dive on in! If you need a token, check out our <a href=\"http://www.diffbot.com/pricing\">pricing and plans</a> (including our Free plan).</p><p>Questions? Hit us up at <a href=\"mailto:support@diffbot.com\">support@diffbot.com</a>.</p></div>",
 :text
 "Diffbot’s human wranglers are proud today to announce the release of our newest product: an API for… products!\nThe Product API can be used for extracting clean, structured data from any e-commerce product page. It automatically makes available all the product data you’d expect: price, discount/savings amount, shipping cost, product description, any relevant product images, SKU and/or o
ther product IDs.\nEven cooler: pair the Product API with Crawlbot , our intelligent site-spidering tool, and let Diffbot determine which pages are products, then automatically structure the entire catalog. Here’s a quick demonstration of Crawlbot at work:\nWe’ve developed the Product API over the course of two years, building upon our core vision technology that’s extracted structured data from billions of web pages, and training our machine learning systems using data from tens of thousands of unique shopping sites. We can’t wait for you to try it out.\nWhat are you waiting for? Check out the Product API documentation and dive on in! If you need a token, check out our pricing and plans (including our Free plan).\nQuestions? Hit us up at support@diffbot.com .",
 :human_language "en",
 :date "Wed, 31 Jul 2013 07:00:00 GMT",
 :supertags
 [{:depth 0.6470588235294117,
   :senseRank 3,
   :score 0.7,
   :name "Online game",
   :positions [[49 55]],
   :categories
   {:2087401 "Online games", :24212208 "Video game
 terminology"},
   :type 1,
   :contentMatch 0.6543209876543209,
   :variety 0.6421052631578947,
   :id 1050944}
  {:depth 0.47058823529411764,
   :senseRank 1,
   :score 0.6,
   :name "Intrusion detection system",
   :positions [[458 461]],
   :categories {:30869856 "Intrusion detection systems"},
   :type 1,
   :contentMatch 0.6543209876543209,
   :variety 0.7789473684210526,
   :id 113021}],
 :author "John Davi",
 :cid 1227573853,
 :title "Diffbot’s New Product API Teaches Robots to Shop Online",
 :date_created "Sat, 28 Dec 2013 16:43:45 PST",
 :url "http://blog.diffbot.com/diffbots-new-product-api-teaches-robots-to-shop-online/",
 :categories
 {:sports 0.004774440201028423,
  :disaster_accident 0.005714285714285713,
  :health_medical_pharma 0.14785714285714283,
  :technology_internet 0.38092731007413366,
  :law_crime 0.05547619047619047,
  :education 0.013333333333333332,
  :other 0.03714285714285714,
  :environment 0.04380901027166066,
  :politics 0.03619047619047619,
  :business_finance 0.00857142857142857,
  :socialissues 0.06428571428571428,
  :labor 0.061428571428571416,
  :humaninterest 0.059285714285714275,
  :religion_belief 0.014285714285714284,
  :entertainment_culture 0.012142857142857141,
  :war_conflict 0.0019178108817486923,
  :weather 0.014285714285714284,
  :hospitality_recreation 0.03857142857142856},
 :type "article"}
```

### Optional parameters

There are three optional parameters that can be passed as keyword arguments

* `timeout` Set a timeout in ms, the default is no timeout
* `callback` A callback function for jsonp requests.
* `fields` A list of fields that should be returned in the response.

The available `fields` deponds on the API that is being called, for full reference see the Diffbot API documentation for [article](http://www.diffbot.com/products/automatic/article/), [frontpage](http://www.diffbot.com/products/automatic/frontpage/), [product](http://www.diffbot.com/products/automatic/product/) and [image](http://www.diffbot.com/products/automatic/image/).


```clojure
user> (article token
               "http://blog.diffbot.com/diffbots-new-product-api-teaches-robots-to-shop-online/"
               :timeout 5000
               :fields ["meta", "querystring", "images(*)"])
```


### Analyzing content behind a firewall

To analyze urls that are not publicly available, the content can be specified explicitly using the optional `:content` keyword argument.

```clojure
user> (article token
               "http://www.diffbot.com/our-apis/article"
               :content "Now is the time for all good robots to come to the aid of their-- oh never mind, run!")
{:author "", :text "Now is the time for all good robots to come to the aid of their-- oh never mind, run!", :title "Now is the time for all good robots to come to the aid of their-- oh never mind, run!", :html "<div><body>Now is the time for all good robots to come to the aid of their-- oh never mind, run!\n</body></div>", :type "article", :url "http://www.diffbot.com/our-apis/article"}
```

### clj-http

This library delegates to [clj-http](https://github.com/dakrone/clj-http) to make http requests, configuration can be passed directly through using a `:req` keyword argument.

As an example, to include the entire response in exception cases forward through the following:

```clojure
user> (article token
               "http://blog.diffbot.com/diffbots-new-product-api-teaches-robots-to-shop-online/"
               :req {:throw-entire-message? true})
```

### Exception handling

`clj-http` throws [Slingshot](https://github.com/scgilardi/slingshot) stones on exception cases, these can be caught and matched against.

```clojure
(try+
    (article token "http://blog.diffbot.com/diffbots-new-product-api-teaches-robots-to-shop-online/")
    (catch [:status 500] e
        ;handle 500
    )
    (catch [:status 401]
        ; handle auth error
    )
)
```

Alternatively, on exceptional cases the response body can be returned directly by instructing clj-http not to throw exceptions.

```clojure
(article token
         "http://blog.diffbot.com/diffbots-new-product-api-teaches-robots-to-shop-online/"
         :req {:throw-exceptions false})
```

## License

TBD
