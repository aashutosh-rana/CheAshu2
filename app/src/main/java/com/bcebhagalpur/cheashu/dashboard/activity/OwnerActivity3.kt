@file:Suppress("DEPRECATION")

package com.bcebhagalpur.cheashu.dashboard.activity

import android.Manifest
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.Button
import android.widget.Toast
import android.widget.VideoView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bcebhagalpur.cheashu.R
import com.iceteck.silicompressorr.SiliCompressor
import java.io.File
import java.lang.Boolean
import java.net.URISyntaxException

class OwnerActivity3 : AppCompatActivity() {

    private lateinit var btnSelectVideo:Button
    private lateinit var videoView1:VideoView
    private lateinit var videoView2:VideoView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_owner3)

        btnSelectVideo=findViewById(R.id.btnSelectVideo)
        videoView1=findViewById(R.id.videoView1)
        videoView2=findViewById(R.id.videoView2)


        btnSelectVideo.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                ==PackageManager.PERMISSION_GRANTED){
                selectVideo()
            }else{
                ActivityCompat.requestPermissions(
                        this,
                        arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 101
                )
            }
        }
    }

    private fun selectVideo() {
        val intent=Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI)
//        intent.type = "video/*"
        val intent1=Intent(MediaStore.ACTION_VIDEO_CAPTURE)
        intent1.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 30)
//        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent1, 200)
    }

    override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<out String>,
            grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode==101)
        {
            if (grantResults.isNotEmpty() && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                selectVideo()
            }else{
                Toast.makeText(this, "Permission denied!", Toast.LENGTH_SHORT).show()
            }
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode==200){
            if (resultCode== RESULT_OK && data!=null){
                val uri=data.data
                videoView1.setVideoURI(uri)
                videoView1.start()


                val filePath=File(Environment.getExternalStorageDirectory().absolutePath)
                CompressVideo(this, videoView2).execute("false", uri.toString(), filePath.path)
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    internal class CompressVideo(private val context: Context, private val videoView2: VideoView) : AsyncTask<String, String, String>() {

        var dialog=Dialog(context)


        override fun onPreExecute() {
           super.onPreExecute()

           dialog=ProgressDialog.show(context, "", "Compressing...........")
       }

      override fun doInBackground(vararg paths: String?): String? {
          var filePath: String? = null
          try {

              //This bellow is just a temporary solution to test that method call works
              val b = Boolean.parseBoolean(paths[0])
              filePath = if (b) {
                  SiliCompressor.with(context).compressVideo(
                          paths[1], paths[2]
                  )
              } else {
                  val videoContentUri = Uri.parse(paths[1])
                  // Example using the bitrate and video size parameters
//                  filePath = SiliCompressor.with(mContext).compressVideo(
//                                videoContentUri,
//                                paths[2],
//                                1280,
//                                720,
//                                1500000)
                  SiliCompressor.with(context).compressVideo(
                          videoContentUri,
                          paths[2]
                  )
              }
          } catch (e: URISyntaxException) {
              e.printStackTrace()
          } catch (e: Exception) {
              e.printStackTrace()
          }
          return filePath
      }

        override fun onPostExecute(result: String?) {
            val videoFile=File(result!!)
            val compressUri=Uri.fromFile(videoFile)
            videoView2.setVideoURI(compressUri)
            videoView2.start()
        }
   }

}