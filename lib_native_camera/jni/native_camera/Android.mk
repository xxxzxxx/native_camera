LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)

LOCAL_MODULE := $(subst jni/,, $(LOCAL_PATH))

LOCAL_CFLAGS := $(LOCAL_C_INCLUDES:%=-I%)
ifeq ($(TARGET_ARCH_ABI),x86)
#x86シリーズはfloat精度80bitを利用するため64bitを利用するように修正
#sse(hard fpu)を利用する場合は64bit固定のはずなのでしているする必要はない
	LOCAL_CFLAGS := -mfpmath=387 -msse2
endif
ifeq ($(TARGET_ARCH_ABI),x86_64)
	LOCAL_CFLAGS := -mfpmath=387 -msse2
endif

ifeq ($(TARGET_ARCH_ABI),armeabi)
endif
ifeq ($(TARGET_ARCH_ABI),armeabi-v7a-hard)
endif
ifeq ($(TARGET_ARCH_ABI),arm64-v8a)
endif


ifeq ($(APP_OPTIM),debug)
LOCAL_CFLAGS := -D__DEBUG__
endif

LOCAL_SRC_FILES_JNI_PREFIXED := $(wildcard $(LOCAL_PATH)/sources/*.cpp)
LOCAL_SRC_FILES_JNI_UNPREFIXED1 := $(subst jni/,, $(LOCAL_SRC_FILES_JNI_PREFIXED))
LOCAL_SRC_FILES_JNI_UNPREFIXED2 := $(subst $(LOCAL_MODULE)/, , $(LOCAL_SRC_FILES_JNI_UNPREFIXED1))
LOCAL_SRC_FILES := $(LOCAL_SRC_FILES_JNI_UNPREFIXED2)
LOCAL_C_INCLUDES += $(LOCAL_PATH)/headers

include $(BUILD_STATIC_LIBRARY)

$(info -----------------------------------------------)
$(info TARGET_ARCH_ABI  :$(TARGET_ARCH_ABI))
$(info LOCAL_C_INCLUDES :$(LOCAL_C_INCLUDES))
$(info LOCAL_CFLAGS     :$(LOCAL_CFLAGS))
$(info LOCAL_MODULE     :$(LOCAL_MODULE))
$(info LOCAL_SRC_FILES  :$(LOCAL_SRC_FILES))
$(info LOCAL_LDLIBS     :$(LOCAL_LDLIBS))
$(info LOCAL_PATH       :$(LOCAL_PATH))
$(info -----------------------------------------------)

