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
  (let [terrain #_ (builder/tree->terrain) (builder/populate-ladders
                 (builder/tree->terrain))
        player-coords (ffirst (filter #(= (second %) :up-ladder) terrain))]
    {:player player
     :terrain terrain
     :people (builder/generate-people terrain 20)
     :player-coords player-coords
     :status '("Welcome to The Party!")
     :turns 0}))

(defn guest-present? [state coord]
  (contains? (state :people) coord))

(defn neighbourhood [coord]
  (map (partial map + coord) directions))

(defn adjacent-to-player? [state coord]
  (boolean (some #{(state :player-coords)} (neighbourhood coord))))

(defn converse [belligerent target]
  (let [t-comp (target :composure)
        t-stam (target :stamina)
        b-conf (belligerent :confidence)
        missed (= 1 (rand-int 2))
        damage (ceil (* (/ 100 (+ 100 t-comp)) b-conf))]
    (if missed
      target
      (assoc target :stamina (- t-stam damage)))))

(defn guest-move [state other-guests accum old-coords guest]
  (let [terrain (state :terrain)
        tentative-coords (map + (rand-nth directions) old-coords)
        new-coords (if (or (inaccessible? (terrain tentative-coords))
                           (contains? other-guests tentative-coords)
                           (contains? accum tentative-coords)
                           (= (state :player-coords) tentative-coords))
                     old-coords
                     tentative-coords)]
    [new-coords guest]))

(defn guest-actions [state]
  (let [conversing-guests (select-keys (state :people)
                                       (filter
                                        (partial adjacent-to-player? state)
                                        (-> :people state keys)))
        moving-guests (select-keys (state :people)
                                   (filter
                                    (partial
                                     (complement adjacent-to-player?) state)
                                    (-> :people state keys)))
        conversing-player (reduce
                           #(list (converse %2 (first %1))
                                  (conj (second %1) "Someone talked to you!"))
                           (list (state :player) (state :status))
                           (vals conversing-guests))]
    (loop [guests moving-guests
           accum conversing-guests]
      (if (empty? guests)
        (assoc state
               :people accum
               :player (first conversing-player)
               :status (second conversing-player))
        (let [[old-coords guest] (-> guests seq first)
              other-guests (dissoc guests old-coords)
              [new-coords new-guest] (guest-move state guests accum old-coords guest)]
          (recur other-guests (assoc accum new-coords new-guest)))))))

(defn turn-tick [state]
  (assoc state :turns (-> :turns state inc)))

(defn clear-status [state]
  (assoc state :status []))

(defn append-status [state msg]
  (assoc state :status (conj (state :status) msg)))

(defn move-to [state new-coords]
  (assoc state :player-coords new-coords))

(defn player-move [state new-coords]
  (let [terrain (state :terrain)
        blocked (-> new-coords terrain inaccessible?)]
    (if blocked
      (append-status state "You bump your nose into a wall.")
      (-> state
        guest-actions
        (move-to new-coords)
        turn-tick))))

(defn player-converse [state target-coords]
  (let [guests (state :people)
        other-guests (dissoc guests target-coords)
        target (converse (state :player) (guests target-coords))
        new-guests (if (>= (target :stamina) 0)
                     (assoc other-guests target-coords target)
                     other-guests)
        msg (if (>= (target :stamina) 0) "You chat a bit with someone." "The conversation peters out.")]
    (-> (assoc state :people new-guests)
      guest-actions
      (append-status msg)
      turn-tick)))

(defn player-action [state offset]
  (let [new-coords (map + (state :player-coords) offset)
        converse (guest-present? state new-coords)]
    (if converse
      (player-converse state new-coords)
      (player-move state new-coords))))

(defn process-input [state input]
  (let [next-state (clear-status state)]
    (case input
      :player-left (player-action next-state [0 -1])
      :player-right (player-action next-state [0 1])
      :player-up (player-action next-state [-1 0])
      :player-down (player-action next-state [1 0])
      :player-wait (player-action next-state [0 0]))))
