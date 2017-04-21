(ns the-party.dng)

(def seed {:startx 0
           :starty 0
           :stopx 79
           :stopy 25
           :lneft nil
           :right nil})

(defn leaf? [node]
  (and (nil? (:left node)) (nil? (:right node))))

(defn split-node [node]
  (let [dice (rand-int 0 100)]))

(defn heads? [num]
  (< 50 num))

(defn tails? [num]
  (>= 50 num))
