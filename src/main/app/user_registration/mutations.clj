(ns app.user-registration.mutations
  (:require [clojure.spec.alpha :as s]
            [com.fulcrologic.fulcro.server.api-middleware :refer [augment-response]]
            [com.wsscode.pathom.connect :as pc]
            [taoensso.timbre :as log]
            [app.db :as db]
            [app.spec]
            [app.user.db.queries :as user-queries]
            [app.user-registration.model.validation :as mv]))

(pc/defmutation register
  [env {:keys [:user/email :user/password :user/confirm-password] :as params}]
  {::pc/sym `register}
  (log/debug "Registering user")
  (log/spy :debug params)
  (let [env'   (assoc env ::mv/data params)
        errors (mv/validate-data env')]
    (if (seq errors)
      (augment-response
        {:errors (vec errors)}
        #(assoc % :status 400))
      (let [user (user-queries/get-app-user-by-email db/conn-spec {:email email})]
        (if (some? user)
          (augment-response
            {:errors [{:code    :user-already-exists
                       :message "User with given email already exists"}]}
            #(assoc % :status 400))
          (let [new-user (user-queries/insert-app-user db/conn-spec {:email email})]
            (log/warn "Password is not used during registration. Maybe you want to store hashed value in db?")
            (log/spy :debug password)
            {:user/id         (:id new-user)
             :user/email      (:email new-user)
             :user/created-at (:created_at new-user)}))))))

(s/def ::register.response
  (s/keys :req [:user/id :user/email :user/created-at]))

(def mutations [register])
