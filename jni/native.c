#include <jni.h>
#include <string.h>
#include <android/log.h>
#include "trs.h"
#include "atrs.h"

#define DEBUG_TAG "Z80"


Uchar* memory;
int isRunning = 0;

static jobject obj;
static JNIEnv* env;
static jmethodID isRenderingMethodId;
static jmethodID updateScreenMethodId;
static jbyte* screenBuffer;


unsigned char trs_screen[2048];
int instructionsSinceLastScreenAccess;
int screenWasUpdated;

void check_for_screen_updates()
{
    instructionsSinceLastScreenAccess++;
    if (instructionsSinceLastScreenAccess >= SCREEN_UPDATE_THRESHOLD) {
    	if (screenWasUpdated) {
    		jboolean isRendering = (*env)->CallBooleanMethod(env, obj, isRenderingMethodId);
    		if (!isRendering) {
    			memcpy(screenBuffer, trs_screen, 0x3fff - 0x3c00 + 1);
    			(*env)->CallVoidMethod(env, obj, updateScreenMethodId);
    			screenWasUpdated = 0;
    		}
    	}
        instructionsSinceLastScreenAccess = 0;
    }
}


void Java_org_puder_trs80_Hardware_setROMSize(JNIEnv* e, jobject this, jint size)
{
	trs_rom_size = size;
}


void Java_org_puder_trs80_Z80ExecutionThread_bootTRS80(JNIEnv* e, jobject this, jint entryAddr, jbyteArray mem, jbyteArray screen)
{
    obj = this;
    env = e;
    jclass cls = (*env)->GetObjectClass(env, obj);
    isRenderingMethodId = (*env)->GetMethodID(env, cls, "isRendering", "()Z");
    if (isRenderingMethodId == 0) {
        __android_log_print(ANDROID_LOG_DEBUG, DEBUG_TAG, "NDK: isRendering not found");
        return;
    }

    updateScreenMethodId = (*env)->GetMethodID(env, cls, "updateScreen", "()V");
    if (updateScreenMethodId == 0) {
        __android_log_print(ANDROID_LOG_DEBUG, DEBUG_TAG, "NDK: updateScreen not found");
        return;
    }

    jboolean isCopy;
    memory = (Uchar*) (*env)->GetByteArrayElements(env, mem, &isCopy);
    if (isCopy) {
        __android_log_print(ANDROID_LOG_DEBUG, DEBUG_TAG, "NDK: didn't get copy of array");
        return;
    }

    screenBuffer = (*env)->GetByteArrayElements(env, screen, &isCopy);
    if (isCopy) {
        __android_log_print(ANDROID_LOG_DEBUG, DEBUG_TAG, "NDK: didn't get copy of array");
        return;
    }

    android_main(entryAddr);

    (*env)->ReleaseByteArrayElements(env, mem, (jbyte*) memory, JNI_COMMIT);
    (*env)->ReleaseByteArrayElements(env, mem, screenBuffer, JNI_COMMIT);
}

void Java_org_puder_trs80_Z80ExecutionThread_setRunning(JNIEnv* e, jobject this, jboolean run)
{
    isRunning = run;
}
