
-- :name create-person-table
-- :command :execute
-- :result :raw
-- :doc Create person table
CREATE TABLE person (
    id          serial PRIMARY KEY,
    name        varchar(255) NOT NULL,
    age         integer NOT NULL,
    created_at  timestamp NOT NULL DEFAULT current_timestamp
);

-- :name drop-person-table
-- :command :execute
-- :result :raw
-- :doc Drop person table
DROP TABLE IF EXISTS person RESTRICT;

-- :name create-person-list-table
-- :command :execute
-- :result :raw
-- :doc Create person_list table
CREATE TABLE person_list (
    id          varchar(255) PRIMARY KEY,
    created_at  timestamp NOT NULL DEFAULT current_timestamp
);

-- :name drop-person-list-table
-- :command :execute
-- :result :raw
-- :doc Drop person_list table
DROP TABLE IF EXISTS person_list RESTRICT;

-- :name create-person-list-people-table
-- :command :execute
-- :result :raw
-- :doc Create person_list_person table
CREATE TABLE person_list_people (
    list_id     varchar(255) REFERENCES person_list (id)
                  ON UPDATE RESTRICT
                  ON DELETE RESTRICT,
    person_id   int REFERENCES person (id)
                  ON UPDATE RESTRICT
                  ON DELETE RESTRICT,
    created_at  timestamp NOT NULL DEFAULT current_timestamp,
    CONSTRAINT  person_list_people_pkey PRIMARY KEY (list_id, person_id)
);

-- :name drop-person-list-people-table
-- :command :execute
-- :result :raw
-- :doc Drop person_list_person table
DROP TABLE IF EXISTS person_list_people RESTRICT;

-- :name insert-person
-- :command :returning-execute
-- :result :one
-- :doc Insert person record
INSERT INTO person (name, age)
    VALUES (:name, :age)
    RETURNING *;

-- :name batch-insert-person
-- :command :returning-execute
-- :result :many
-- :doc Insert many person records
INSERT INTO person (name, age)
    VALUES :tuple*:people
    RETURNING *;

-- :name get-all-people
-- :command :query
-- :result :many
-- :doc Select all records from person table
SELECT * FROM person;

-- :name get-person-by-id
-- :command :query
-- :result :one
-- :doc Select person with given id
SELECT * FROM person
    WHERE id = :id;

-- :name insert-person-list
-- :command :returning-execute
-- :result :one
-- :doc Insert person_list record
INSERT INTO person_list (id)
    VALUES (:id)
    RETURNING *;

-- :name batch-insert-person-list
-- :command :returning-execute
-- :result :many
-- :doc Insert many person_list records
INSERT INTO person_list (id)
    VALUES :tuple*:person_lists
    RETURNING *;

-- :name get-person-list-by-id
-- :command :query
-- :result :one
-- :doc Select person with given id
SELECT * FROM person_list
    WHERE id = :id;

-- :name get-all-person-lists
-- :command :query
-- :result :many
-- :doc Select all records from person table
SELECT * FROM person_list;

-- :name add-person-to-list
-- :command :returning-execute
-- :result :one
-- :doc Adds person to person list
INSERT INTO person_list_people (list_id, person_id)
    VALUES (:list_id, :person_id)
    RETURNING *;

-- :name add-people-to-list
-- :command :returning-execute
-- :result :many
-- :doc Add people to person list
INSERT INTO person_list_people (list_id, person_id)
    VALUES :tuple*:people
    RETURNING *;

-- :name get-people-by-list-id
-- :command :query
-- :result :many
-- :doc Select records representing people in person list
SELECT * FROM person_list_people
    WHERE list_id = :list_id;

-- :name remove-person-from-list
-- :command :execute
-- :result :affected
-- :doc Removes person from person list
DELETE FROM person_list_people
    WHERE list_id = :list_id AND person_id = :person_id;