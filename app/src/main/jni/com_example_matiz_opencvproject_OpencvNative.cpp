#include <com_example_matiz_opencvproject_OpencvNative.h>
#include <android/log.h>

JNIEXPORT jint JNICALL Java_com_example_matiz_opencvproject_OpencvNative_convertGray
  (JNIEnv *, jclass, jlong addrRgba, jlong addrGray){
    Mat& rgb = *(Mat*)addrRgba;
    Mat& gray = *(Mat*)addrGray;

    return (jint)toGray(rgb, gray);
}

int toGray(Mat img, Mat& gray){
    cvtColor(img, gray, CV_RGBA2GRAY);
    if(gray.rows==img.rows && gray.cols==img.cols){
        return 1;
    }
    return 0;
}
JNIEXPORT void JNICALL Java_com_example_matiz_opencvproject_OpencvNative_faceDetection
  (JNIEnv *env, jclass, jlong addrRgba){
    Mat& rgb = *(Mat*)addrRgba;
    detectFace(rgb);
}

void detectFace(Mat& frame){
     /** Global variables */
    String face_cascade_name = "/storage/emulated/0/data/haarcascade_frontalface_alt.xml";
    String eyes_cascade_name = "/storage/emulated/0/data/haarcascade_eye_tree_eyeglasses.xml";
    CascadeClassifier face_cascade;
    CascadeClassifier eyes_cascade;
    string window_name = "Capture - Face detection";
    RNG rng(12345);

    if( !face_cascade.load( face_cascade_name ) ){ __android_log_print(ANDROID_LOG_DEBUG, "LOG_TAG", "--(!)Error loading\n");return;};
    if( !eyes_cascade.load( eyes_cascade_name ) ){ __android_log_print(ANDROID_LOG_DEBUG, "LOG_TAG", "--(!)Error loading\n");return;};
    std::vector<Rect> faces;
    Mat frame_gray;

    cvtColor( frame, frame_gray, CV_BGR2GRAY );
    equalizeHist( frame_gray, frame_gray );
    face_cascade.detectMultiScale( frame_gray, faces, 1.1, 2, 0|CV_HAAR_SCALE_IMAGE, Size(30, 30) );

    for( size_t i = 0; i < faces.size(); i++ )
    {
        Point center( faces[i].x + faces[i].width*0.5, faces[i].y + faces[i].height*0.5 );
        ellipse( frame, center, Size( faces[i].width*0.5, faces[i].height*0.5), 0, 0, 360, Scalar( 255, 0, 255 ), 4, 8, 0 );

        Mat faceROI = frame_gray( faces[i] );
        std::vector<Rect> eyes;

        //-- In each face, detect eyes
        eyes_cascade.detectMultiScale( faceROI, eyes, 1.1, 2, 0 |CV_HAAR_SCALE_IMAGE, Size(30, 30) );

        for( size_t j = 0; j < eyes.size(); j++ )
        {
            Point center( faces[i].x + eyes[j].x + eyes[j].width*0.5, faces[i].y + eyes[j].y + eyes[j].height*0.5 );
            int radius = cvRound( (eyes[j].width + eyes[j].height)*0.25 );
            circle( frame, center, radius, Scalar( 255, 0, 0 ), 4, 8, 0 );
        }
    }
}