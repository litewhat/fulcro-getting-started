(ns app.person.mutations
  (:require [com.wsscode.pathom.connect :as pc]
            [taoensso.timbre :as log]
            [app.db :as db]
            [app.person.model :as pm]))

(pc/defmutation delete-person
  [env {list-id :list/id person-id :person/id}]
  {::pc/sym `delete-person}
  (log/debug (format "Deleting person %s from the list %s" person-id list-id))
  (let [system {:db {:conn db/conn-spec}}]
    (pm/remove-person-from-list system {:list/id list-id :person/id person-id})))

(def mutations [delete-person])
