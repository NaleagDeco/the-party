(ns the-party.core
  (:require [the-party.game-state :as gs])
  (:require [lanterna.screen :as s])
  (:require [lanterna.terminal :as t])
  (:require [reagi.core :as r])
  (:gen-class))

;;;; INPUTS
(def input (r/events))

(defn process-input! [char]
  (do
    (case char
      \h (r/deliver input :player-left)
      \j (r/deliver input :player-down)
      \k (r/deliver input :player-up)
      \l (r/deliver input :player-right)
      nil)
    char))

;;;; MODEL

;;;; UPDATE
(def game-state
  (r/reduce gs/process-input (gs/create "Hello") input))

;;;; VIEW
(defn render-tile [tile]
  (case tile
    :vertical-wall \|
    :horizontal-wall \-
    :open-door \+
    :passage \#
    :empty-space \.
    :inaccessible \space))

(defn render-player [term state]
  (let [coords (state :player-coords)]
    ; lanterna uses x y coords but we use row column
    (t/put-character term \@ (second coords) (first coords))))

(defn render [term state]
  (t/clear term)
  (doseq [tile (seq (state :terrain))]
    (t/put-character term (render-tile (second tile))
                     (second (first tile)) (first (first tile))))
  (render-player term state))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (let [term (t/get-terminal)]
    (t/in-terminal term
                   (loop [key nil]
                     (if (= key \q) "Goodbye!"
                         (do
                           (render term @game-state)
                           (recur (process-input! (t/get-key-blocking term)))))))))
