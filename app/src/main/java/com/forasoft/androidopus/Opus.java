package com.forasoft.androidopus;

public class Opus {

    static {
        System.loadLibrary("jniopus");
    }


    public native boolean initEncoder(int samplingRate, int numberOfChannels, int frameSize, int maxFrameSize);

    public native int encodeBytes(short[] in, byte[] out);

    public native boolean releaseEncoder();

    public native boolean initDecoder(int samplingRate, int numberOfChannels, int frameSize);

    public native int decodeBytes(byte[] in, short[] out);

    public native boolean releaseDecoder();


    public int encode(short[] in, byte[] out) {
        return encodeBytes(in, out);
    }

    public int decode(byte[] encodedBuffer, short[] buffer) {
        return decodeBytes(encodedBuffer, buffer);
    }
}
