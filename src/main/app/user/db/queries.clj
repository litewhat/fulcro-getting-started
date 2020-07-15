(ns app.user.db.queries
  (:require [hugsql.core :as hc]
            [taoensso.timbre :as log]))

(declare
  create-app-user-table
  drop-app-user-table
  create-token-type
  drop-token-type
  add-token-type
  create-token-table
  drop-token-table
  insert-app-user
  batch-insert-app-user
  get-app-user-by-id
  get-app-user-by-email
  get-all-app-users
  delete-app-user
  batch-delete-app-user

  ;add-token
  ;create-token
  ;invoke-token
  )

(def ^:private query-file "app/user/db/queries.sql")

(log/debug "Initializing user queries")
(hc/def-db-fns query-file)
(log/debug "User queries initialized")

(comment
  (clojure.pprint/pprint (hc/map-of-db-fns query-file)))