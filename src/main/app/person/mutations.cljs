(ns app.person.mutations
  (:require [com.fulcrologic.fulcro.mutations :as m :refer [defmutation]]))

(defmutation delete-person
  "Delete the person with `name` from the list with `list-name`"
  [{:keys [list-name name]}]
  (action [{:keys [state]}]
    (let [path (if (= "Friends" list-name)
                 [:friends :list/people]
                 [:enemies :list/people])
          old-list (get-in @state path)
          new-list (vec (filter #(not= name (:person/name %)) old-list))]
      (swap! state assoc-in path new-list))))
