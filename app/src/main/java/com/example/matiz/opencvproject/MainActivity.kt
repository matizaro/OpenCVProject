package com.example.matiz.opencvproject

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.SurfaceView
import kotlinx.android.synthetic.main.activity_main.*
import org.opencv.android.*
import java.nio.file.Files.size
import org.opencv.imgproc.Imgproc
import android.content.Context.MODE_PRIVATE
import org.opencv.core.*
import java.io.File
import java.io.FileOutputStream
import org.opencv.objdetect.CascadeClassifier
import org.opencv.core.Scalar
import org.opencv.core.Core
import org.opencv.android.OpenCVLoader
import org.opencv.core.MatOfRect
import org.opencv.core.Mat
import org.opencv.core.CvType
import org.opencv.android.JavaCameraView
import android.view.WindowManager
import org.opencv.android.LoaderCallbackInterface
import org.opencv.android.BaseLoaderCallback
import org.opencv.android.CameraBridgeViewBase






class MainActivity : AppCompatActivity(), CameraBridgeViewBase.CvCameraViewListener {
//
    val PERMISSION_REQUEST_CAMERA=12
    var openCvCameraView: CameraBridgeViewBase? = null
    var cascadeClassifier: CascadeClassifier? = null
    var eyeClassifier: CascadeClassifier? = null
    var grayscaleImage: Mat? = null
    var absoluteFaceSize: Double = 0.0

    private val mLoaderCallback = object : BaseLoaderCallback(this) {
        override fun onManagerConnected(status: Int) {
            when (status) {
                LoaderCallbackInterface.SUCCESS -> initializeOpenCVDependencies()
                else -> super.onManagerConnected(status)
            }
        }
    }
    private fun initalizeRawResource(id : Int, fileName : String) : File{
        val inputStream = resources.openRawResource(id)
        val cascadeDir = getDir("cascade", Context.MODE_PRIVATE)
        val mCascadeFile = File(cascadeDir, fileName)
        val os = FileOutputStream(mCascadeFile)


        val buffer = ByteArray(4096)
        var bytesRead: Int = inputStream.read(buffer)
        while (bytesRead != -1) {
            os.write(buffer, 0, bytesRead)
            bytesRead = inputStream.read(buffer)
        }
        inputStream.close()
        os.close()
        return mCascadeFile;
    }
    private fun initClassifier(file : File) : CascadeClassifier?{
        var cascadeClassifier : CascadeClassifier? = CascadeClassifier(file.path)
        cascadeClassifier?.load(file.path)
        if (cascadeClassifier!!.empty()) {
            Log.e("LOG_TAG", "Failed to load cascade classifier");
            cascadeClassifier = null;
        } else
            Log.i("LOG_TAG", "Loaded cascade classifier from " + file.path);
        return cascadeClassifier
    }
    private fun initializeOpenCVDependencies() {
        try {
            // Copy the resource into a temp file so OpenCV can load it
            // Load the cascade classifier
            cascadeClassifier = initClassifier(initalizeRawResource(R.raw.haarcascade_frontalface_alt, "cascade_frontalface.xml"))
            eyeClassifier = initClassifier(initalizeRawResource(R.raw.haarcascade_eye_tree_eyeglasses, "cascade_eyeglasses.xml"))

    } catch (e: Exception) {
            Log.e("OpenCVActivity", "Error loading cascade", e)
        }
        openCvCameraView?.setCameraIndex(1)
        // And we are ready to go
        openCvCameraView?.enableView()
    }

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED
          && ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE),
                    PERMISSION_REQUEST_CAMERA)
        }
        openCvCameraView = JavaCameraView(this, -1)
        setContentView(openCvCameraView)
        openCvCameraView?.setCvCameraViewListener(this)
    }

    override fun onCameraViewStarted(width: Int, height: Int) {
        grayscaleImage = Mat(height, width, CvType.CV_8UC4)

        // The faces will be a 20% of the height of the screen
        absoluteFaceSize = height * 0.2
    }

    override fun onCameraViewStopped() {}

    override fun onCameraFrame(aInputFrame: Mat): Mat {
        // Create a grayscale image
        var dp = 1.0;
        var minDist = 50.0
        var minRadius = 10
        var maxRadius = 200;
        val param1 = 70.0
        val param2 = 72.0
        Imgproc.cvtColor(aInputFrame, grayscaleImage, Imgproc.COLOR_RGBA2RGB)
        detectClassifier(aInputFrame, cascadeClassifier, Scalar(0.0, 255.0, 0.0, 255.0))
        var array = detectClassifier(aInputFrame, eyeClassifier, Scalar(255.0, 0.0, 0.0, 255.0))
        array?.let {
            if(array.size>0){
                array.forEach { item->
                    var submat = aInputFrame.submat(item)
                    var oldMat = Mat(submat.rows(), submat.cols(),
        CvType.CV_8UC1)
                    Imgproc.cvtColor(submat, oldMat, Imgproc.COLOR_BGR2GRAY);
                    Imgproc.HoughCircles(oldMat, oldMat, Imgproc.CV_HOUGH_GRADIENT, 2.0, 100.0, 30.0, 150.0,5,140)
                    Imgproc.blur(oldMat,oldMat, Size(3.0, 3.0))
                    val numberOfCircles = if (oldMat.rows() === 0) 0 else oldMat.cols()

                    for (i in 0 until numberOfCircles) {
                        val circleCoordinates = oldMat.get(0, i)

                        val x = circleCoordinates[0]+item.x
                        val y = circleCoordinates[1]+item.y

                        val center = Point(x, y)

                        val radius = circleCoordinates[2].toInt()

                        /* circle's outline */
//                        Imgproc.circle(aInputFrame, center, radius, Scalar(0.0,
//                                255.0, 0.0), 4)

                        /* circle's center outline */
                        Imgproc.rectangle(aInputFrame, Point(x - 5.0, y - 5.0),
                                Point(x + 5.0, y + 5.0),
                                Scalar(0.0, 128.0, 255.0), -1)
                    }
                }
            }
        }

        return aInputFrame
    }
    private fun detectClassifier(aInputFrame: Mat, classifier : CascadeClassifier?, color : Scalar): Array<Rect>? {
        val faces = MatOfRect()
        classifier?.detectMultiScale(grayscaleImage, faces, 1.1, 2, 2,
                Size(absoluteFaceSize, absoluteFaceSize), Size())

        // If there are any faces found, draw a rectangle around it
        val facesArray = faces.toArray()
        for (i in facesArray.indices)
            Imgproc.rectangle(aInputFrame, facesArray[i].tl(), facesArray[i].br(), color, 3)
        return facesArray;
    }
    public override fun onResume() {
        super.onResume()
        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_6, this, mLoaderCallback)
    }
}