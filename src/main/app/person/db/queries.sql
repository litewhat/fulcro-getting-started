
-- :name create-person-table
-- :command :execute
-- :result :raw
-- :doc Create person table
-- another comment
create table person (
    id          serial primary key,
    name        varchar(255) not null,
    age         integer not null,
    created_at  timestamp not null default current_timestamp
)

-- :name drop-person-table
-- :command execute
-- :result :raw
-- :doc Drop person table
drop table if exists person restrict;
