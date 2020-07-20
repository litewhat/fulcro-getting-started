
-- :name create-app-user-table
-- :command :execute
-- :result :raw
-- :doc Create app_user table
CREATE TABLE app_user (
    id          uuid DEFAULT uuid_generate_v4() PRIMARY KEY,
    email       varchar(255) NOT NULL UNIQUE,
    password    varchar(255) NOT NULL,
    created_at  timestamp NOT NULL DEFAULT current_timestamp,
    deleted_at  timestamp NULL
);

-- :name drop-app-user-table
-- :command :execute
-- :result :raw
-- :doc Drop user table
DROP TABLE IF EXISTS app_user RESTRICT;

-- :name create-token-type
-- :command :execute
-- :result :raw
-- :doc Create token_type
CREATE TYPE token_type AS ENUM ('access', 'refresh');

-- :name drop-token-type
-- :command :execute
-- :result :raw
-- :doc Create token_type
DROP TYPE IF EXISTS token_type;

-- :name add-token-type
-- :command :execute
-- :result :raw
-- :doc Add token type
ALTER TYPE token_type ADD VALUE /*~ (format "'%s'" (:value params)) ~*/;

-- :name create-token-table
-- :command :execute
-- :result :raw
-- :doc Create token table
CREATE TABLE token (
    id          uuid DEFAULT uuid_generate_v4() PRIMARY KEY,
    type        token_type NOT NULL,
    value       varchar(255) NOT NULL,
    invoked_at  timestamp NULL,
    created_at  timestamp NOT NULL DEFAULT current_timestamp
);

-- :name drop-token-table
-- :command :execute
-- :result :raw
-- :doc Drop token table
DROP TABLE IF EXISTS token RESTRICT;

-- :name insert-app-user
-- :command :returning-execute
-- :result :one
-- :doc Insert app_user record
INSERT INTO app_user (email, password)
    VALUES (:email, :password)
    RETURNING *;

-- :name batch-insert-app-user
-- :command :returning-execute
-- :result :many
-- :doc Insert many app_user records
INSERT INTO app_user (email, password)
    VALUES :tuple*:users
    RETURNING *;

-- :name get-app-user-by-id
-- :command :query
-- :result :one
-- :doc Select app_user with given id
SELECT * FROM app_user
    WHERE id = :id;

-- :name get-app-user-by-email
-- :command :query
-- :result :one
-- :doc Select app_user with given email
SELECT * FROM app_user
    WHERE email = :email;

-- :name get-all-app-users
-- :command :query
-- :result :many
-- :doc Select all records from app_user table
SELECT * FROM app_user;

-- :name get-app-users-by-emails
-- :command :query
-- :result :many
-- :doc Select all records from app_user table for given emails
SELECT * FROM app_user
    WHERE email IN :tuple:emails;

-- :name delete-app-user
-- :command :execute
-- :result :affected
-- :doc Removes app_user record for given id
DELETE FROM app_user
    WHERE id = :id;

-- :name batch-delete-app-user
-- :command :execute
-- :result :affected
-- :doc Removes app_user records for given list of ids
DELETE FROM app_user
    WHERE id IN :tuple:ids;

-- :name mark-deleted-app-user
-- :command :returning-execute
-- :result :one
-- :doc Set deleted_at timestamp to app_user record with given id
UPDATE app_user
    SET deleted_at = now()
    WHERE id = :id
    RETURNING *;

-- :name batch-mark-deleted-app-user
-- :command :returning-execute
-- :result :many
-- :doc Set deleted_at timestamp to app_user records with given ids
UPDATE app_user
    SET deleted_at = now()
    WHERE id IN :tuple:ids
    RETURNING *;

-- :name get-all-not-deleted-users
-- :command :query
-- :result :many
-- :doc Return all users with deleted_at equals to NULL
SELECT * FROM app_user
    WHERE deleted_at IS NULL;

-- :name insert-token
-- :command :returning-execute
-- :result :one
-- :doc Insert token record
INSERT INTO token (type, value)
    VALUES (/*~ (format "'%s'" (:type params)) ~*/, :value)
    RETURNING *;

-- :name get-token-by-id
-- :command :query
-- :result :one
-- :doc Select token for given id
SELECT * FROM token
    WHERE id = :id;

-- :name get-all-tokens
-- :command :query
-- :result :many
-- :doc Select all records from token table
SELECT * FROM token;
