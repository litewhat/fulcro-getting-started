
-- :name create-extension
-- :command :execute
-- :result :raw
-- :doc Create extension
create extension :i:name;

-- :name drop-extension
-- :command :execute
-- :result :raw
-- :doc Drop extension
drop extension if exists :i:name restrict;