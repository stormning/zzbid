#!/usr/bin/env bash
export TESSDATA_PREFIX=/usr/local/share
GIT_URL=git@github.com:stormning/zzbid.git
if [ -d "./zzbid" ];then
    mkdir ./zzbid
    cd ./zzbid
    git clone $GIT_URL .
    git remote add origin $GIT_URL
    else
    cd ./zzbid
    git reset --hard origin/master
fi
./mvnw clean install
./mvnw spring-boot:run