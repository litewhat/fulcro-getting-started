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

(defsc UserRegistrationForm
  [this {:keys [:user-registration/id
                :user-registration/status
                :user-registration/email
                :user-registration/password
                :user-registration/confirm-password
                :user-registration/errors] :as props}]
  {:query         (fn [params]
                    [:user-registration/id
                     :user-registration/status
                     :user-registration/email
                     :user-registration/password
                     :user-registration/confirm-password
                     {:user-registration/errors (comp/get-query InputError)}])
   :ident         (fn [] [:user-registration/id id])
   :initial-state (fn [params] {})}
  (dom/div {}
    (dom/form {:onSubmit (fn [e] (.preventDefault e))}
      (let [email-errors (filter #(= :user-registration/email (:error/field-name %)) errors)]
        (dom/div
          (dom/input {:type     "text" :placeholder "Email"
                      :onChange #(let [email (-> % .-target .-value)]
                                   (comp/transact! this [(ur.mut/update-input
                                                           {:user-registration/id    id
                                                            :user-registration/email email})
                                                         (ur.mut/validate-input
                                                           {:user-registration/id    id
                                                            :user-registration/email email})]))})
          (map #(ui-input-error %) email-errors)))

      (let [password-errors (filter #(= :user-registration/password (:error/field-name %)) errors)]
        (dom/div
          (dom/input {:type     "password" :placeholder "Password"
                      :onChange #(let [password (-> % .-target .-value)]
                                   (comp/transact! this [(ur.mut/update-input
                                                           {:user-registration/id       id
                                                            :user-registration/password password})
                                                         (ur.mut/validate-input
                                                           {:user-registration/id       id
                                                            :user-registration/password password})]))})
          (map #(ui-input-error %) password-errors)))
      (let [confirm-password-errors (filter #(= :user-registration/confirm-password (:error/field-name %)) errors)]
        (dom/div
          (dom/input {:type     "password" :placeholder "Confirm password"
                      :onChange #(let [confirm-password (-> % .-target .-value)]
                                   (comp/transact! this [(ur.mut/update-input
                                                           {:user-registration/id               id
                                                            :user-registration/confirm-password confirm-password})
                                                         (ur.mut/validate-input
                                                           {:user-registration/id               id
                                                            :user-registration/confirm-password confirm-password})]))})
          (map #(ui-input-error %) confirm-password-errors)))
      (let [disabled? (not= :valid-inputs status)]
        (dom/button {:disabled disabled?
                     :onClick  #(comp/transact! this [(ur.mut/register
                                                        {:user/email            email
                                                         :user/password         password
                                                         :user/confirm-password confirm-password})])}
                    "Register")))))

(def ui-registration-form (comp/computed-factory UserRegistrationForm {}))

(defsc UserRegistrationInProgress
  [this {:keys [:user-registration/id] :as props}]
  {:query         [:user-registration/id]
   :ident         (fn [] [:user-registration/id id])
   :initial-state {}}
  (dom/div {}
    (dom/div {:className "spinner-border" :role "status"}
      (dom/span {:className "sr-only"}
        "In progress..."))))

(def ui-registration-in-progress (comp/computed-factory UserRegistrationInProgress {}))

(defsc UserRegistrationSuccess
  [this {:keys [:user-registration/id] :as props}]
  {:query         [:user-registration/id]
   :ident         (fn [] [:user-registration/id id])
   :initial-state {}}
  (dom/div {}
    (dom/p {:className "text-success"}
      "User registered successfully!")))

(def ui-registration-success (comp/computed-factory UserRegistrationSuccess {}))

(defsc UserRegistrationFailure
  [this {:keys [:user-registration/id
                :user-registration/status
                :user-registration/remote-errors] :as props}]
  {:query         [:user-registration/id
                   :user-registration/status
                   :user-registration/remote-errors]
   :ident         (fn [] [:user-registration/id id])
   :initial-state {}}
  (dom/div {}
    (map #(dom/p {:className "text-danger"} (:message %)) remote-errors)))

(def ui-registration-failure (comp/computed-factory UserRegistrationFailure {}))

(defsc UserRegistration
  [this {:keys [:user-registration/id
                :user-registration/email
                :user-registration/status
                :user-registration/password
                :user-registration/confirm-password
                :user-registration/errors
                :user-registration/remote-errors] :as props}]
  {:query         (fn [params]
                    [:user-registration/id
                     :user-registration/email
                     :user-registration/password
                     :user-registration/confirm-password
                     :user-registration/status
                     {:user-registration/errors (comp/get-query InputError)}
                     :user-registration/remote-errors])
   :ident         (fn [] [:user-registration/id id])
   :initial-state (fn [{:keys [id]}]
                    {:user-registration/id     (or id (random-uuid))
                     :user-registration/errors []
                     :user-registration/status :started})}
  (cond
    (contains? #{:started :valid-inputs :invalid-inputs} status)
    (ui-registration-form props)

    (contains? #{:in-progress} status)
    (ui-registration-in-progress props)

    (contains? #{:success} status)
    (ui-registration-success props)

    (contains? #{:failure} status)
    (ui-registration-failure props)))

(def ui-user-registration (comp/factory UserRegistration))
