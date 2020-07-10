(ns app.person.mutations
  (:require [com.fulcrologic.fulcro.algorithms.merge :as merge]
            [com.fulcrologic.fulcro.application :as app]
            [com.fulcrologic.fulcro.mutations :refer [defmutation]]
            [taoensso.timbre :as log]))

(defmutation delete-person
  "Delete the person with `:person/id` from the list with `:list/id`"
  [{list-id :list/id person-id :person/id}]
  (action [{:keys [state]}]
    (swap! state merge/remove-ident* [:person/id person-id] [:list/id list-id :list/people]))
  (remote [env]
    (log/spy :debug env)
    true))

(defmutation increase-counter
  [{counter-id :counter/id}]
  (action [{:keys [app]}]
    (let [current-state (app/current-state app)
          counter-state (get-in current-state [:counter/id counter-id])
          counter-tree  (update counter-state :counter/clicks inc)]
      (log/spy :debug counter-state)
      (merge/merge! app {[:counter/id counter-id] counter-tree} [:counter/id :counter/clicks]))))
