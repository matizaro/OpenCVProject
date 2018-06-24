package com.example.matiz.opencvproject;

public class OpencvNative {
    static{
        System.loadLibrary("OpenCVLibrary");
    }
    public native static int convertGray(long matAddrRgba, long matAddrGray);
    public native static int faceDetection(long mAddrRGBa);

}
