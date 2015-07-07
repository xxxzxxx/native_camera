/*
 * Copyright 2010, Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

#ifndef TEXTDETECTION_JNI_COMMON_H
#define TEXTDETECTION_JNI_COMMON_H

#include <jni.h>
#include <android/log.h>
#include <assert.h>

#define LOG_TAG "native_authenticator(native)"
#define LOGV(...) __android_log_print(ANDROID_LOG_VERBOSE, LOG_TAG, __VA_ARGS__)
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__)
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)
#define LOGW(...) __android_log_print(ANDROID_LOG_WARN, LOG_TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)
#define LOG_ASSERT(_cond, ...) if (!_cond) __android_log_assert("conditional", LOG_TAG, __VA_ARGS__)

#ifdef __cplusplus
extern "C" {
#endif

jbyteArray as_byte_array(JNIEnv* env,unsigned char* buf, int len);
unsigned char* as_unsigned_char_array(JNIEnv* env,jbyteArray array);

#ifdef __cplusplus
}
#endif

#endif //TEXTDETECTION_JNI_COMMON_H endl
