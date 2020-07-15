
-- :name create-extension
-- :command :execute
-- :result :raw
-- :doc Create extension
create extension :identifier:name;

-- :name drop-extension
-- :command :execute
-- :result :raw
-- :doc Drop extension
drop extension if exists :identifier:name restrict;