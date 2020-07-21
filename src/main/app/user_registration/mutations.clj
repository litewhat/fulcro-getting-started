(ns app.user-registration.mutations
  (:require [clojure.spec.alpha :as s]
            [com.fulcrologic.fulcro.server.api-middleware :refer [augment-response]]
            [com.wsscode.pathom.connect :as pc]
            [taoensso.timbre :as log]
            [app.db :as db]
            [app.spec]
            [app.user-registration.model :as m]
            [app.user-registration.model.validation :as mv]))

(pc/defmutation register
  [env {:keys [:user/email :user/password :user/confirm-password] :as params}]
  {::pc/sym `register}
  (let [env'   (assoc env ::mv/data params)
        errors (mv/validate-data env')
        system {:db {:conn db/conn-spec}}]
    (if (seq errors)
      (augment-response {:errors (vec errors)} #(assoc % :status 400))
      (if-some [user (m/by-email system email)]
        (augment-response
          {:errors [{:code    :user-already-exists
                     :message "User with given email already exists"}]}
          #(assoc % :status 400))
        (m/create-user system {:email email :password password})))))

(s/def :app.user-registration.mutations.register/success
  (s/keys :req [:user/id :user/email :user/created-at]))

(s/def :app.user-registration.mutations.register/errors
  (s/coll-of
    (s/keys :req-un [:error/code :error/message]
            :opt-un [:error/param])
    :kind vector?))

(s/def :app.user-registration.mutations.register/error
  (s/keys :req-un [:app.user-registration.mutations.register/errors]))

(s/def ::register.response
  (s/or :success :app.user-registration.mutations.register/success
        :error :app.user-registration.mutations.register/error))

(def mutations [register])
