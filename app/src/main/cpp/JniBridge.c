#include "jni.h"
#include "nback.h"
#include "nback.c"


JNIEXPORT jintArray JNICALL
Java_com_example_lab1_NBackHelper_createNBackString(JNIEnv *env, jobject this,
                                                                  jint size, jint combinations,
                                                                  jint matchPercentage, jint nBack) {
    Nback s1;
    s1 = create(size, combinations, matchPercentage, nBack);

    // Create a new jintArray to store the content array
    jintArray contentArray = (*env)->NewIntArray(env, s1->size);

    // Check if allocation was successful
    if (contentArray == NULL) {
        // Handle allocation failure if needed
        return NULL;
    }

    // Set the content of the jintArray to the content array from the struct
    (*env)->SetIntArrayRegion(env, contentArray, 0, s1->size, s1->content);

    return contentArray;
}