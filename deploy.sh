#!/bin/bash

source ./secret/before_deploy.sh
sbt "release with-defaults"
