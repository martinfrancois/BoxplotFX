#!/bin/bash

if [[ "$TRAVIS_PULL_REQUEST" == "true" || ("$TRAVIS_BRANCH" != "master" && "$TRAVIS_BRANCH" != "master-11") && "$TRAVIS_TAG" == "" ]]
then
  echo "No tag was made from master or master-11, skipping incrementing version."
  exit 0
fi

rev=$(git rev-parse --short HEAD)

CHANGELOG_EMAIL=${CHANGELOG_EMAIL:='build@travis-ci.org'}

git config user.name "Travis CI"
git config user.email $CHANGELOG_EMAIL

git remote add upstream "https://$GITHUB_TOKEN@github.com/martinfrancois/BoxplotFX.git"
git fetch upstream
git checkout $TRAVIS_BRANCH

# increment version
mvn versions:set -DnewVersion=$TRAVIS_TAG -DoldVersion=* -DgroupId=* -DartifactId=*
# increment version of children modules
mvn versions:update-child-modules

git commit -am "increment version to ${TRAVIS_TAG}"
git push upstream $TRAVIS_BRANCH