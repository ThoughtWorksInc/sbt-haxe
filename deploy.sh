#!/bin/bash

travis_retry git clone --depth=1 $secret_git ./secret
cat ./secret/sonatype.sbt >> ./local.sbt

git log --max-count=1 --format=format:%an | xargs -0 -n 1 git config --global -- user.name
git log --max-count=1 --format=format:%ae | xargs -0 -n 1 git config --global -- user.email

git config --global push.default simple

git branch --force "$TRAVIS_BRANCH"
git checkout "$TRAVIS_BRANCH"
git config remote.origin.url git@github.com:"$TRAVIS_REPO_SLUG".git

eval "$(ssh-agent -s)"
ssh-add ./secret/id_rsa

sbt "release with-defaults"
