(ns app.person.mutations
  (:require [app.person.resolvers :as resolvers]
            [com.wsscode.pathom.connect :as pc]
            [taoensso.timbre :as log]))

(pc/defmutation delete-person
  [env {list-id :list/id person-id :person/id}]
  {::pc/sym `delete-person}
  (log/info "Deleting person" person-id "from the list" list-id)
  (swap! resolvers/list-table update-in [list-id :list/people]
         (fn [old]
           (filterv #(not= person-id %) old))))

(def mutations [delete-person])
