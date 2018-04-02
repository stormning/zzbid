#!/usr/bin/env bash
export TESSDATA_PREFIX=/usr/local/share
export GIT_SSL_NO_VERIFY=true
GIT_URL=https://github.com/stormning/zzbid.git
ZZBID_SRC=/opt/zzbid/source
if [ -f $ZZBID_SRC/pom.xml ];then
    echo "start resetting ..."
    cd $ZZBID_SRC
    git reset --hard origin/master
    else
    echo "start cloning ..."
    cd $ZZBID_SRC
    git clone $GIT_URL .
    git remote add origin $GIT_URL
fi
$ZZBID_SRC/mvnw clean install
$ZZBID_SRC/mvnw spring-boot:run