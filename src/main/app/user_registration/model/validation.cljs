(ns app.user-registration.model.validation)

(defmulti validate-input (fn [_ param _] param) :default ::default)

(def email-regex-str "(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:(2(5[0-5]|[0-4][0-9])|1[0-9][0-9]|[1-9]?[0-9]))\\.){3}(?:(2(5[0-5]|[0-4][0-9])|1[0-9][0-9]|[1-9]?[0-9])|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])")

(defmethod validate-input :user-registration/email
  [{:as env} param value]
  (let [valid? (some? (re-matches (re-pattern email-regex-str) value))]
    (when (not valid?)
      {:error/code    :invalid-email
       :error/message "Invalid email"
       :error/field-name param})))

(defmethod validate-input ::default
  [{:as env} param value]
  nil)

#_#_
(defmethod validate-input :user-registration/password
  [{:as env} param value]
  (let [valid? (s/valid? (s/nilable string?) value)]
    (when (not valid?)
      {:code    :invalid-password
       :message "Invalid password"})))

(defmethod validate-input :user-registration/confirm-password
  [{:as env} param value]
  (let [password (get-in app-state [:user-registration/id reg-id :password])
        valid?   (and (s/valid? (s/nilable string?) value) (= password value))]
    (when (not valid?)
      {:code    :invalid-confirm-password
       :message "Invalid confirm password"})))

;; validate-input context
#_
{:user-registration/id 1
 :user-registration/errors {:email {:code "abc"
                                    :message "Abc"}
                            :password {:code "abc"
                                       :message "Abc"}
                            :confirm-password {:code "abc"
                                               :message "Abc"}}}


