#!/bin/bash

echo TRAVIS_TAG=$TRAVIS_TAG
echo TAG_BRANCH=$TAG_BRANCH

if [[ "$TRAVIS_PULL_REQUEST" != "false" || ("$TAG_BRANCH" != "master" && "$TAG_BRANCH" != "master-11") || "$TRAVIS_TAG" == "") ]]
then
  echo "No tag was made from master or master-11, skipping incrementing version."
  exit 0
fi

INCREMENT_EMAIL=${INCREMENT_EMAIL:='build@travis-ci.org'}

git config user.name "Travis CI"
git config user.email $INCREMENT_EMAIL

git remote add upstream "https://$GITHUB_TOKEN@github.com/martinfrancois/BoxplotFX.git"
git fetch upstream
git checkout $TAG_BRANCH

# increment version
mvn versions:set -DnewVersion=$TRAVIS_TAG -DoldVersion=* -DgroupId=* -DartifactId=*
# increment version of children modules
mvn versions:update-child-modules

git commit -am "increment version to ${TRAVIS_TAG}"
git push upstream $TAG_BRANCH