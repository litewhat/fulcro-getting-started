(ns app.user-registration.mutations
  (:require [com.fulcrologic.fulcro.algorithms.merge :as merge]
            [com.fulcrologic.fulcro.mutations :refer [defmutation]]
            [taoensso.timbre :as log]
            [app.user-registration.model.validation :as v]
            [app.user-registration.model :as m]))

(defmutation update-input
  [{registration-id :user-registration/id :as params}]
  (action [{:keys [state]}]
    (let [data-to-update (select-keys params [:user-registration/email :user-registration/password :user-registration/confirm-password])]
      (swap! state #(update-in % [:user-registration/id registration-id] merge data-to-update)))))

(defmutation validate-input
  [{registration-id :user-registration/id :as params}]
  (action [{:keys [component state] :as env}]
    (let [state-map           (deref state)
          data-to-validate    (select-keys params m/input-field-names)
          env'                (assoc env ::v/registration-id registration-id ::v/data data-to-validate)

          ;; perform validation and return new vector of errors associated with registration
          validation-errors   (v/validate-data env')
          registration-errors (m/list-errors env' registration-id)
          {:keys [affected unaffected]} (m/split-affected-errors registration-errors data-to-validate)
          new-errors          (vec (concat unaffected (map #(assoc % :error/id (random-uuid)) validation-errors)))

          ;; make status transition
          new-registration    (let [registration        (m/by-id env' registration-id)
                                    registration-values (m/input-values registration)
                                    event               (if (or (seq new-errors) (not= (count m/input-field-names) (count registration-values)))
                                                          :wrong-input
                                                          :correct-input)]
                                (-> registration
                                    (m/make-status-transition event)
                                    (assoc :user-registration/errors new-errors)))]
      (swap! state (fn [s]
                     (cond-> s
                       (seq affected) (assoc :error/id (apply dissoc (:error/id state-map) (map :error/id affected)))
                       :always (merge/merge-component component new-registration)))))))

(defmutation register
  [{:user/keys [:email :password :confirm-password] :as params}]
  (action [{:keys [app component ref state] :as env}]
    (let [registration  (get-in @state ref)
          registration' (m/make-status-transition registration :click-register)]
      (merge/merge-component! app component registration')))
  (remote [env] true)
  (ok-action [{:keys [app component ref state] :as env}]
    (let [registration  (get-in @state ref)
          registration' (m/make-status-transition registration :success)]
      (merge/merge-component! app component registration')))
  (error-action [{:keys [app component ref result state] :as env}]
    (let [errors (get-in result [:body `register :errors])
          registration (-> (get-in @state ref)
                           (m/make-status-transition :error)
                           (assoc :user-registration/remote-errors errors))]
      (merge/merge-component! app component registration))))
