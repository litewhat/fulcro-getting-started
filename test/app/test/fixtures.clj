(ns app.test.fixtures
  (:require [taoensso.timbre :as log]
            [db.seed :as dbs]
            [app.db :as db]))

(defn db
  [db-conn]
  (fn [f]
    (log/debug "Setting up tables")
    (db/set-up-tables! db-conn)
    (dbs/seed! db-conn)
    (f)
    (log/debug "Tearing down tables")
    (db/tear-down-tables! db-conn)))
