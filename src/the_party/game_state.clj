(ns the-party.game-state
  (:require [clojure.java.io :as io])
  (:require [the-party.builders :as builder]))

(defn inaccessible? [tile]
  (not-any? #(= tile %) '(:empty-space :passage :open-door)))

(defn create [player]
  (let [terrain (builder/file->terrain (io/resource "map.txt"))
        player-coords (first (rand-nth
                              (filter #(= (second %) :empty-space)
                                      terrain)))]
    {:player player
     :terrain terrain
     :player-coords player-coords
     :status "Welcome to The Party!"}))

(defn move-player [state offset]
  (let [terrain (state :terrain)
        old-coords (state :player-coords)
        tentative-coords (map #(reduce + %) (map vector old-coords offset))
        tentative-tile (terrain tentative-coords)
        new-coords (if (inaccessible? tentative-tile)
                     old-coords tentative-coords)]
    (assoc state :player-coords new-coords)))

(defn process-input [state input]
  (case input
    :player-left (move-player state [0 -1])
    :player-right (move-player state [0 1])
    :player-up (move-player state [-1 0])
    :player-down (move-player state [1 0])))
