(ns app.db.queries
  (:require [hugsql.core :as hc]
            [taoensso.timbre :as log]))

(declare
  create-extension
  drop-extension
  get-all-tables
  get-all-data-types
  get-all-enum-type-values)

(def ^:private query-file "app/db/queries.sql")

(log/debug "Initializing DB queries")
(hc/def-db-fns query-file {:quoting :ansi})
(log/debug "DB queries initialized")
