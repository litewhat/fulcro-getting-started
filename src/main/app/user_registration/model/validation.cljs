(ns app.user-registration.model.validation
  (:require [cljs.spec.alpha :as s]
            [app.common.utils :as u]))

(defmulti validate-input (fn [_ param _] param) :default ::default)

(defmethod validate-input ::default
  [{:as env} param value]
  nil)

(defmethod validate-input :user-registration/email
  [{:as env} param value]
  (let [valid? (u/email? value)]
    (when (not valid?)
      {:error/code       :invalid-email
       :error/message    "Invalid email"
       :error/field-name param})))

(defmethod validate-input :user-registration/password
  [{:as env} param value]
  (let [valid? (s/valid? (s/nilable (s/and not-empty
                                           (u/min-length? 8)
                                           u/has-lowercase-letter?
                                           u/has-uppercase-letter?
                                           u/has-number?))
                         value)]
    (when (not valid?)
      {:error/code       :invalid-password
       :error/message    "Password should contain minimum 8 characters, lowercase letter, uppercase letter and number"
       :error/field-name param})))

(defmethod validate-input :user-registration/confirm-password
  [{:keys [::registration-id] :as env} param value]
  (let [state-map (-> env :state deref)
        password  (get-in state-map [:user-registration/id registration-id :user-registration/password])
        valid?    (s/valid? (s/and (s/nilable string?) #(= password %)) value)]
    (when (not valid?)
      {:error/code       :invalid-confirm-password
       :error/message    "Password does not match"
       :error/field-name param})))

(defn validate-data
  [{:keys [::registration-id ::data] :as env}]
  (keep #(validate-input env (first %) (second %)) data))