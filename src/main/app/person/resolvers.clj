(ns app.person.resolvers
  (:require [com.wsscode.pathom.core :as p]
            [com.wsscode.pathom.connect :as pc]
            [taoensso.timbre :as log]))

;; tables

(defonce person-table
  {1  {:person/id 1 :person/name "David" :person/age 45}
   2  {:person/id 2 :person/name "Gonzalo" :person/age 38}
   3  {:person/id 3 :person/name "Andrea" :person/age 42}
   4  {:person/id 4 :person/name "Joseph" :person/age 65}
   5  {:person/id 5 :person/name "Lucas" :person/age 25}
   6  {:person/id 6 :person/name "Raymond" :person/age 56}
   99 {:person/id 99 :person/name "Paweł" :person/age 28}})

(defonce list-table
  {:friends {:list/id :friends
             :list/label "Friends"
             :list/people [1 2 3]}
   :enemies {:list/id :enemies
             :list/label "Enemies"
             :list/people [4 5 6]}})

(defn get-person-from-db
  "Returns person for given person id `pid`"
  [db pid]
  (get db pid))

(defn get-list-from-db
  "Returns person name for given person id `pid`"
  [list-table list-id]
  (get list-table list-id))

;; resolvers

(pc/defresolver person-resolver
  [env {person-id :person/id}]
  {::pc/input #{:person/id}
   ::pc/output [:person/name :person/age]}
  (log/info "Calling person-resolver with :person/id" person-id)
  (get-person-from-db person-table person-id))

(pc/defresolver list-resolver
  [env {list-id :list/id}]
  {::pc/input  #{:list/id}
   ::pc/output [:list/label {:list/people [:person/id]}]}
  (when-let [xlist (get-list-from-db list-table list-id)]
    (log/info "Calling list-resolver" xlist)
    (assoc xlist :list/people (mapv (fn [id] {:person/id id}) (:list/people xlist)))))

(def resolvers [person-resolver list-resolver])