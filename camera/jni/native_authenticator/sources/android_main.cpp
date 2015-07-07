#include <stdio.h>
#include "common.h"
#include "omp.h"

#ifdef __cplusplus
extern "C" {
#endif

	JNIEXPORT void JNICALL Java_com_primitive_natives_primitive_OmpAction_nativeExec(JNIEnv *env,jobject obj);

	void Java_com_primitive_natives_primitive_OmpAction_nativeExec(JNIEnv *env,jobject obj)
	{
		LOGD("Java_com_primitive_natives_primitive_OmpAction_nativeExec");
		double ans = 0.0f;
#pragma omp parallel 
		{
#pragma omp for
			for (int i = 0; i < 320000; i++)
			{
				ans += i / 3.1415167856f;
				LOGD("%d:[%10f]" , i, ans);
			}
		}
	}

#ifdef __cplusplus
}
#endif
