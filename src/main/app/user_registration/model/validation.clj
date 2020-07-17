(ns app.user-registration.model.validation
  (:require [clojure.spec.alpha :as s]
            [app.common.utils :as u]))

(defmulti validate-input (fn [_ param _] param) :default ::default)

(defmethod validate-input ::default
  [{:as env} param value]
  nil)

(defmethod validate-input :user/email
  [env param value]
  (let [valid? (u/email? value)]
    (when (not valid?)
      {:code    :invalid-email
       :message "Invalid email"
       :param   param})))

(defmethod validate-input :user/password
  [env param value]
  (let [valid? (s/valid? (s/nilable (s/and not-empty
                                           (u/min-length? 8)
                                           u/has-lowercase-letter?
                                           u/has-uppercase-letter?
                                           u/has-number?))
                         value)]
    (when (not valid?)
      {:code    :invalid-password
       :message "Password should contain minimum 8 characters, lowercase letter, uppercase letter and number"
       :param   param})))

(defmethod validate-input :user/confirm-password
  [{:keys [::data]} param value]
  (let [password (:user/password data)
        valid?   (s/valid? (s/and (s/nilable string?) #(= password %)) value)]
    (when (not valid?)
      {:code    :invalid-confirm-password
       :message "Password does not match"
       :param   param})))

(defn validate-data
  [{:keys [::data] :as env}]
  (keep #(validate-input env (first %) (second %)) data))
