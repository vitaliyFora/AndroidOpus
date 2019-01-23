#include <jni.h>
#include <silk/define.h>
#include <include/opus.h>
#include <malloc.h>
#include <android/log.h>


char logMsg[255];
OpusEncoder *enc;
OpusDecoder *dec;

opus_int32 SAMPLING_RATE;
int CHANNELS;
int APPLICATION_TYPE = OPUS_APPLICATION_VOIP;
int FRAME_SIZE;
int MAX_FRAME_SIZE;
int BITRATE;
const int MAX_PAYLOAD_BYTES = 1500;

JNIEXPORT jboolean JNICALL
Java_com_forasoft_androidopus_Opus_initEncoder(JNIEnv *env, jobject instance, jint samplingRate,
                                               jint numberOfChannels,
                                               jint frameSize, jint maxFrameSize) {

    FRAME_SIZE = frameSize;
    SAMPLING_RATE = samplingRate;
    CHANNELS = numberOfChannels;
    MAX_FRAME_SIZE = maxFrameSize;

    int error;
    int size;

    size = opus_encoder_get_size(1);
    enc = malloc(size);
    error = opus_encoder_init(enc, SAMPLING_RATE, CHANNELS, APPLICATION_TYPE);

    if (error < 0) {
        sprintf(logMsg, "Initialized Encoder with ErrorCode: %d", error);
        __android_log_write(ANDROID_LOG_DEBUG, "Native Code:", logMsg);
        return JNI_FALSE;
    }

    return JNI_TRUE;

}

JNIEXPORT jint JNICALL
Java_com_forasoft_androidopus_Opus_encodeBytes(JNIEnv *env, jobject instance, jshortArray in,
                                               jbyteArray out) {

    jint inputArraySize = (*env)->GetArrayLength(env, in);
    jint outputArraySize = (*env)->GetArrayLength(env, out);

    jshort *pcm = (*env)->GetShortArrayElements(env, in, 0);

    unsigned char *data = (unsigned char *) calloc(MAX_FRAME_SIZE, sizeof(unsigned char));
    int dataArraySize = opus_encode(enc, pcm, FRAME_SIZE, data, MAX_FRAME_SIZE);

    if (dataArraySize >= 0) {
        if (dataArraySize <= outputArraySize) {
            (*env)->SetByteArrayRegion(env, out, 0, dataArraySize, (jbyte *) data);
        } else {
            sprintf(logMsg, "Output array of size: %d to small for storing encoded data.",
                    outputArraySize);
            __android_log_write(ANDROID_LOG_DEBUG, "Native Code:", logMsg);

            return -1;
        }
    }

    (*env)->ReleaseShortArrayElements(env, in, pcm, JNI_ABORT);
    (*env)->ReleaseByteArrayElements(env, out, data, JNI_ABORT);

    return dataArraySize;
}

JNIEXPORT jboolean JNICALL
Java_com_forasoft_androidopus_Opus_releaseEncoder(JNIEnv *env, jobject instance) {

    opus_encoder_destroy(enc);

    return 1;
}

JNIEXPORT jboolean JNICALL
Java_com_forasoft_androidopus_Opus_initDecoder(JNIEnv *env, jobject instance, jint samplingRate,
                                               jint numberOfChannels, jint frameSize) {

    FRAME_SIZE = frameSize;
    SAMPLING_RATE = samplingRate;
    CHANNELS = numberOfChannels;

    int size;
    int error;

    size = opus_decoder_get_size(CHANNELS);
    dec = malloc(size);
    error = opus_decoder_init(dec, SAMPLING_RATE, CHANNELS);

    sprintf(logMsg, "Initialized Decoder with ErrorCode: %d", error);
    __android_log_write(ANDROID_LOG_DEBUG, "Native Code:", logMsg);

    return error;

}

JNIEXPORT jint JNICALL
Java_com_forasoft_androidopus_Opus_decodeBytes(JNIEnv *env, jobject instance, jbyteArray in,
                                               jshortArray out) {

    jint inputArraySize = (*env)->GetArrayLength(env, in);
    jint outputArraySize = (*env)->GetArrayLength(env, out);

    jbyte *encodedData = (*env)->GetByteArrayElements(env, in, 0);
    opus_int16 *data = (opus_int16 *) calloc(FRAME_SIZE, sizeof(opus_int16));
    int decodedDataArraySize = opus_decode(dec, encodedData, inputArraySize, data, FRAME_SIZE, 0);

    if (decodedDataArraySize >= 0) {
        if (decodedDataArraySize <= outputArraySize) {
            (*env)->SetShortArrayRegion(env, out, 0, decodedDataArraySize, data);
        } else {
            sprintf(logMsg, "Output array of size: %d to small for storing encoded data.",
                    outputArraySize);
            __android_log_write(ANDROID_LOG_DEBUG, "Native Code:", logMsg);

            return -1;
        }
    }

    (*env)->ReleaseByteArrayElements(env, in, encodedData, JNI_ABORT);

    return decodedDataArraySize;
}

JNIEXPORT jboolean JNICALL
Java_com_forasoft_androidopus_Opus_releaseDecoder(JNIEnv *env, jobject instance) {

    opus_decoder_destroy(dec);

    return 1;
}

