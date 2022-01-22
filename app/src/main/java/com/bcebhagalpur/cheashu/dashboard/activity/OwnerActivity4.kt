@file:Suppress("DEPRECATION")
package com.bcebhagalpur.cheashu.dashboard.activity

import android.Manifest
import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.*
import android.media.ExifInterface
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.StrictMode
import android.os.StrictMode.VmPolicy
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.annotation.Nullable
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.amazonaws.auth.CognitoCachingCredentialsProvider
import com.bcebhagalpur.cheashu.R
import com.bcebhagalpur.cheashu.dashboard.aws.AWSKeys
import com.bcebhagalpur.cheashu.dashboard.aws.S3Uploader
import com.bcebhagalpur.cheashu.dashboard.aws.S3Utils.generates3ShareUrl
import com.bcebhagalpur.cheashu.dashboard.helper.ImageAdapter1
import com.bcebhagalpur.cheashu.dashboard.helper.ImageModel
import com.bcebhagalpur.cheashu.dashboard.helper.SelectedImageAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.*
import java.lang.ArithmeticException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

@Suppress("DEPRECATION")
class OwnerActivity4 : Activity() {

    private var s3uploaderObj: S3Uploader? = null
    private var urlFromS3: String? = null
    private var progressDialog: ProgressDialog? = null
    private val TAG =OwnerActivity4::class.java.canonicalName
    private var imageList: ArrayList<ImageModel>? = null
    private var selectedImageList: ArrayList<String>? = null
    private var imageRecyclerView: RecyclerView? = null
    private var selectedImageRecyclerView: RecyclerView? = null
    private var resImg = intArrayOf(R.drawable.ic_camera_white_30dp, R.drawable.ic_folder_white_30dp)
    private var title = arrayOf("Camera", "Folder")
    private var mCurrentPhotoPath: String? = null
    private var selectedImageAdapter: SelectedImageAdapter? = null
    private var imageAdapter: ImageAdapter1? = null
    private var projection = arrayOf(MediaStore.MediaColumns.DATA)
    var image: File? = null
    private var done: Button? = null
    private lateinit var mStorage : StorageReference

    val imageUrlList= arrayListOf<String>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_owner4)

        s3uploaderObj = S3Uploader(this)
        progressDialog = ProgressDialog(this)


         val permissions = arrayOf(
             Manifest.permission.READ_EXTERNAL_STORAGE,
             Manifest.permission.WRITE_EXTERNAL_STORAGE,
             Manifest.permission.CAMERA
         )

          if (hasPermissions(this, permissions)) {
            init()
            allImages
            setImageList()
            setSelectedImageList()
        }else{
            ActivityCompat.requestPermissions(this, permissions, PERMISSION_ALL)
        }
    }

    private fun init() {

        imageRecyclerView = findViewById(R.id.recycler_view)
        selectedImageRecyclerView = findViewById(R.id.selected_recycler_view)
        done = findViewById(R.id.done)
        selectedImageList = ArrayList()
        imageList = ArrayList()
        mStorage = FirebaseStorage.getInstance().reference.child("Uploads")

        done!!.setOnClickListener {
           val credentialsProvider = CognitoCachingCredentialsProvider(
               application.applicationContext,
               AWSKeys.COGNITO_POOL_ID,
               AWSKeys.MY_REGION
           )

             uploadToS3()
        }
    }


    private fun uploadToS3(){

        val path=intent.getStringExtra("path")
        val rootPath=intent.getStringExtra("rootPath")
        val propertyType=intent.getStringExtra("propertyType")
        val currentUser= FirebaseAuth.getInstance().currentUser!!.uid
        val db = FirebaseFirestore.getInstance()
        Toast.makeText(applicationContext, path, Toast.LENGTH_LONG)
                .show()

        for (i in 0 until selectedImageList!!.size) {

            val uri=Uri.parse( selectedImageList!![i])

           val cmpimg= compressImage(uri.toString())
            db.document(path!!)
                    .update("images", FieldValue.arrayUnion(
                            "https://cheashu-room-photos.s3.ap-south-1.amazonaws.com/$path/$i"))
            db.document(rootPath!!)
                    .update("images", FieldValue.arrayUnion(
                            "https://cheashu-room-photos.s3.ap-south-1.amazonaws.com/$path/$i"))

            val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
            StrictMode.setThreadPolicy(policy)

            s3uploaderObj!!.initUpload(cmpimg, "$path/$i")

            s3uploaderObj!!.setOns3UploadDone(object : S3Uploader.S3UploadInterface {
                override fun onUploadSuccess(response: String?) {
                    if (response.equals("Success", ignoreCase = true)) {
                        urlFromS3 = generates3ShareUrl(
                            applicationContext,
                            selectedImageList!![i]
                        )
                    }
                }
                override fun onUploadError(response: String?) {
                    Log.e(TAG, "Error Uploading")
                    Toast.makeText(applicationContext, "Error", Toast.LENGTH_LONG).show()
                }
            })
        }
        if (propertyType=="MarriageHall"){
            val intent=Intent(this,MarriageHallActivity3::class.java)
            intent.putExtra("path",path)
            intent.putExtra("rootPath",rootPath)
            startActivity(intent)
        }else if(propertyType=="Commercial Space"||propertyType=="Villa" || propertyType=="House"){
            val intent=Intent(this,HomeActivity::class.java)
            intent.putExtra("path",path)
            intent.putExtra("rootPath",rootPath)
            startActivity(intent)
            finish()
        } else if(propertyType=="Pg"){

            val intent=Intent(this,PgActivity3::class.java)
            intent.putExtra("path",path)
            intent.putExtra("rootPath",rootPath)
            startActivity(intent)

        }else{
            val intent=Intent(this,OwnerActivity3::class.java)
            intent.putExtra("path",path)
            intent.putExtra("rootPath",rootPath)
            startActivity(intent)
        }
    }

     @Throws(IOException::class)
    fun storeOnCache(context: Context, bitmap: Bitmap): File {
        val cacheDir = context.cacheDir
        val file = File(cacheDir, generateRandomFilename("jpg"))
        val out = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 10, out)
        out.flush()
        out.close()
        return file
    }

    private fun generateRandomFilename(@Nullable extension: String?): String {
        return StringBuilder(50)
                .append(System.currentTimeMillis())
                .append((Math.random() * 10000.0).toInt())
                .append(".")
                .append(extension)
                .toString()
    }

    private fun setImageList() {
        imageRecyclerView!!.layoutManager = GridLayoutManager(applicationContext, 4)
        imageAdapter = ImageAdapter1(applicationContext, imageList)
        imageRecyclerView!!.adapter = imageAdapter
        imageAdapter!!.setOnItemClickListener { position, _ ->
            when (position) {
                0 -> {
                    takePicture()
                }
                1 -> {
                    pickImageIntent
                }
                else -> {
                    try {
                        if (!imageList!![position].isSelected) {
                            selectImage(position)
                        } else {
                            try {
                                unSelectImage(position)
                            } catch (e: java.lang.IndexOutOfBoundsException) {
                                e.printStackTrace()
                            }

                        }
                    } catch (ed: ArrayIndexOutOfBoundsException) {
                        ed.printStackTrace()
                    }
                }
            }
        }
        setImagePickerList()
    }

    private fun setSelectedImageList() {
        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        selectedImageRecyclerView!!.layoutManager = layoutManager
        selectedImageAdapter = SelectedImageAdapter(this, selectedImageList!!)
        selectedImageRecyclerView!!.adapter = selectedImageAdapter
    }

    // Add Camera and Folder in ArrayList
    private fun setImagePickerList() {
        for (i in resImg.indices) {
            val imageModel = ImageModel()
            imageModel.setResImg(resImg[i])
            imageModel.setTitle(title[i])
            imageList!!.add(i, imageModel)
        }
        imageAdapter!!.notifyDataSetChanged()
    }

    // get all images from external storage
    private val allImages: Unit
        get() {
           val orderBy = MediaStore.Images.Media._ID
            imageList!!.clear()
            val cursor: Cursor? = contentResolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection,
                null,
                null,
                null
            )
            while (cursor!!.moveToNext()) {
                val absolutePathOfImage: String ="file://"+
                        cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.DATA))
                val imageModel = ImageModel()
                imageModel.setImage(absolutePathOfImage)
                imageList!!.add(imageModel)
            }
            cursor.close()
        }

    // start the image capture Intent

    private fun createImageFile(): File? {
        // Create an image file name
        val dateTime = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName = "JPEG_" + dateTime + "_"
        val storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        try {
            image = File.createTempFile(imageFileName, ".jpg", storageDir)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = "file://" + image!!.absolutePath
        return image
    }

    private fun takePicture() {
        val builder = VmPolicy.Builder()
        StrictMode.setVmPolicy(builder.build())
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        // Continue only if the File was successfully created;
            val photoFile: File? = createImageFile()
            if (photoFile != null) {
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile))
                startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE)
            }
    }

    private val pickImageIntent: Unit
        get() {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            intent.flags = Intent.FLAG_ACTIVITY_NO_ANIMATION
            startActivityForResult(intent, PICK_IMAGES)
        }

    // Add image in SelectedArrayList
   private fun selectImage(position: Int) {
        // Check before add new item in ArrayList;
        try {
            if (!selectedImageList!!.contains(imageList!![position].getImage())) {
                try {
                    imageList!![position].setSelected(true)
                    selectedImageList!!.add(0, imageList!![position].getImage()!!)
                    selectedImageAdapter!!.notifyDataSetChanged()
                    imageAdapter!!.notifyDataSetChanged()
                }catch (e: java.lang.IndexOutOfBoundsException){
                    e.printStackTrace()
                }

            }
        }catch (e: IndexOutOfBoundsException){
            e.printStackTrace()
        }

    }

    // Remove image from selectedImageList
   private fun unSelectImage(position: Int) {
        for (i in 0 until selectedImageList!!.size) {
            try {
                if (imageList!![position].getImage() != null) {
                    try {
                        if (selectedImageList!![i] == imageList!![position].getImage()) {
                            try {
                                imageList!![position].setSelected(false)
                                selectedImageList!!.removeAt(i)
                                selectedImageAdapter!!.notifyDataSetChanged()
                                imageAdapter!!.notifyDataSetChanged()
                            }catch (e: java.lang.IndexOutOfBoundsException){
                                e.printStackTrace()
                            }

                        }
                    }catch (e: IndexOutOfBoundsException){
                        e.printStackTrace()
                    }

                }
            }catch (e: IndexOutOfBoundsException){
                e.printStackTrace()
            }

        }
    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode != RESULT_CANCELED) {
            if (requestCode == REQUEST_IMAGE_CAPTURE) {
                if (mCurrentPhotoPath != null) {
                    addImage(mCurrentPhotoPath)
                }
            } else if (requestCode == PICK_IMAGES) {
                if (data!!.clipData != null) {
                    val mClipData = data.clipData
                    for (i in 0 until mClipData!!.itemCount) {
                        val item = mClipData.getItemAt(i)
                        val uri = item.uri
                        getImageFilePath(uri)
                    }
                } else if (data.data != null) {
                    val uri = data.data
                    getImageFilePath(uri!!)
                }
            }
        }else{
            Toast.makeText(this, "canclled!!!!!!!!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun addImage(filePath: String?) {
        val imageModel = ImageModel()
        imageModel.setImage(filePath)
        imageModel.setSelected(true)
        imageList!!.add(2, imageModel)
        selectedImageList!!.add(0, filePath!!)
        selectedImageAdapter!!.notifyDataSetChanged()
        imageAdapter!!.notifyDataSetChanged()
    }

    // Get image file path
   private fun getImageFilePath(uri: Uri) {
        val cursor = contentResolver.query(uri, projection, null, null, null)
        if (cursor != null) {
            while (cursor.moveToNext()) {
                val absolutePathOfImage = cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.DATA))
                if (absolutePathOfImage != null) {
                    checkImage(absolutePathOfImage)
                } else {
                    try {
                        checkImage(uri.toString())
                    }catch (e: java.lang.IndexOutOfBoundsException){
                        e.printStackTrace()
                    }

                }
            }
        }
    }

   private fun checkImage(filePath: String?) {
        // Check before adding a new image to ArrayList to avoid duplicate images
        if (!selectedImageList!!.contains(filePath)) {
            for (pos in 0 until imageList!!.size) {
                try {
                    if (imageList!![pos].getImage() != null) {
                        if (imageList!![pos].getImage().equals(filePath, ignoreCase = true)) {
                            imageList!!.removeAt(pos)
                        }
                    }
                }catch (e: IndexOutOfBoundsException){
                    e.printStackTrace()
                }

            }
            addImage(filePath)
        }
    }

    // add image in selectedImageList and imageList

    val isStoragePermissionGranted: Boolean
        get() {
            val ACCESS_EXTERNAL_STORAGE =
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            if (ACCESS_EXTERNAL_STORAGE != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    STORAGE_PERMISSION
                )
                return false
            }
            return true
        }

    val isCameraPermissionGranted: Boolean
        get() {
            val ACCESS_CAMERA =
                    ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            if (ACCESS_CAMERA != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.CAMERA),
                    CAMERA_PERMISSION
                )
                return false
            }
            return true
        }
    val isReadExternalStorage: Boolean
        get() {
            val ACCESS_READ_STORAGE =
                    ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    )
            if (ACCESS_READ_STORAGE != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    READ_EXTERNAL_STORAGE1
                )
                return false
            }
            return true
        }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode== PERMISSION_ALL && grantResults[0]==PackageManager.PERMISSION_GRANTED && grantResults[1]==
                PackageManager.PERMISSION_GRANTED) {
            init()
            allImages
            setImageList()
            setSelectedImageList()
        }else{
            Toast.makeText(this, "Permission Required", Toast.LENGTH_SHORT).show()
            finish()
            overridePendingTransition(0, 0)
        }
    }

//    fun hasPermissions(context: Context, vararg permissions: String): Boolean = permissions.all {
//        ActivityCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
//    }


    private fun hasPermissions(context: Context?, vararg permissions: Array<String>): Boolean {
        if (context != null) {
            for (permission in permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission.toString()) != PackageManager.PERMISSION_GRANTED) {
                    return false
                }
            }
        }
        return true
    }


    companion object {
        const val REQUEST_IMAGE_CAPTURE = 150
        const val PICK_IMAGES = 2
        const val STORAGE_PERMISSION = 100
        const val READ_EXTERNAL_STORAGE1 = 200
        const val CAMERA_PERMISSION = 300
        const val PERMISSION_ALL = 500
    }

    override fun onBackPressed() {
        finish()
        overridePendingTransition(0, 0)
    }

    fun compressImage(imageUri: String): String? {
        val filePath = getRealPathFromURI(imageUri)
        var scaledBitmap: Bitmap? = null
        val options = BitmapFactory.Options()

//      by setting this field as true, the actual bitmap pixels are not loaded in the memory. Just the bounds are loaded. If
//      you try the use the bitmap here, you will get null.
        options.inJustDecodeBounds = true
        var bmp = BitmapFactory.decodeFile(filePath, options)
        var actualHeight = options.outHeight
        var actualWidth = options.outWidth

//      max Height and width values of the compressed image is taken as 816x612
        try {
            val maxHeight = 816.0f
            val maxWidth = 612.0f
            var imgRatio = (actualWidth / actualHeight).toFloat()
            val maxRatio = maxWidth / maxHeight

//      width and height values are set maintaining the aspect ratio of the image
        if (actualHeight > maxHeight || actualWidth > maxWidth) {
            if (imgRatio < maxRatio) {
                imgRatio = maxHeight / actualHeight
                actualWidth = (imgRatio * actualWidth).toInt()
                actualHeight = maxHeight.toInt()
            } else if (imgRatio > maxRatio) {
                imgRatio = maxWidth / actualWidth
                actualHeight = (imgRatio * actualHeight).toInt()
                actualWidth = maxWidth.toInt()
            } else {
                actualHeight = maxHeight.toInt()
                actualWidth = maxWidth.toInt()
            }
        }
        }catch (e:ArithmeticException){
            e.printStackTrace()
        }
//      setting inSampleSize value allows to load a scaled down version of the original image
        options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight)

//      inJustDecodeBounds set to false to load the actual bitmap
        options.inJustDecodeBounds = false

//      this options allow android to claim the bitmap memory if it runs low on memory
        options.inPurgeable = true
        options.inInputShareable = true
        options.inTempStorage = ByteArray(16 * 1024)
        try {
//          load the bitmap from its path
            bmp = BitmapFactory.decodeFile(filePath, options)
        } catch (exception: OutOfMemoryError) {
            exception.printStackTrace()
        }
        try {
            scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.ARGB_8888)
        } catch (exception: OutOfMemoryError) {
            exception.printStackTrace()
        }
        val ratioX = actualWidth / options.outWidth.toFloat()
        val ratioY = actualHeight / options.outHeight.toFloat()
        val middleX = actualWidth / 2.0f
        val middleY = actualHeight / 2.0f
        val scaleMatrix = Matrix()
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY)
        val canvas = Canvas(scaledBitmap!!)
        canvas.setMatrix(scaleMatrix)
        canvas.drawBitmap(
            bmp,
            middleX - bmp.width / 2,
            middleY - bmp.height / 2,
            Paint(Paint.FILTER_BITMAP_FLAG)
        )

//      check the rotation of the image and display it properly
        val exif: ExifInterface
        try {
            exif = ExifInterface(filePath!!)
            val orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 0)
            Log.d("EXIF", "Exif: $orientation")
            val matrix = Matrix()
            if (orientation == 6) {
                matrix.postRotate(90F)
                Log.d("EXIF", "Exif: $orientation")
            } else if (orientation == 3) {
                matrix.postRotate(180F)
                Log.d("EXIF", "Exif: $orientation")
            } else if (orientation == 8) {
                matrix.postRotate(270F)
                Log.d("EXIF", "Exif: $orientation")
            }
            scaledBitmap = Bitmap.createBitmap(
                scaledBitmap!!, 0, 0,
                scaledBitmap.width, scaledBitmap.height, matrix,
                true
            )
        } catch (e: IOException) {
            e.printStackTrace()
        }
        var out: FileOutputStream? = null
        val filename = getFilename()
        try {
            out = FileOutputStream(filename)
//          write the compressed bitmap at the destination specified by filename.
            scaledBitmap!!.compress(Bitmap.CompressFormat.JPEG, 80, out)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }
        return filename
    }

    private fun getFilename(): String {
        val file = File(Environment.getExternalStorageDirectory().path, "MyFolder/Images")
        if (!file.exists()) {
            file.mkdirs()
        }
        return file.absolutePath.toString() + "/" + System.currentTimeMillis() + ".jpg"
    }

    private fun getRealPathFromURI(contentURI: String): String? {
            val contentUri = Uri.parse(contentURI)
            val cursor = contentResolver.query(contentUri, null, null, null, null)

            return if (cursor == null) {
                contentUri.path
            } else {
                cursor.moveToFirst()
                val index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
                cursor.getString(index)
            }
    }

    private fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
        val height = options.outHeight
        val width = options.outWidth
        var inSampleSize = 1
        if (height > reqHeight || width > reqWidth) {
            val heightRatio = Math.round(height.toFloat() / reqHeight.toFloat())
            val widthRatio = Math.round(width.toFloat() / reqWidth.toFloat())
            inSampleSize = if (heightRatio < widthRatio) heightRatio else widthRatio
        }
        val totalPixels = (width * height).toFloat()
        val totalReqPixelsCap = (reqWidth * reqHeight * 2).toFloat()
        while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
            inSampleSize++
        }
        return inSampleSize
    }

}

// for (i in 0 until selectedImageList!!.size) {
//    val i1=i+1
//    val progress = ProgressDialog(this).apply {
//        setTitle("Uploading Picture $i1")
//        setCancelable(false)
//        setCanceledOnTouchOutside(false)
//        show()
//    }
//    val bmp = MediaStore.Images.Media.getBitmap(
//        contentResolver,
//        selectedImageList!![i].toUri()
//    )
//    val baos = ByteArrayOutputStream()
//    bmp.compress(Bitmap.CompressFormat.JPEG, 10, baos)
//    val uri: ByteArray = baos.toByteArray()
////                val uri1=Uri.parse("file://" + selectedImageList!![i])
////                val uri=Uri.parse(selectedImageList!![i])
//
//    val por = PutObjectRequest("room-photos",i.toString(),
//        File(uri.toString())
//    )
//    s3Client.putObject(por)
//
//    val mReference = mStorage.child(FirebaseAuth.getInstance().currentUser!!.uid).child(
//        i.toString()
//    )
//    val uploadTask = mReference.putBytes(uri)
//
//
//    uploadTask.addOnProgressListener { taskSnapshot ->
//        val value: Double =
//            (100.0 * taskSnapshot.bytesTransferred) / taskSnapshot.totalByteCount
//        Log.v("value", "value==$value")
//        progress.setMessage("Uploaded.. " + value.toInt() + "%")
//    }
//    try {
//        uploadTask.continueWith {
//            if (!it.isSuccessful) {
//                it.exception?.let { t -> throw t }
//            }
//            mReference.downloadUrl
//        }.addOnFailureListener {
//
//            Toast.makeText(
//                this@OwnerActivity4,
//                it.message,
//                Toast.LENGTH_LONG
//            ).show()
//            progress.hide()
//        }.addOnCompleteListener {
//            if (it.isSuccessful) {
//                progress.dismiss()
//                it.result!!.addOnSuccessListener { task ->
//                    val myUri = task.toString()
//                    Toast.makeText(
//                        this@OwnerActivity4,
//                        "Image " + i1.toString() + "uploaded successfully",
//                        Toast.LENGTH_LONG
//                    )
//                        .show()
//
//                }
//            }
//
//        }
//
//    } catch (e: java.lang.Exception) {
//        Toast.makeText(
//            this@OwnerActivity4,
//            "please upload images",
//            Toast.LENGTH_SHORT
//        ).show()
//    }
//    Toast.makeText(applicationContext, selectedImageList!![i], Toast.LENGTH_LONG)
//        .show()
//}