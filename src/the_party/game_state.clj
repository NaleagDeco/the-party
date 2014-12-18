(ns the-party.game-state
  (:require [clojure.java.io :as io])
  (:require [the-party.builders :as builder]))

(defn create [player]
  { :player player
   :terrain (builder/file->terrain (io/resource "map.txt"))
   :player-coords [0 0]
   :status "Welcome to The Party!" })

(defn move-player [state offset]
  (let [old-coords (state :player-coords)
        new-coords (map #(reduce + %) (map vector old-coords offset))]
    (assoc state :player-coords new-coords)))

(defn process-input [state input]
  (case input
    :player-left (move-player state [0 -1])
    :player-right (move-player state [0 1])
    :player-up (move-player state [-1 0])
    :player-down (move-player state [1 0])))
