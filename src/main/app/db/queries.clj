(ns app.db.queries
  (:require [hugsql.core :as hc]
            [taoensso.timbre :as log]))

(declare
  create-extension
  drop-extension)

(def ^:private query-file "app/db/queries.sql")

(log/debug "Initializing DB queries")
(hc/def-db-fns query-file {:quoting :ansi})
(log/debug "DB queries initialized")

(comment
  (clojure.pprint/pprint (hc/map-of-db-fns query-file)))
