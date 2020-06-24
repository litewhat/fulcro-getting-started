(ns app.person.db.queries
  (:require [hugsql.core :as hc]
            [taoensso.timbre :as log]))

(declare
  create-person-table
  drop-person-table
  create-person-list-table
  drop-person-list-table
  create-person-list-people-table
  drop-person-list-people-table
  insert-person
  get-all-people
  get-person-by-id
  insert-person-list
  get-person-list-by-id
  get-all-person-lists
  add-person-to-list
  get-people-by-list-id)

(def ^:private query-file "app/person/db/queries.sql")

(log/debug "Initalizing person queries")
(hc/def-db-fns query-file)

(comment
  (clojure.pprint/pprint (hc/map-of-db-fns "app/person/db/queries.sql")))