
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
    id          serial primary key,
    created_at  timestamp not null default current_timestamp
);

-- :name drop-person-list-table
-- :command :execute
-- :result :raw
-- :doc Drop person_list table
drop table if exists person_list restrict;

-- :name create-person-list-person-table
-- :command :execute
-- :result :raw
-- :doc Create person_list_person table
create table person_list_person (
    list_id     int references person_list (id)
                  on update restrict
                  on delete restrict,
    person_id   int references person (id)
                  on update restrict
                  on delete restrict,
    created_at  timestamp not null default current_timestamp,
    constraint  person_list_person_pkey primary key (list_id, person_id)
);

-- :name drop-person-list-person-table
-- :command :execute
-- :result :raw
-- :doc Drop person_list_person table
drop table if exists person_list_person restrict;

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
-- :doc Select all records from person table
select * from person
where id = :id;