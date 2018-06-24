LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)

LOCAL_MODULE := app
LOCAL_LDFLAGS := -Wl,--build-id
LOCAL_SRC_FILES := \
	C:\Users\matiz\AndroidStudioProjects\OpenCVProject\app\src\main\jni\jniLibs\arm64-v8a\libopencv_java3.so \
	C:\Users\matiz\AndroidStudioProjects\OpenCVProject\app\src\main\jni\jniLibs\armeabi\libopencv_java3.so \
	C:\Users\matiz\AndroidStudioProjects\OpenCVProject\app\src\main\jni\jniLibs\armeabi-v7a\libopencv_java3.so \
	C:\Users\matiz\AndroidStudioProjects\OpenCVProject\app\src\main\jni\jniLibs\mips\libopencv_java3.so \
	C:\Users\matiz\AndroidStudioProjects\OpenCVProject\app\src\main\jni\jniLibs\mips64\libopencv_java3.so \
	C:\Users\matiz\AndroidStudioProjects\OpenCVProject\app\src\main\jni\jniLibs\x86\libopencv_java3.so \
	C:\Users\matiz\AndroidStudioProjects\OpenCVProject\app\src\main\jni\jniLibs\x86_64\libopencv_java3.so \

LOCAL_C_INCLUDES += C:\Users\matiz\AndroidStudioProjects\OpenCVProject\app\src\main\jni\jniLibs
LOCAL_C_INCLUDES += C:\Users\matiz\AndroidStudioProjects\OpenCVProject\app\src\debug\jni
LOCAL_C_INCLUDES += C:\Users\matiz\AndroidStudioProjects\OpenCVProject\app\src\main\jni

include $(BUILD_SHARED_LIBRARY)
