(ns app.user-registration.model
  (:require [buddy.hashers :as h]
            [app.user.db.queries :as q]))

(defn- db-record->user
  [{:keys [id email created_at]}]
  {:user/id         id
   :user/email      email
   :user/created-at created_at})

(defn create-user
  [system {:keys [email password]}]
  (let [db-conn  (get-in system [:db :conn])
        new-user (q/insert-app-user db-conn {:email email :password (h/derive password)})]
    (db-record->user new-user)))

(defn by-email
  [system email]
  (let [db-conn (get-in system [:db :conn])]
    (when-some [user (q/get-app-user-by-email db-conn {:email email})]
      (db-record->user user))))

