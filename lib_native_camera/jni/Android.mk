LOCAL_PATH := $(call my-dir)
#include $(CLEAR_VARS)

# Just build the Android.mk files in the subdirs
include $(call all-subdir-makefiles)
