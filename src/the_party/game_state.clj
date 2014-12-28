(ns the-party.game-state
  (:require [clojure.java.io :as io])
  (:require [the-party.builders :as builder])
  (:require [clojure.math.numeric-tower :refer [ceil]]))


(def directions
  [[-1, -1], [-1, 0], [-1, 1],
   [0, -1], [0, 0], [0, 1],
   [1, -1], [1, 0], [1, 1]])

(defn inaccessible? [tile]
  (not-any? #(= tile %) '(:empty-space :passage :open-door)))

(defn create [player]
  (let [terrain (builder/file->terrain (io/resource "map.txt"))
        player-coords (-> terrain builder/empty rand-nth)]
    {:player player
     :terrain terrain
     :people (builder/generate-people terrain 20)
     :player-coords player-coords
     :status "Welcome to The Party!"
     :turns 0}))

(defn guest-present? [state coord]
  (contains? (state :people) coord))

(defn neighbourhood [coord]
  (map (partial map + coord) directions))

(defn guest-action [state accum old-coords guest]
  (let [terrain (state :terrain)
        tentative-coords (map + (rand-nth directions) old-coords)
        new-coords (if (or (inaccessible? (terrain tentative-coords))
                           (contains? (state :people) tentative-coords))
                     old-coords
                     tentative-coords)]
    (assoc accum new-coords guest)))

(defn guest-actions [state]
  (assoc state :people (reduce-kv (partial guest-action state) {} (state :people))))

(defn turn-tick [state]
  (assoc state :turns (-> :turns state inc)))

(defn status-msg [state msg]
  (assoc state :status msg))

(defn move-to [state new-coords]
  (assoc state :player-coords new-coords))

(defn player-move [state new-coords]
  (let [terrain (state :terrain)
        blocked (-> new-coords terrain inaccessible?)]
    (if blocked
      (status-msg state "You bump your nose into a wall.")
      (-> state
        (move-to new-coords)
        guest-actions
        (status-msg "")
        turn-tick))))

(defn converse [belligerent target]
  (let [t-comp (target :composure)
        t-stam (target :stamina)
        b-conf (belligerent :confidence)
        missed (= 1 (rand-int 2))
        damage (ceil (* (/ 100 (+ 100 t-comp)) b-conf))]
    (if missed
      target
      (assoc target :stamina (- t-stam damage)))))

(defn player-converse [state target-coords]
  (let [guests (state :people)
        other-guests (dissoc guests target-coords)
        target (converse (state :player) (guests target-coords))]
    (assoc state :people (if (>= (target :stamina) 0)
                           (assoc other-guests target-coords target)
                           other-guests))))

(defn player-action [state offset]
  (let [new-coords (map + (state :player-coords) offset)
        converse (guest-present? state new-coords)]
    (if converse
      (player-converse state new-coords)
      (player-move state new-coords))))

(defn process-input [state input]
  (case input
    :player-left (player-action state [0 -1])
    :player-right (player-action state [0 1])
    :player-up (player-action state [-1 0])
    :player-down (player-action state [1 0])))
