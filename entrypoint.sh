#!/usr/bin/env bash
export TESSDATA_PREFIX=/usr/local/share
export GIT_SSL_NO_VERIFY=true
GIT_URL=git@github.com:stormning/zzbid.git
ZZBID_SRC=/opt/zzbid/source
if [[ -d $ZZBID_SRC ]];then
    mkdir $ZZBID_SRC
    cd $ZZBID_SRC
    git clone $GIT_URL .
    git remote add origin $GIT_URL
    else
    cd $ZZBID_SRC
    git reset --hard origin/master
fi
$ZZBID_SRC/mvnw clean install
$ZZBID_SRC/mvnw spring-boot:run