#!/bin/bash

cd /root/code/versionA/shadow &&
git pull &&
cd /root/code/versionA/ShadowUtils &&
git pull &&
cd /root/code/versionB/shadow &&
git pull &&
cd /root/code/versionB/ShadowUtils &&
git pull &&
cd /root/code/versionC/shadow &&
git pull &&
cd /root/code/versionC/ShadowUtils &&
git pull &&
rm -rf /root/shadow/versionA/shadow-* &&
rm -rf /root/shadow/versionA/ShadowUtils &&
rm -rf /root/shadow/versionB/shadow-* &&
rm -rf /root/shadow/versionB/ShadowUtils &&
rm -rf /root/shadow/versionC/shadow-* &&
rm -rf /root/shadow/versionC/ShadowUtils &&
cp -r /root/code/versionA/shadow/shadow-manage /root/shadow/versionA &&
cp -r /root/code/versionA/shadow/shadow-queue /root/shadow/versionA &&
cp -r /root/code/versionA/ShadowUtils /root/shadow/versionA &&
cp -r /root/code/versionB/shadow/shadow-manage /root/shadow/versionB &&
cp -r /root/code/versionB/ShadowUtils /root/shadow/versionB
cp -r /root/code/versionC/shadow/shadow-manage /root/shadow/versionC &&
cp -r /root/code/versionC/shadow/shadow-queue /root/shadow/versionC &&
cp -r /root/code/versionC/ShadowUtils /root/shadow/versionC