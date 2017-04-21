(ns the-party.builders
  (:refer-clojure :exclude [empty])
  (:require [clojure.java.io :as io]
            [the-party.people :as people]
            [the-party.dng :as dng]))

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

(defn tree->terrain []
  (let [leafs (flatten (dng/leafs (dng/split-node dng/seed)))
        empty-map
        (into {} (map (fn [k] {k :inaccessible}) (for [r (range 25) c (range 80)] (vector r c))))]
    (loop [l leafs
           floor empty-map]
      (if (empty? l)
        floor
        (let [leaf (first l)
              topwall (for [r (list (:starty leaf)) c (range (:startx leaf) (+ (:stopx leaf) 1))] (vector r c))
              bottomwall (for [r (list (:stopy leaf)) c (range (:startx leaf) (+ (:stopx leaf) 1))] (vector r c))
              leftwall (for [c (list (:startx leaf)) r (range (:starty leaf) (+ (:stopy leaf) 1))] (vector r c))
              rightwall (for [c (list (:stopx leaf)) r (range (:starty leaf) (+ (:stopy leaf) 1))] (vector r c))
              innards (for [r (range (inc (:starty leaf)) (:stopy leaf)) c (range (inc (:startx leaf)) (:stopx leaf))] (vector r c))
              floort (reduce #(assoc %1 %2 :horizontal-wall) floor topwall)
              floorb (reduce #(assoc %1 %2 :horizontal-wall) floort bottomwall)
              floorl (reduce #(assoc %1 %2 :vertical-wall) floorb leftwall)
              floorr (reduce #(assoc %1 %2 :vertical-wall) floorl rightwall)
              floorf (reduce #(assoc %1 %2 :empty-space) floorr innards)]
          (recur (rest l)
                 floorf))))))

(defn empty [terrain]
  (map first (filter #(= (second %) :empty-space) terrain)))

(defn generate-people [terrain num]
  (let [selections  (->> terrain empty shuffle (take num))]
    (zipmap selections (repeatedly #(people/bro)))))

(defn populate-ladders [terrain]
  (let [selections (->> terrain empty shuffle (take 2))]
    (-> terrain
        (assoc (first selections) :up-ladder)
        (assoc (second selections) :down-ladder))))
