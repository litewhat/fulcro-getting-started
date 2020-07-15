
-- :name create-extension
-- :command :execute
-- :result :raw
-- :doc Create extension
CREATE EXTENSION :identifier:name;

-- :name drop-extension
-- :command :execute
-- :result :raw
-- :doc Drop extension
DROP EXTENSION IF EXISTS :identifier:name RESTRICT;