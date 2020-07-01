
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
create table person_list_people (
    list_id     varchar(255) references person_list (id)
                  on update restrict
                  on delete restrict,
    person_id   int references person (id)
                  on update restrict
                  on delete restrict,
    created_at  timestamp not null default current_timestamp,
    constraint  person_list_people_pkey primary key (list_id, person_id)
);

-- :name drop-person-list-people-table
-- :command :execute
-- :result :raw
-- :doc Drop person_list_person table
drop table if exists person_list_people restrict;

-- :name insert-person
-- :command :execute
-- :result :affected
-- :doc Insert person record
insert into person (name, age)
values (:name, :age);

-- :name get-all-people
-- :command :query
-- :result :many
-- :doc Select all records from person table
select * from person;

-- :name get-person-by-id
-- :command :query
-- :result :one
-- :doc Select person with given id
select * from person
where id = :id;

-- :name insert-person-list
-- :command :execute
-- :result :affected
-- :doc Insert person list record
insert into person_list (id)
values (:id);

-- :name get-person-list-by-id
-- :command :query
-- :result :one
-- :doc Select person with given id
select * from person_list
where id = :id;

-- :name get-all-person-lists
-- :command :query
-- :result :many
-- :doc Select all records from person table
select * from person_list;

-- :name add-person-to-list
-- :command :execute
-- :result :affected
-- :doc Adds person to person list
insert into person_list_people (list_id, person_id)
values (:list_id, :person_id);

-- :name add-people-to-list
-- :command :execute
-- :result :affected
-- :doc Add people to person list
insert into person_list_people (list_id, person_id)
values :tuple*:people;

-- :name get-people-by-list-id
-- :command :query
-- :result :many
-- :doc Select records representing people in person list
select * from person_list_people
where list_id = :list_id;
