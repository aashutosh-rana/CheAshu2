package com.bcebhagalpur.cheashu.dashboard.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
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

class PgActivity3 : AppCompatActivity() {

    private lateinit var chipFoodTime: ChipGroup
    private lateinit var chipFoodType: ChipGroup
    private lateinit var chipFoodChargeType: ChipGroup
    private lateinit var etFoodCharge:TextInputLayout

    private lateinit var btnSubmit: Button
    private lateinit var btnSkip: Button
    private val db= FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pg3)

        val path=intent.getStringExtra("path")
        val rootPath=intent.getStringExtra("rootPath")
        val propertyType=intent.getStringExtra("propertyType")

        chipFoodTime=findViewById(R.id.chipFoodTime)
        chipFoodType=findViewById(R.id.chipFoodType)
        chipFoodChargeType=findViewById(R.id.chipFoodChargeType)
        etFoodCharge=findViewById(R.id.etFoodCharge)

        btnSubmit=findViewById(R.id.btnSubmit)
        btnSkip=findViewById(R.id.btnSkip)

        tags(chipFoodChargeType,path!!,rootPath!!,"messChargeType")
        tags(chipFoodTime, path, rootPath,"messTime")
        tags(chipFoodType, path, rootPath,"foodType")

         btnSubmit.setOnClickListener {
            if (!TextUtils.isEmpty(etFoodCharge.editText!!.text.toString())){
                db.document(path)
                    .update("messCharge", etFoodCharge.editText!!.text.toString())
                db.document(rootPath)
                    .update("messCharge", etFoodCharge.editText!!.text.toString())
                Toast.makeText(this,"Submitted successfully", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this,HomeActivity::class.java))
                finish()
            }else{
                Toast.makeText(this,"Please provide mess charge",Toast.LENGTH_SHORT).show()
            }

        }
        btnSkip.setOnClickListener {
            Toast.makeText(this,"You skipped", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this,HomeActivity::class.java))
            finish()
        }

    }

    private fun tags(chipGroup: ChipGroup, path: String, rootPath: String,field:String) {
        chipGroup.children.forEach {
            try {
                (it as Chip).setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked) {
                        db.document(path)
                            .update(field, FieldValue.arrayUnion(it.text.toString()))
                        db.document(rootPath)
                            .update(field, FieldValue.arrayUnion(it.text.toString()))
                    } else {
                        db.document(rootPath)
                            .update(field, FieldValue.arrayRemove(it.text.toString()))
                        db.document(path)
                            .update(field, FieldValue.arrayRemove(it.text.toString()))
                    }
                }
            } catch (e: TypeCastException) {
                e.printStackTrace()
            }
        }

    }
}