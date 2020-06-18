(ns app.person.mutations
  (:require [com.fulcrologic.fulcro.mutations :as m :refer [defmutation]]))

(defmutation delete-person
  "Delete the person with `name` from the list with `list-name`"
  [{:keys [list item-id]}]
  (action [{:keys [state]}]
    #_(let [path (if (= "Friends" list-name)
                 [:friends :list/people]
                 [:enemies :list/people])
          old-list (get-in @state path)
          new-list (vec (filter #(not= name (:person/name %)) old-list))]
      #_(swap! state assoc-in path new-list))
    (println "Running mutation" `delete-person)))
