#include "common.h"

#ifdef __cplusplus
extern "C" {
#endif

jbyteArray as_byte_array(JNIEnv* env,unsigned char* buf, int len)
{
    LOGI("as_byte_array");
    jbyteArray array = env->NewByteArray (len);
    env->SetByteArrayRegion (array, 0, len, reinterpret_cast<jbyte*>(buf));
    return array;
}
unsigned char* as_unsigned_char_array(JNIEnv* env,jbyteArray array)
{
    LOGI("as_unsigned_char_array");
    int len = env->GetArrayLength (array);
    unsigned char* buf = new unsigned char[len];
    env->GetByteArrayRegion (array, 0, len, reinterpret_cast<jbyte*>(buf));
    return buf;
}

#ifdef __cplusplus
}
#endif
