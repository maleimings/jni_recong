#include <stdio.h>
#include <stdlib.h>
#include <jni.h>
#include <android/log.h>
#include "xxplateresult.h"



#define LOG_TAG "recong_lib"
#define LOGV(...) __android_log_print(ANDROID_LOG_SILENT, LOG_TAG, __VA_ARGS__)
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)
#define JNI_ERR (-1)

#ifdef __cplusplus
extern "C" {
#endif
    char* Jstring2CStr(JNIEnv*   env,   jstring   jstr);
    jstring CStr2Jstring( JNIEnv* env, const char* pat );

	jstring doRecongFileNative(JNIEnv *env, jobject thiz, jstring filepath, jint x, jint y, jstring defaultvalue)
	{
		char *path= Jstring2CStr(env, filepath);
		char *result = (char*)malloc(1024);
		RecordFile(path, &result, 0);

		return CStr2Jstring(env, result);
	}

	jstring doRecongByteNative(JNIEnv *env, jobject thiz, jbyteArray bytedata, jint x, jint y, jstring defaultvalue)
	{
		char* data = (char*)env->GetByteArrayElements(bytedata, 0);
		char *result = (char*)malloc(1024);
		RecordStream(data, &result, 0);
		return CStr2Jstring(env, result);
	}

	jint initNative(JNIEnv *env, jobject thiz, jstring license)
	{
		char *lis= Jstring2CStr(env, license);
		return Init(lis);
	}

	jint releaseNative(JNIEnv *env, jobject thiz)
	{
		return FInit();
	}


	static JNINativeMethod s_methods[] = {
	{"doRecongFile", "(Ljava/lang/String;IILjava/lang/String;)Ljava/lang/String;", (void*)doRecongFileNative},
	{"doRecongData", "([BIILjava/lang/String;)Ljava/lang/String;", (void*)doRecongByteNative},
	{"init", "(Ljava/lang/String;)I", (void*)initNative},
	{"release", "()I", (void*)releaseNative},
	};

	int JNI_OnLoad(JavaVM* vm, void* reserved)
	{
		LOGI("JNI_OnLoad");
		JNIEnv* env = NULL;
	   if (vm->GetEnv(reinterpret_cast<void**>(&env), JNI_VERSION_1_6) != JNI_OK)
	   {
		  return JNI_ERR;
	   }

	   jclass cls = env->FindClass("com/example/recongapi/RecongAPI");
	   if (cls == NULL)
	   {
		  return JNI_ERR;
	   }

	   int len = sizeof(s_methods) / sizeof(s_methods[0]);
	   if (env->RegisterNatives(cls, s_methods, len) < 0)
	   {
		  return JNI_ERR;
	   }

	   return JNI_VERSION_1_4;
	}

	char* Jstring2CStr(JNIEnv*   env,   jstring   javaString)
	{
		char*   rtn   =   NULL;
		jclass   clsstring   =   env->FindClass("java/lang/String");
		jstring   strencode   =   env->NewStringUTF("utf-8");
		jmethodID   mid   =   env->GetMethodID(clsstring,   "getBytes",   "(Ljava/lang/String;)[B");
		jbyteArray   barr=   (jbyteArray)env->CallObjectMethod(javaString,mid,strencode);
		jsize   alen   =   env->GetArrayLength(barr);
		jbyte*   ba   =   env->GetByteArrayElements(barr,JNI_FALSE);
		if(alen   >   0)
		{
		rtn   =   (char*)malloc(alen+1);         //new   char[alen+1];
		memcpy(rtn,ba,alen);
		rtn[alen]=0;
		}
		env->ReleaseByteArrayElements(barr,ba,0);
		return rtn;

	}

	jstring CStr2Jstring( JNIEnv* env, const char* pat )
	{

	jclass strClass = (env)->FindClass("java/lang/String");


	jmethodID ctorID = (env)->GetMethodID(strClass, "<init>", "([BLjava/lang/String;)V");

	jbyteArray bytes = (env)->NewByteArray((jsize)strlen(pat));

	(env)->SetByteArrayRegion(bytes, 0, (jsize)strlen(pat), (jbyte*)pat);

	jstring encoding = (env)->NewStringUTF("utf-8");

	return (jstring)(env)->NewObject(strClass, ctorID, bytes, encoding);

	}

#ifdef __cplusplus
}
#endif
