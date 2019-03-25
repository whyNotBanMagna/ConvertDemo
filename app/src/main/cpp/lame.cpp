#include <jni.h>
#include <string>
#include "lame.h"
#include "Mp3Encoder.h"

Mp3Encoder *encoder=NULL;

extern "C"
JNIEXPORT jstring JNICALL
Java_com_we_convertdemo_Convert_stringFromJNI(JNIEnv *env, jobject) {

    std::string hello = "Hello from C++";

    return env->NewStringUTF(get_lame_version());
}

extern "C"
JNIEXPORT void JNICALL
Java_com_we_convertdemo_Convert_init(JNIEnv *env, jobject instance, jstring pcmPath_,
                                     jint audioChannels, jint bitRate, jint sampleRate,
                                     jstring mp3Path_) {
    const char *pcmPath = env->GetStringUTFChars(pcmPath_, 0);
    const char *mp3Path = env->GetStringUTFChars(mp3Path_, 0);

    encoder = new Mp3Encoder();
    encoder->Init(pcmPath,audioChannels,bitRate,sampleRate,mp3Path);


    env->ReleaseStringUTFChars(pcmPath_, pcmPath);
    env->ReleaseStringUTFChars(mp3Path_, mp3Path);

}

extern "C"
JNIEXPORT void JNICALL
Java_com_we_convertdemo_Convert_encode(JNIEnv *env, jobject instance) {
    encoder->Encode();
}

extern "C"
JNIEXPORT void JNICALL
Java_com_we_convertdemo_Convert_destroy(JNIEnv *env, jobject instance) {
    encoder->Destory();
}