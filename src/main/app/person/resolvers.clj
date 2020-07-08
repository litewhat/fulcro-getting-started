(ns app.person.resolvers
  (:require [com.wsscode.pathom.core :as p]
            [com.wsscode.pathom.connect :as pc]
            [taoensso.timbre :as log]
            [app.db :as db]
            [app.person.model :as pm]))

(pc/defresolver person-resolver
  [env {person-id :person/id}]
  {::pc/input #{:person/id}
   ::pc/output [:person/name :person/age]}
  (log/debug (format "Calling person-resolver for person-id = %s" person-id))
  (let [system {:db {:conn db/conn-spec}}]
    (pm/person-by-id system person-id)))

(pc/defresolver list-resolver
  [env {list-id :list/id}]
  {::pc/input  #{:list/id}
   ::pc/output [:list/label {:list/people [:person/id]}]}
  (log/debug (format "Calling list-resolver for list-id = %s" list-id))
  (let [system {:db {:conn db/conn-spec}}]
    (pm/person-list-by-id system list-id)))

(pc/defresolver friends-resolver
  [env input]
  {::pc/output [{:friends [:list/id]}]}
  (log/debug "Calling friends-resolver")
  {:friends {:list/id :friends}})

(pc/defresolver enemies-resolver
  [env input]
  {::pc/output [{:enemies [:list/id]}]}
  (log/debug "Calling enemies-resolver")
  {:enemies {:list/id :enemies}})


(def resolvers [person-resolver list-resolver friends-resolver enemies-resolver])