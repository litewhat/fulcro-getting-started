(ns app.user-registration.ui
  (:require [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
            [com.fulcrologic.fulcro.dom :as dom]
            [taoensso.timbre :as log]
            [app.user-registration.mutations :as ur.mut]))

(defsc InputError
  [this
   {:keys [:error/id :error/message :error/code] :as props}
   {:keys [:input-name] :as computed}]
  {:query         [:error/id :error/message :error/code :error/field-name]
   :ident         (fn [] [:error/id id])
   :initial-state (fn [{:keys [id] :as params}]
                    {:error/id         (or id (random-uuid))
                     :error/message    "Invalid value"
                     :error/code       :invalid-value
                     :error/field-name :user-registration/email})}
  (dom/p :.text-danger message))

(def ui-input-error (comp/computed-factory InputError {:keyfn :error/id}))

(defsc UserRegistration
  [this {:keys [:user-registration/id
                :user-registration/email
                :user-registration/status
                :user-registration/password
                :user-registration/confirm-password
                :user-registration/errors] :as props}]
  {:query         (fn [params]
                    [:user-registration/id
                     :user-registration/email
                     :user-registration/password
                     :user-registration/confirm-password
                     :user-registration/status
                     {:user-registration/errors (comp/get-query InputError)}])
   :ident         (fn []
                    [:user-registration/id id])
   :initial-state (fn [{:keys [id]}]
                    {:user-registration/id     (or id (random-uuid))
                     :user-registration/errors []
                     :user-registration/status :started})}
  (let [email-errors            (filter #(= :user-registration/email (:error/field-name %)) errors)
        password-errors         (filter #(= :user-registration/password (:error/field-name %)) errors)
        confirm-password-errors (filter #(= :user-registration/confirm-password (:error/field-name %)) errors)]
    (dom/div
      (dom/form {:onSubmit (fn [e] (.preventDefault e))}
        (dom/div
          (dom/input {:type     "text" :placeholder "Email"
                      :onChange #(let [email (-> % .-target .-value)]
                                   (comp/transact! this [(ur.mut/update-input
                                                           {:user-registration/id    id
                                                            :user-registration/email email})
                                                         (ur.mut/validate-input
                                                           {:user-registration/id    id
                                                            :user-registration/email email})]))})
          (map #(ui-input-error %) email-errors))
        (dom/div
          (dom/input {:type     "password" :placeholder "Password"
                      :onChange #(let [password (-> % .-target .-value)]
                                   (comp/transact! this [(ur.mut/update-input
                                                           {:user-registration/id       id
                                                            :user-registration/password password})
                                                         (ur.mut/validate-input
                                                           {:user-registration/id       id
                                                            :user-registration/password password})]))})
          (map #(ui-input-error %) password-errors))
        (dom/div
          (dom/input {:type     "password" :placeholder "Confirm password"
                      :onChange #(let [confirm-password (-> % .-target .-value)]
                                   (comp/transact! this [(ur.mut/update-input
                                                           {:user-registration/id               id
                                                            :user-registration/confirm-password confirm-password})
                                                         (ur.mut/validate-input
                                                           {:user-registration/id               id
                                                            :user-registration/confirm-password confirm-password})]))})
          (map #(ui-input-error %) confirm-password-errors))
        (let [disabled? (not= :valid-inputs status)]
          (dom/button {:disabled disabled?
                       :onClick  #(comp/transact! this [(ur.mut/register
                                                          {:user/email            email
                                                           :user/password         password
                                                           :user/confirm-password confirm-password})])}
                      "Register"))))))

(def ui-user-registration (comp/factory UserRegistration))
