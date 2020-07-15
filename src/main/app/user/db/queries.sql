
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
-- :doc Insert app_user record
insert into app_user (id, email)
    values (default, :email)
    returning id;

-- :name batch-insert-app-user
-- :command :execute
-- :result :affected
-- :doc Insert many app_user records
insert into app_user (email)
values :tuple*:users;

-- :name get-app-user-by-id
-- :command :query
-- :result :one
-- :doc Select app_user with given id
select * from app_user
where id = :id;

-- :name get-app-user-by-email
-- :command :query
-- :result :one
-- :doc Select app_user with given email
select * from app_user
where email = :email;

-- :name get-all-app-users
-- :command :query
-- :result :many
-- :doc Select all records from app_user table
select * from app_user;

-- :name delete-app-user
-- :command :execute
-- :result :affected
-- :doc Removes app_user record for given id
delete from app_user
where id = :id;

-- :name batch-delete-app-user
-- :command :execute
-- :result :affected
-- :doc Removes app_user records for given list of ids
delete from app_user
where id in :tuple:ids;

