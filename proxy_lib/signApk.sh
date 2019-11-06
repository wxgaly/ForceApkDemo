#!/usr/bin/env sh
# 定义变量
PASSWORD=""
JKS_PATH=""
SINGED_PATH=""
UNSINGED_PATH=""
KEY_ALIAS=""

#参数赋值
PASSWORD=$1
JKS_PATH=$2
SINGED_PATH=$3
UNSINGED_PATH=$4
KEY_ALIAS=$5

#测试参数
#echo "$PASSWORD"
#echo "$JKS_PATH"
#echo "$SINGED_PATH"
#echo "$UNSINGED_PATH"
#echo "$KEY_ALIAS"

#命令行签名，自动输入密码
echo "$PASSWORD" | jarsigner -verbose -keystore "$JKS_PATH" -signedjar "$SINGED_PATH" "$UNSINGED_PATH" "$KEY_ALIAS"
