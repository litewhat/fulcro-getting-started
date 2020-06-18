(ns app.person.mutations
  (:require [com.fulcrologic.fulcro.mutations :refer [defmutation]]
            [com.fulcrologic.fulcro.algorithms.merge :as merge]))

(defmutation delete-person
  "Delete the person with `name` from the list with `list-name`"
  [{list-id :list/id person-id :person/id}]
  (action [{:keys [state]}]
    (js/console.log "Running mutation" `delete-person)
    (swap! state merge/remove-ident* [:person/id person-id] [:list/id list-id :list/people])))

