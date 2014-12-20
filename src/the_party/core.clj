(ns the-party.core
  (:require [clojure.core.async :as async :refer [<! >!]])
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
    :vertical-wall "|"
    :horizontal-wall "-"
    :open-door "+"
    :passage "#"
    :empty-space "."
    :inaccessible " "))

(defn render-player [term state]
  (let [coords (state :player-coords)]
    ; lanterna uses x y coords but we use row column
    (s/put-string term (second coords) (first coords) "@")))

(defn render [term state]
  (s/clear term)
  (doseq [tile (seq (state :terrain))]
    (s/put-string term (second (first tile)) (first (first tile))
                  (render-tile (second tile))))
  (render-player term state)
  (s/redraw term))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (let [screen (s/get-screen)
        ui-chan (async/chan (async/sliding-buffer 10))]
    (r/subscribe game-state ui-chan)
    (s/in-screen screen
                 (async/go-loop [game-state (<! ui-chan)]
                   (render screen game-state)
                   (recur (<! ui-chan)))
                 (loop [key nil]
                   (if (= \q key) "Goodbye!"
                       (recur (process-input! (s/get-key-blocking screen))))))))
