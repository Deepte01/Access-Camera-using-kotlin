package com.vide.clickpicture

import android.app.Activity
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.provider.MediaStore
import android.content.Intent
import android.content.pm.PackageManager
//import android.graphics.Bitmap
import android.os.Build
import android.os.Environment
import androidx.core.content.FileProvider
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*





class snapImage : AppCompatActivity() {

    lateinit var currentPhotoPath: String
    private val REQUEST_CAPTURE_IMAGE = 100
    var photoURI:Uri?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        button.setOnClickListener{
            if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.M)
            {
                if(checkSelfPermission(android.Manifest.permission.CAMERA)== PackageManager.PERMISSION_DENIED ||
                    checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)== PackageManager.PERMISSION_DENIED)
                {
                    //permission not enable
                    val permission= arrayOf(android.Manifest.permission.CAMERA,android.Manifest.permission.WRITE_EXTERNAL_STORAGE)

                    requestPermissions(permission, REQUEST_CAPTURE_IMAGE)
                }
                else{
                    //permission already provided
                    openCameraIntent()
                }
            }
            else{
                //system os is < marshmellow
            }

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


    private fun openCameraIntent(){
        val pictureIntent=Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if(pictureIntent.resolveActivity(packageManager)!=null)
        {
            var photoFile: File?=null
            try{
                photoFile = createImageFile();
            }
            catch (e:IOException ) {
                // Error occurred while creating the File
             //   ...
            }
            if(photoFile!=null)
            {
                 photoURI= FileProvider.getUriForFile(this,"com.vide.clickpicture.provider",photoFile)
                pictureIntent.putExtra(
                    MediaStore.EXTRA_OUTPUT,photoURI)
                startActivityForResult(pictureIntent,
                    REQUEST_CAPTURE_IMAGE)

            }
        }


    }

    override fun onActivityResult(
        requestCode: Int, resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CAPTURE_IMAGE) {
            //don't compare the data to null, it will always come as  null because we are providing a file URI, so load with the imageFilePath we obtained before opening the cameraIntent

            if (resultCode == Activity.RESULT_OK) {
                imageView.setImageURI(photoURI)
            }
            else if(resultCode == Activity.RESULT_CANCELED) {
            }

        }
    }

}