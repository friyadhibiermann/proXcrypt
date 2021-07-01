
#include <list>
#include <vector>
#include <string.h>
#include <pthread.h>
#include <cstring>
#include <jni.h>
#include <unistd.h>
#include <jni.h>
#include <string>

#include <iostream>
#include <ESP/ESP.h>
#include <ESP/import.h>

#include "ESP/ESP.h"
#include "ESP/Hacks.h"
#include "ESP/struct.h"

using namespace std;

ESP espOverlay;
int type=1,utype=2;

extern "C"
JNIEXPORT void JNICALL
Java_com_oniversal_proxcrypt_Overlay_DrawOn(JNIEnv *env, jclass clazz, jobject espView, jobject canvas) {
    espOverlay = ESP(env, espView, canvas);
    if (espOverlay.isValid()){
        DrawESP(espOverlay, espOverlay.getWidth(), espOverlay.getHeight());
    }
}
extern "C"
JNIEXPORT void JNICALL
Java_com_oniversal_proxcrypt_Overlay_Close(JNIEnv *env, jobject thiz) {
    Close();
}
extern "C"
JNIEXPORT void JNICALL
Java_com_oniversal_proxcrypt_FloatLogo_SettingValue(JNIEnv *env, jobject thiz, jint setting_code,
                                                    jboolean value) {
    Close();
}

extern "C"
JNIEXPORT jboolean JNICALL
Java_com_oniversal_proxcrypt_Overlay_getReady(JNIEnv *env, jclass clazz, jint typeofgame) {
    // TODO: implement getReady()
    int sockCheck=1;
    if (!Create()) {
        perror("Creation failed");
        return false;
    }
    setsockopt(sock,SOL_SOCKET,SO_REUSEADDR,&sockCheck, sizeof(int));
    if (!Connect()) {
        perror("Connect failed");
        return false;
    }

    if (Connect()) {
        SetValue sv{};
        sv.mode=typeofgame;
        sv.type=utype;
        send((void*)&sv,sizeof(sv));
        printf("Connected");
        // Close();
        return true;
    }
}
/*
JNIEXPORT jint JNICALL
JNI_OnLoad(JavaVM *vm, void *reserved) {
    JNIEnv *globalEnv;
    vm->GetEnv((void **) &globalEnv, JNI_VERSION_1_6);

    return JNI_VERSION_1_6;
}
 */
