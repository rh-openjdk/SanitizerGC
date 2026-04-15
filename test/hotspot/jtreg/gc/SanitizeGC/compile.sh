#!/bin/bash

if [ -n "$JAVA_HOME" ]; then
  echo "Compiling SanitizeGC tests."
  "$JAVA_HOME/bin/javac" test/hotspot/jtreg/gc/SanitizeGC/*java
  "$JAVA_HOME/bin/javac" test/hotspot/jtreg/gc/SanitizeGC/benchmarks/*java
  "$JAVA_HOME/bin/javac" test/hotspot/jtreg/gc/SanitizeGC/shouldfail/*java
else
  echo "Please set JAVA_HOME."
  exit 1
fi
