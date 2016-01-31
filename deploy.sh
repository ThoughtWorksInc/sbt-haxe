#!/bin/bash

source ./secret/git_config.sh
sbt "release with-defaults"
