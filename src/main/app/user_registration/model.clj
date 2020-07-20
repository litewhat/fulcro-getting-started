(ns app.user-registration.model
  (:require [app.user.db.queries :as q]
            [taoensso.timbre :as log]))


(defn- db-record->user
  [{:keys [id email created_at]}]
  {:user/id         id
   :user/email      email
   :user/created-at created_at})

(defn create-user
  [system {:keys [email password]}]
  (let [db-conn (get-in system [:db :conn])
        new-user (q/insert-app-user db-conn {:email email})]
    (log/warn "Password is not used during registration. Maybe you want to store hashed value in db?")
    (log/spy :debug password)
    (db-record->user new-user)))

(defn by-email
  [system email]
  (let [db-conn (get-in system [:db :conn])]
    (when-some [user (q/get-app-user-by-email db-conn {:email email})]
      (db-record->user user))))