#!/bin/bash

cd /root/shadow/shadow-manage &&
mvn install -Dmaven.test.skip=true &&
cd /root/shadow/shadow-queue &&
mvn package -Dmaven.test.skip=true &&
cd /root/shadow/ShadowUtils &&
gradle makeJar &&
mv /root/shadow/shadow-queue/target/shadow-queue-0.0.1-S NAPSHOT-jar-with-dependencies.jar /root/shadow/package/server.jar &&
mv /root/shadow/ShadowUtils/app/build/libs/test.jar /ro ot/shadow/package/device.jar &&
cd /root/shadow/package &&
zip -r package.zip ./*
