#!/bin/bash

if [[ "$TRAVIS_PULL_REQUEST" == "true" || ("$TRAVIS_BRANCH" != "master" && "$TRAVIS_BRANCH" != "master-11") ]]
then
  echo "This commit was made against the $TRAVIS_BRANCH and not master or master-11! Changelog not updated!"
  exit 0
fi

gem install rack -v 1.6.4
gem install github_changelog_generator

rev=$(git rev-parse --short HEAD)

CHANGELOG_EMAIL=${CHANGELOG_EMAIL:='build@travis-ci.org'}

git config user.name "Travis CI"
git config user.email $CHANGELOG_EMAIL

git remote add upstream "https://$GITHUB_TOKEN@github.com/martinfrancois/BoxplotFX.git"
git fetch upstream
git checkout $TRAVIS_BRANCH

github_changelog_generator -t $GITHUB_TOKEN

git add -A CHANGELOG.md
git commit -m "updated changelog at ${rev}"
git push upstream $TRAVIS_BRANCH