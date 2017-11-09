#!/bin/bash

cd `dirname $0`
BIN_DIR=`pwd`
cd ..
DEPLOY_DIR=`pwd`
SERVER_NAME=`echo ${DEPLOY_DIR}`
NOW_TIME=`date +"%Y-%m-%d %H:%M:%S"`  

echo $NOW_TIME $SERVER_NAME

PIDS=`ps -ef | grep java | grep "$DEPLOY_DIR" |awk '{print $2}'`
if [ -n "$PIDS" ]; then
	echo "The $SERVER_NAME is runing ..."
	echo "PID: $PIDS"
	exit 1
fi

sh $BIN_DIR/start.sh

OPLOG_DIR=$DEPLOY_DIR/oplog
if [ ! -d $OPLOG_DIR ]; then
    mkdir $OPLOG_DIR
fi

echo "waiting 60 second ..."

sleep 60

PIDS=`ps -ef | grep java | grep "$DEPLOY_DIR" |awk '{print $2}'`
LOCAL_IP=`ifconfig $1|sed -n 2p|awk  '{ print $2 }'|awk -F : '{ print $2 }'`
#PORT=`netstat -naop | grep "${PIDS}" | awk 'NR==1{print $4}' | awk -F : '{ print $2 }'`

DUBBO_DIR=$DEPLOY_DIR/conf/META-INF/conf/dubbo-private.properties
chmod +x $DUBBO_DIR
PORT=`sed -n '/dubbo.dubbo.port=/'p $DUBBO_DIR | sed 's/dubbo.dubbo.port=//'`


LOG_CONTENT=''

if [ -n "$PIDS" ]; then
	echo "The $SERVER_NAME start success"
	LOG_CONTENT="OSSALARM:LOGUCL_LOGKEY=LOGUCL_APPSTARTSUCCESS&dateTime=${NOW_TIME}&event=start&ip=${LOCAL_IP}&port=${PORT}&appName=${DEPLOY_DIR}&result=1&info=\"restart success\""
else
	echo "The $SERVER_NAME start failed"
	LOG_CONTENT="OSSALARM:LOGUCL_LOGKEY=LOGUCL_APPSTARTFAIL&dateTime=${NOW_TIME}&event=start&ip=${LOCAL_IP}&port=${PORT}&appName=${DEPLOY_DIR}&result=2&info=\"restart failure\""
fi

echo $LOG_CONTENT >> $OPLOG_DIR/op.log