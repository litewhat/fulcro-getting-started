(ns app.test.mock)

(def ^:dynamic *state-map*
  {:error/id
   {#uuid"2630743f-8d83-4e14-a577-3493dad4c802"
    {:error/code       :invalid-email
     :error/message    "Invalid email"
     :error/field-name :user-registration/email
     :error/id         #uuid"2630743f-8d83-4e14-a577-3493dad4c802"}
    #uuid"9b496605-9371-4c9e-9ab3-b7824e5af59f"
    {:error/code       :invalid-password
     :error/message    "Password should contain minimum 8 characters, lowercase letter, uppercase letter and number"
     :error/field-name :user-registration/password
     :error/id         #uuid"9b496605-9371-4c9e-9ab3-b7824e5af59f"}
    #uuid"c5d4f45d-524c-4542-b702-4b1e02a62d69"
    {:error/code       :invalid-confirm-password
     :error/message    "Password does not match"
     :error/field-name :user-registration/confirm-password
     :error/id         #uuid"c5d4f45d-524c-4542-b702-4b1e02a62d69"}
    #uuid"fa8da194-7105-43ae-aaaa-0634ebea094d"
    {:error/code       :invalid-password
     :error/message    "Password should contain minimum 8 characters, lowercase letter, uppercase letter and number"
     :error/field-name :user-registration/password
     :error/id         #uuid"fa8da194-7105-43ae-aaaa-0634ebea094d"}}
   :user-registration
   [:user-registration/id #uuid"3f65cf59-4049-4ed4-be1e-f51cc93e331c"]
   :user-registration/id
   {#uuid"3f65cf59-4049-4ed4-be1e-f51cc93e331c"
    {:user-registration/id               #uuid"3f65cf59-4049-4ed4-be1e-f51cc93e331c"
     :user-registration/errors           [[:error/id #uuid"2630743f-8d83-4e14-a577-3493dad4c802"]
                                          [:error/id #uuid"9b496605-9371-4c9e-9ab3-b7824e5af59f"]
                                          [:error/id #uuid"c5d4f45d-524c-4542-b702-4b1e02a62d69"]]
     :user-registration/email            "adsasdasd"
     :user-registration/password         "asdasd"
     :user-registration/confirm-password "asdasd"}
    #uuid"b7a51278-1a10-48da-b091-062188c90eea"
    {:user-registration/id               #uuid"b7a51278-1a10-48da-b091-062188c90eea"
     :user-registration/errors           [[:error/id #uuid"fa8da194-7105-43ae-aaaa-0634ebea094d"]]
     :user-registration/email            "test@example.com"
     :user-registration/password         "asdasd"
     :user-registration/confirm-password "asdasd"}}})

(defn env []
  {:state (atom *state-map*)})