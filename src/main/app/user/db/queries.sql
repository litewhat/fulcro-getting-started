
-- :name create-app-user-table
-- :command :execute
-- :result :raw
-- :doc Create app_user table
create table app_user (
    id          uuid default uuid_generate_v4() primary key,
    email       varchar(255) not null unique,
    created_at  timestamp not null default current_timestamp,
    deleted_at  timestamp null
);

-- :name drop-app-user-table
-- :command :execute
-- :result :raw
-- :doc Drop user table
drop table if exists app_user restrict;

-- :name insert-app-user
-- :command :returning-execute
-- :result :one
-- :doc Insert person record
insert into app_user (id, email)
    values (default, :email)
    returning id;