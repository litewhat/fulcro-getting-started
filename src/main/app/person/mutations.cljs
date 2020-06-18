(ns app.person.mutations
  (:require [com.fulcrologic.fulcro.mutations :refer [defmutation]]
            [com.fulcrologic.fulcro.algorithms.merge :as merge]))

(defmutation delete-person
  "Delete the person with `:person/id` from the list with `:list/id`"
  [{list-id :list/id person-id :person/id}]
  (action [{:keys [state]}]
    (swap! state merge/remove-ident* [:person/id person-id] [:list/id list-id :list/people]))
  ;; `remote` this is the name of remote server (see app.application namespace)
  (remote [env]
    (js/console.log env)
    true))

