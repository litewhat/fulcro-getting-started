(ns app.person.db.queries
  (:require [hugsql.core :as hc]
            [taoensso.timbre :as log]))

(declare
  create-person-table
  drop-person-table
  create-person-list-table
  drop-person-list-table
  create-person-list-person-table
  drop-person-list-person-table
  insert-person
  get-all-people
  get-person-by-id)

(def ^:private query-file "app/person/db/queries.sql")

(log/debug "Initalizing person queries")
(hc/def-db-fns query-file)

(comment
  (clojure.pprint/pprint (hc/map-of-db-fns "app/person/db/queries.sql")))