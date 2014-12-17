(ns the-party.game-state
  (:require [clojure.java.io :as io])
  (:require [the-party.builders :as builder]))

(defn create-game [player]
  { :player player
   :terrain (builder/file->terrain (io/resource "map.txt"))
   :player-coords [0 0]
   :status "Welcome to The Party!" })
