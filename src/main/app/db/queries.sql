
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

-- :name get-all-enum-type-values
-- :command :query
-- :result :many
-- :doc List all values associated with given type name
SELECT n.nspname AS enum_schema,
       t.typname AS enum_name,
       e.enumlabel AS enum_value
FROM pg_type t
   JOIN pg_enum e ON t.oid = e.enumtypid
   JOIN pg_catalog.pg_namespace n ON n.oid = t.typnamespace
where t.typname = /*~ (format "'%s'" (:name params)) ~*/;
