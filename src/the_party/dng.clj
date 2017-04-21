(ns the-party.dng
  (:require [clojure.math.numeric-tower :as math]))

(def MAX_SPLITS 4)
(def MIN_ROOM_SIZE 4)

(def seed {:startx 0
           :starty 0
           :stopx 79
           :stopy 25
           :left nil
           :right nil})

(defn leaf? [node]
  (and (nil? (:left node)) (nil? (:right node))))

(defn split-node [node height]
  (if (> height MAX_SPLITS)
    node
    (let [splitfn (fn [start stop]
                    (loop [split-at start]
                      (if (or (< (math/abs (- split-at start)) 3) (< (math/abs (- (+ split-at 1) stop)) 3))
                        (recur (+ start (rand-int (- stop start))))
                        split-at)))
          dice (rand-int 100)
          mode (if (heads? dice) :vertical :horizontal)
          [start stop] (if (= mode :vertical) [:starty :stopy] [:startx :stopx])
          split-at (splitfn (start node) (stop node))]
      (let [left (assoc node stop split-at)
            right (assoc node start (+ split-at 1))]
        (-> node
            (assoc :left left)
            (assoc :right right))))))

(defn heads? [num]
  (< 50 num))

(defn tails? [num]
  (>= 50 num))
