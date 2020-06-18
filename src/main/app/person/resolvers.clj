(ns app.person.resolvers
  (:require [com.wsscode.pathom.core :as p]
            [com.wsscode.pathom.connect :as pc]))

(defonce person-table
  {99 {:person/id 99 :person/name "Paweł" :person/age 28}})

(defn get-person-from-db
  "Returns person name for given person id `pid`"
  [db pid]
  (get db pid))

(pc/defresolver person-name-resolver
  [env {person-id :person/id}]
  {::pc/input #{:person/id}
   ::pc/output [:person/name]}
  (get-person-from-db person-table person-id))

(pc/defresolver person-resolver
  [env {person-id :person/id}]
  {::pc/input #{:person/id}
   ::pc/output [:person/name :person/age]}
  (get-person-from-db person-table person-id))

(def resolvers [person-name-resolver
                person-resolver])