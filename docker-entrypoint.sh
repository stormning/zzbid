#!/usr/bin/env bash
export TESSDATA_PREFIX=/usr/local/share
export GIT_SSL_NO_VERIFY=true
GIT_URL=https://github.com/stormning/zzbid.git
ZZBID_SRC=/opt/zzbid/source
if [ -f $ZZBID_SRC/pom.xml ];then
    echo "start resetting ..."
    cd $ZZBID_SRC
    git reset --hard origin/master
    git pull
    else
    echo "start cloning ..."
    cd $ZZBID_SRC
    git clone $GIT_URL .
    git remote add origin $GIT_URL
fi
$ZZBID_SRC/mvnw clean install -Dmaven.test.skip=true
java -server -Xms1563M -Xmx1563M -Duser.timezone=Asia/Shanghai -jar $ZZBID_SRC/target/zzbid-0.0.1-SNAPSHOT.jar