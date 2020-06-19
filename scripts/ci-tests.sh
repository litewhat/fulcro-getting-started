#!/bin/bash

rm -rf target/test
shadow-cljs compile ci
node_modules/karma/bin/karma start --single-run