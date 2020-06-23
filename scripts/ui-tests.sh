#!/bin/bash

rm -rf target/test/browser
mkdir -p target/test/browser
cp resources/public/index.html target/test/browser/index.html
shadow-cljs compile ui-test
python3 -m webbrowser -t "http://localhost:8022"
