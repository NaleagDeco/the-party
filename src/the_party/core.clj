(ns the-party.core
  (:require [the-party.game-state :as gs])
  (:require [lanterna.screen :as s])
  (:require [lanterna.terminal :as t])
  (:gen-class))

;;;; INPUTS

;;;; MODEL

;;;; UPDATE

;;;; VIEW
(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (let [state (gs/create-game "Hello")
        term (t/get-terminal)]
    (t/in-terminal term
                      (t/put-string term "Hello!")
                      (t/get-key-blocking term))))
