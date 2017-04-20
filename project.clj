(defproject the-party "0.1.0-SNAPSHOT"
  :description "A non-combat roguelike about social anxiety"
  :url "http://example.com/FIXME"
  :license {:name "MIT License"
            :url "http://opensource.org/licenses/MIT"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/core.async "0.3.442"]
                 [clojure-lanterna "0.9.7"]
                 [reagi "0.10.1" :exclusions [org.clojure/clojure]]
                 [org.clojure/math.numeric-tower "0.0.4"]]
  :main ^:skip-aot the-party.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
