//
// Created by Nova000381 on 2019/2/15 015.
//

#ifndef NATIVE_PLUGIN_ANDROID_LOG_H
#define NATIVE_PLUGIN_ANDROID_LOG_H

#endif //NATIVE_PLUGIN_ANDROID_LOG_H

#include <android/log.h>

#define TAG "wxg"

#define LOGI(FORMAT, ...) __android_log_print(ANDROID_LOG_INFO,TAG,FORMAT,##__VA_ARGS__);
#define LOGE(FORMAT, ...) __android_log_print(ANDROID_LOG_ERROR,TAG,FORMAT,##__VA_ARGS__);
#define LOGD(FORMAT, ...) __android_log_print(ANDROID_LOG_DEBUG,TAG,FORMAT,##__VA_ARGS__);