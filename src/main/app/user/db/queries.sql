
-- :name create-app-user-table
-- :command :execute
-- :result :raw
-- :doc Create app_user table
create table app_user (
    id          uuid primary key,
    email       varchar(255) not null,
    created_at  timestamp not null default current_timestamp,
    deleted_at  timestamp null
);

-- :name drop-app-user-table
-- :command :execute
-- :result :raw
-- :doc Drop user table
drop table if exists app_user restrict;
