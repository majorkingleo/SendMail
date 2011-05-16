#!/bin/bash

#export LANG=de_AT.UTF-8
#export LC_ALL=POSIX.UTF-8
export LC_ALL="de_AT.UTF-8"
#locale >> /tmp/locale.log

exec /usr/bin/java $*
