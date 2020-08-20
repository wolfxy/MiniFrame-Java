#!/bin/sh
source select-jdk-1.7.sh
ant -f build-service.xml
rm -fr File-System/lib/mini-jserver-frame.jar
cp dest/Mini-Service/mini-jserver-frame.jar File-System/lib
