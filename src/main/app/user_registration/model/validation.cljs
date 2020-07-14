(ns app.user-registration.model.validation
  (:require [cljs.spec.alpha :as s]))

(defmulti validate-input (fn [_ param _] param) :default ::default)

(defmethod validate-input ::default
  [{:as env} param value]
  nil)

(def email-regex-str "(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:(2(5[0-5]|[0-4][0-9])|1[0-9][0-9]|[1-9]?[0-9]))\\.){3}(?:(2(5[0-5]|[0-4][0-9])|1[0-9][0-9]|[1-9]?[0-9])|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])")

(defmethod validate-input :user-registration/email
  [{:as env} param value]
  (let [valid? (some? (re-matches (re-pattern email-regex-str) value))]
    (when (not valid?)
      {:error/code    :invalid-email
       :error/message "Invalid email"
       :error/field-name param})))

(defn has-lowercase-letter? [s]
  (not (= s (.toUpperCase s))))

(defn has-uppercase-letter? [s]
  (not (= s (.toLowerCase s))))

(defn has-number? [s]
  (.test #"\d" s))

(defn min-length? [len]
  #(<= len (count %)))

(defmethod validate-input :user-registration/password
  [{:as env} param value]
  (let [valid? (s/valid? (s/nilable (s/and not-empty
                                           (min-length? 8)
                                           has-lowercase-letter?
                                           has-uppercase-letter?
                                           has-number?))
                         value)]
    (when (not valid?)
      {:error/code    :invalid-password
       :error/message "Password should contain minimum 8 characters, lowercase letter, uppercase letter and number"
       :error/field-name param})))

(defmethod validate-input :user-registration/confirm-password
  [{::keys [:registration-id] :as env} param value]
  (let [state-map (-> env :state deref)
        password  (get-in state-map [:user-registration/id registration-id :user-registration/password])
        valid?    (s/valid? (s/and (s/nilable string?) #(= password %)) value)]
    (when (not valid?)
      {:error/code       :invalid-confirm-password
       :error/message    "Password does not match"
       :error/field-name param})))
