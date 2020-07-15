
-- :name create-app-user-table
-- :command :execute
-- :result :raw
-- :doc Create app_user table
CREATE TABLE app_user (
    id          uuid DEFAULT uuid_generate_v4() PRIMARY KEY,
    email       varchar(255) NOT NULL UNIQUE,
    created_at  timestamp NOT NULL DEFAULT current_timestamp,
    deleted_at  timestamp NULL
);

-- :name drop-app-user-table
-- :command :execute
-- :result :raw
-- :doc Drop user table
DROP TABLE IF EXISTS app_user RESTRICT;

-- :name insert-app-user
-- :command :returning-execute
-- :result :one
-- :doc Insert app_user record
INSERT INTO app_user (id, email)
VALUES (DEFAULT, :email)
RETURNING *;

-- :name batch-insert-app-user
-- :command :execute
-- :result :affected
-- :doc Insert many app_user records
INSERT INTO app_user (email)
VALUES :tuple*:users;

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

