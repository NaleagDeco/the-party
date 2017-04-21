(ns the-party.dng
  (:require [clojure.math.numeric-tower :as math]))

(def MIN_ROOM_SIZE 4)

(def seed {:startx 0
           :starty 0
           :stopx 79
           :stopy 25
           :left nil
           :right nil})

(defn leaf? [node]
  (and (nil? (:left node)) (nil? (:right node))))

(defn heads? [num]
  (< 50 num))

(defn tails? [num]
  (>= 50 num))


(defn split-node
  [node]
  (let [splitfn (fn [start stop]
                  (loop [split-at start
                         rounds 0]
                    (cond (> rounds 3) nil
                          (or (< (math/abs (- split-at start)) MIN_ROOM_SIZE) (< (math/abs (- (+ split-at 1) stop)) MIN_ROOM_SIZE))
                          (recur (+ start (rand-int (- stop start)))
                                 (inc rounds))
                          :else split-at)))
        smallx? (< (math/abs (- (:stopx node) (:startx node))) MIN_ROOM_SIZE)
        smally? (< (math/abs (- (:stopy node) (:startx node))) MIN_ROOM_SIZE)
        mode (cond (and smallx? smally?) :dead
                   smallx? :vertical
                   smally? :horizontal
                   :else (if (heads? (rand-int 100)) :vertical :horizontal))]
    (if (= mode :done)
      nil
      (let [[start stop] (if (= mode :vertical) [:starty :stopy] [:startx :stopx])
            split-at (splitfn (start node) (stop node))]
        (if (nil? split-at)
          node
          (let [left (assoc node stop split-at)
                right (assoc node start (+ split-at 1))]
            (-> node
                (assoc :left (split-node left))
                (assoc :right (split-node right)))))))))

(defn whittle-tree [tree]
  (if (and (nil? (:left tree)) (nil? (:right tree)))
    tree
    (-> tree
        (assoc :left (whittle-tree (:left tree)))
        (assoc :right (whittle-tree (:right tree))))))

(defn leafs [tree]
  (if (leaf? tree)
    [tree]
    [(leafs (:left tree)) (leafs (:right tree))]))
