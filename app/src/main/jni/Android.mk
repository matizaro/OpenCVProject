
	LOCAL_PATH := $(call my-dir)

	include $(CLEAR_VARS)

	#opencv
	OPENCVROOT:= C:\openssl-0.9.8k_X64\OpenCV-android-sdk
	OPENCV_CAMERA_MODULES:=on
	OPENCV_INSTALL_MODULES:=on
	OPENCV_LIB_TYPE:=SHARED
	include ${OPENCVROOT}/sdk/native/jni/OpenCV.mk

	LOCAL_SRC_FILES := com_example_matiz_opencvproject_OpencvNative.cpp

	LOCAL_LDLIBS += -ldl -llog
	LOCAL_MODULE := OpenCVLibrary


	include $(BUILD_SHARED_LIBRARY)