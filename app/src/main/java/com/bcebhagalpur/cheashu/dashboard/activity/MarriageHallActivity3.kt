package com.bcebhagalpur.cheashu.dashboard.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.view.children
import com.bcebhagalpur.cheashu.R
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class MarriageHallActivity3 : AppCompatActivity() {

    private lateinit var chipAmenities:ChipGroup
    private lateinit var isRoomAvailable:CheckBox
    private lateinit var linearRoomInfo:LinearLayout
    private lateinit var etAcRoom:TextInputLayout
    private lateinit var etNonAcRoom:TextInputLayout
    private lateinit var etDescription:TextInputLayout
    private lateinit var btnSubmit:Button
    private lateinit var btnSkip:Button
    private val db= FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_marriage_hall3)

        val path=intent.getStringExtra("path")
        val rootPath=intent.getStringExtra("rootPath")
        val propertyType=intent.getStringExtra("propertyType")

        chipAmenities=findViewById(R.id.chipAmenities)
        isRoomAvailable=findViewById(R.id.isRoomAvailable)
        linearRoomInfo=findViewById(R.id.linearRoomInfo)
        etAcRoom=findViewById(R.id.etAcRoom)
        etNonAcRoom=findViewById(R.id.etNonAcRoom)
        etDescription=findViewById(R.id.etDescription)
        btnSubmit=findViewById(R.id.btnSubmit)
        btnSkip=findViewById(R.id.btnSkip)
        isRoomAvailable.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked){
                linearRoomInfo.visibility=View.VISIBLE
            }else{
                linearRoomInfo.visibility=View.GONE
            }
        }

        tags(chipAmenities,path!!,rootPath!!)

        btnSubmit.setOnClickListener {
            db.document(path)
                .update("acRoom", etAcRoom.editText!!.text.toString())
            db.document(rootPath)
                .update("acRoom", etAcRoom.editText!!.text.toString())
            db.document(path)
                .update("nonAcRoom", etNonAcRoom.editText!!.text.toString())
            db.document(rootPath)
                .update("nonAcRoom", etNonAcRoom.editText!!.text.toString())
            db.document(path)
                .update("description", etDescription.editText!!.text.toString())
            db.document(rootPath)
                .update("description", etDescription.editText!!.text.toString())
            Toast.makeText(this,"Submitted successfully",Toast.LENGTH_SHORT).show()
            startActivity(Intent(this,HomeActivity::class.java))
            finish()
        }
        btnSkip.setOnClickListener {
            Toast.makeText(this,"You skipped",Toast.LENGTH_SHORT).show()
            startActivity(Intent(this,HomeActivity::class.java))
            finish()
        }

    }

    private fun tags(chipGroup: ChipGroup, path: String, rootPath: String) {
        chipGroup.children.forEach {
            try {
                (it as Chip).setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked) {
                        db.document(path)
                            .update("tags", FieldValue.arrayUnion(it.text.toString()))
                        db.document(rootPath)
                            .update("tags", FieldValue.arrayUnion(it.text.toString()))
                    } else {
                        db.document(rootPath)
                            .update("tags", FieldValue.arrayRemove(it.text.toString()))
                        db.document(path)
                            .update("tags", FieldValue.arrayRemove(it.text.toString()))
                    }
                }
            } catch (e: TypeCastException) {
                e.printStackTrace()
            }
        }

    }
}