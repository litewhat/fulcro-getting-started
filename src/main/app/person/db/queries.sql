
-- :name create-person-table
-- :command :execute
-- :result :raw
-- :doc Create person table
create table person (
    id          serial primary key,
    name        varchar(255) not null,
    age         integer not null,
    created_at  timestamp not null default current_timestamp
);

-- :name drop-person-table
-- :command :execute
-- :result :raw
-- :doc Drop person table
drop table if exists person restrict;

-- :name create-person-list-table
-- :command :execute
-- :result :raw
-- :doc Create person_list table
create table person_list (
    id          varchar(255) primary key,
    created_at  timestamp not null default current_timestamp
);

-- :name drop-person-list-table
-- :command :execute
-- :result :raw
-- :doc Drop person_list table
drop table if exists person_list restrict;

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
-- :command :execute
-- :result :affected
-- :doc Insert person record
INSERT INTO person (name, age)
VALUES (:name, :age);

-- :name batch-insert-person
-- :command :execute
-- :result :affected
-- :doc Insert many person records
INSERT INTO person (name, age)
VALUES :tuple*:people;

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
-- :command :execute
-- :result :affected
-- :doc Insert person_list record
INSERT INTO person_list (id)
VALUES (:id);

-- :name batch-insert-person-list
-- :command :execute
-- :result :affected
-- :doc Insert many person_list records
INSERT INTO person_list (id)
VALUES :tuple*:person_lists;

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
-- :command :execute
-- :result :affected
-- :doc Adds person to person list
INSERT INTO person_list_people (list_id, person_id)
VALUES (:list_id, :person_id);

-- :name add-people-to-list
-- :command :execute
-- :result :affected
-- :doc Add people to person list
INSERT INTO person_list_people (list_id, person_id)
VALUES :tuple*:people;

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