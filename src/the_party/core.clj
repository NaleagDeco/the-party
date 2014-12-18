(ns the-party.core
  (:require [the-party.game-state :as gs])
  (:require [lanterna.screen :as s])
  (:require [lanterna.terminal :as t])
  (:gen-class))

;;;; INPUTS

;;;; MODEL

;;;; UPDATE

;;;; VIEW
(defn render-tile [tile]
  (case tile
    :vertical-wall \|
    :horizontal-wall \-
    :open-door \+
    :passage \#
    :empty-space \.
    :inaccessible \space))

(defn render [term state]
  (doseq [tile (seq (state :terrain))]
    (t/put-character term (render-tile (second tile))
                     (second (first tile)) (first (first tile)))))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (let [state (gs/create-game "Hello")
        term (t/get-terminal)]
    (t/in-terminal term
                   (render term state)
                   (t/get-key-blocking term))))
