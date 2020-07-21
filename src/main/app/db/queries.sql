
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

-- :name get-all-tables
-- :command :query
-- :result :many
-- :doc List all tables
SELECT * FROM pg_catalog.pg_tables
    WHERE schemaname != 'pg_catalog' AND schemaname != 'information_schema';

-- :name get-all-data-types
-- :command :query
-- :result :many
-- :doc List all custom data types
SELECT n.nspname as schema, t.typname as type FROM pg_type t
    LEFT JOIN pg_catalog.pg_namespace n ON n.oid = t.typnamespace
    WHERE (t.typrelid = 0 OR (SELECT c.relkind = 'c' FROM pg_catalog.pg_class c
                                  WHERE c.oid = t.typrelid))
        AND NOT EXISTS (SELECT 1 FROM pg_catalog.pg_type el
                            WHERE el.oid = t.typelem AND el.typarray = t.oid)
        AND n.nspname NOT IN ('pg_catalog', 'information_schema');

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
    WHERE t.typname = /*~ (format "'%s'" (:name params)) ~*/;
