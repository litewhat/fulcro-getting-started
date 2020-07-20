(ns app.spec
  (:require #?(:clj [clojure.spec.alpha :as s]
               :cljs [cljs.spec.alpha :as s])))

(s/def :error/code keyword?)
(s/def :error/message string?)
(s/def :error/param keyword?)
(s/def :token/type #{:access :refresh})
(s/def :token/value string?)
(s/def :user/id uuid?)
(s/def :user/email string?)
(s/def :user/token (s/keys :req [:token/type :token/value]))
(s/def :user/tokens (s/coll-of :user/token :kind vector?))
(s/def :user/password string?)
(s/def :user/confirm-password string?)