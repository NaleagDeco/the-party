(defproject the-party "0.1.0-SNAPSHOT"
  :description "A non-combat roguelike about social anxiety"
  :url "http://example.com/FIXME"
  :license {:name "MIT License"
            :url "http://opensource.org/licenses/MIT"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [clojure-lanterna "0.9.4"]
                 [reagi "0.10.1"]]
  :main ^:skip-aot the-party.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
