#!/usr/bin/env bash
echo "Running mvn install"
mvn package -DskipTests
echo "Making zip"
cd ${TRAVIS_BUILD_DIR}/target/apidocs
zip -r ${TRAVIS_BUILD_DIR}/javadoc.zip .