#ifndef __PMV_ANDROID_LOGGER_H__
#define __PMV_ANDROID_LOGGER_H__

#include <android/log.h>
#include <assert.h>
#include <time.h>

#define pmv_print_log(_Level,_Fmt, ...) __android_log_print(_Level,__FILE__,"(%s)[%d]:"_Fmt, __FUNCTION__ ,__LINE__ ,__VA_ARGS__)
#define pmv_verbose_log(...) 	 do{}while(0)
#define pmv_debug_log(_Fmt, ...) PrintLog(ANDROID_LOG_DEBUG,_Fmt,__VA_ARGS__)
#define pmv_info_log(_Fmt, ...)  PrintLog(ANDROID_LOG_INFO,_Fmt,__VA_ARGS__)
#define pmv_warn_log(_Fmt, ...)  PrintLog(ANDROID_LOG_WARN,_Fmt,__VA_ARGS__)
#define pmv_err_log(_Fmt, ...)   PrintLog(ANDROID_LOG_ERROR,_Fmt,__VA_ARGS__)
#define pmv_assert_log(_cond, _Fmt, ...) if (!_cond) __android_log_assert("(%s)[%d]:conditional!!"_Fmt, __FUNCTION__ ,__LINE__ ,__VA_ARGS__)
#define pmv_start_log() \
	clock_t start = clock();\
	info_log("%s started.." ,__FUNCTION__)
#define pmv_end_log() \
	double timecount = (double)(clock()-start)/CLOCKS_PER_SEC;\
	info_log(timecount >= 0.1\
		? "%s endl..[%2f] slow.. "\
		: "%s endl..[%2f]",__FUNCTION__,timecount\
	)
#endif //__PMV_ANDROID_LOGGER_H__

