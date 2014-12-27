(ns the-party.game-state
  (:require [clojure.java.io :as io])
  (:require [the-party.builders :as builder]))

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

(defn guest-action [state guest]
  (let [terrain (state :terrain)
        [old-coords entity] guest
        tentative-coords (map + (rand-nth directions) old-coords)
        new-coords (if (inaccessible? (terrain tentative-coords))
                     old-coords
                     tentative-coords)]
    [new-coords entity]))

(defn move-player [state offset]
  (let [terrain (state :terrain)
        new-coords (map + (state :player-coords) offset)
        blocked (-> new-coords terrain inaccessible?)]
    (if blocked
      (assoc state :status "You bump your nose into a wall.")
      (assoc state
             :player-coords new-coords
             :people (map #(guest-action state %) (state :people))
             :status ""
             :turns (-> :turns state inc)))))

(defn process-input [state input]
  (case input
    :player-left (move-player state [0 -1])
    :player-right (move-player state [0 1])
    :player-up (move-player state [-1 0])
    :player-down (move-player state [1 0])))
