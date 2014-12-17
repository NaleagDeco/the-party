(ns the-party.builders
  (:require [clojure.java.io :as io]))

(defn char->tile [char]
  (case char
    \| :vertical-wall
    \- :horizontal-wall
    \. :empty-space
    \space :inaccessible
    \+ :open-door
    \# :passage))

(defn file->terrain [f]
  (with-open [rdr (io/reader f)]
    (loop [line (line-seq rdr)
           row-num 0
           accum {}]
      (if (nil? line) accum
          (recur (next line)
                 (inc row-num)
                 (merge accum (zipmap
                               (map vector
                                    (repeatedly (fn [] row-num))
                                    (-> line first count range))
                               (map char->tile (first line)))))))))
