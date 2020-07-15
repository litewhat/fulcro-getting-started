(ns app.db
  (:require [hugsql.core :as hc]
            [hugsql.adapter.clojure-java-jdbc :as had]
            [taoensso.timbre :as log]
            [app.config :as cfg]
            [app.db.queries :as db-queries]
            [app.person.db.queries :as person-queries]
            [app.user.db.queries :as user-queries]))

(def conn-spec
  {:dbtype   "postgres"
   :host     (get-in cfg/app-config [:database :hostname])
   :port     (get-in cfg/app-config [:database :port])
   :dbname   (get-in cfg/app-config [:database :name])
   :user     (get-in cfg/app-config [:database :user])
   :password (get-in cfg/app-config [:database :password])})

(defn init! []
  (log/debug "Initalizing hugsql adapter")
  (hc/set-adapter! (had/hugsql-adapter-clojure-java-jdbc)))

;; migrations

(comment
 (user-queries/insert-app-user conn-spec {:email "test@example.com"})
  )

(defn set-up-tables!
  [db-spec]
  (db-queries/create-extension db-spec {:name "uuid-ossp"})
  (log/debugf "Created `%s` extension" "uuid-ossp")

  (person-queries/create-person-table db-spec)
  (log/debugf "Created %s table" "person")

  (person-queries/create-person-list-table db-spec)
  (log/debugf "Created %s table" "person_list")

  (person-queries/create-person-list-people-table db-spec)
  (log/debugf "Created %s table" "person_list_person")

  (user-queries/create-app-user-table db-spec)
  (log/debugf "Created %s table" "app_user")
  )

(defn tear-down-tables!
  [db-spec]
  (user-queries/drop-app-user-table db-spec)
  (log/debugf "Dropped %s table" "app_user")

  (person-queries/drop-person-list-people-table db-spec)
  (log/debugf "Dropped %s table" "person_list_person")

  (person-queries/drop-person-list-table db-spec)
  (log/debugf "Dropped %s table" "person_list")

  (person-queries/drop-person-table db-spec)
  (log/debugf "Dropped %s table" "person")

  (db-queries/drop-extension db-spec {:name "uuid-ossp"})
  (log/debugf "Dropped `%s` extension" "uuid-ossp"))
