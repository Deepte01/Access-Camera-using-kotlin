package com.vide.clickpicture

import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.io.IOException
import java.net.URI
import java.text.SimpleDateFormat
import java.util.*
import java.util.jar.Manifest

class MainActivity : AppCompatActivity() {

    private  val IMAGE_CAPTURE_CODE: Int=1234
    private val PERMISSION_CODE: Int=123
    var image_uri:Uri?=null
    lateinit var currentPhotoPath: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        button.setOnClickListener{
            if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M)
            {
                if(checkSelfPermission(android.Manifest.permission.CAMERA)==PackageManager.PERMISSION_DENIED ||
                    checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)==PackageManager.PERMISSION_DENIED)
                {
                    //permission not enable
                    val permission= arrayOf(android.Manifest.permission.CAMERA,android.Manifest.permission.WRITE_EXTERNAL_STORAGE)

                    requestPermissions(permission, PERMISSION_CODE)
                }
                else{
                    //permission already provided
                    openCamera()
                }
            }
            else{
                //system os is < marshmellow
            }

        }
    }

    private fun openCamera() {

        val values=ContentValues()
        values.put(MediaStore.Images.Media.DISPLAY_NAME,editText.text.toString())
        values.put(MediaStore.Images.Media.TITLE,editText.text.toString())
        values.put(MediaStore.Images.Media.DESCRIPTION,"From Cam")
        image_uri= contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,values)

        val cameraIntent =Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT,image_uri)
        startActivityForResult(cameraIntent,IMAGE_CAPTURE_CODE)

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
       // super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when(requestCode){
            PERMISSION_CODE ->{
                if(grantResults.size>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED)
                {
                    openCamera()
                }
                else{
                    Toast.makeText(this,"Permission Denied",Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode==Activity.RESULT_OK)
        {
           // val imageBitmap= data?.extras?.get("data") as Bitmap
            //set Image captured to the image view
            imageView.setImageURI(image_uri)
            //imageView.setImageBitmap(imageBitmap)
           // createImageFile()
        }
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            currentPhotoPath = absolutePath
        }
    }
}
