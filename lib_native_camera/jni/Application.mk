APP_ABI := all
 
APP_OPTIM := release
APP_PLATFORM := android-9

APP_STL := gnustl_static

APP_USE_CPP0X := true
APP_CPPFLAGS += -std=c++11 -fexceptions -frtti -O3 -ffloat-store -fvisibility=hidden
APP_CFLAGS := 
NDK_TOOLCHAIN_VERSION := 4.8

$(info -----------------------------------------------)
$(info :$(APP_MODULES) complie options.)
$(info APP_MODULES            :$(APP_MODULES))
$(info APP_OPTIM              :$(APP_OPTIM))
$(info APP_STL                :$(APP_STL))
$(info APP_ABI                :$(APP_ABI))
$(info APP_CFLAGS             :$(APP_CFLAGS))
$(info NDK_DEBUG              :$(NDK_DEBUG))
$(info NDK_TOOLCHAIN_VERSION  :$(NDK_TOOLCHAIN_VERSION))
$(info -----------------------------------------------)
