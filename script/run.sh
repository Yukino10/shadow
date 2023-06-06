#!/bin/bash

nohup java -jar project/shadow-web-0.0.1-SNAPSHOT.jar >> log/web.log 2>&1 &
