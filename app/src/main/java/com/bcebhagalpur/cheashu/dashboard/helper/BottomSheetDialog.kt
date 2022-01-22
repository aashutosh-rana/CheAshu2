package com.bcebhagalpur.cheashu.dashboard.helper

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.Nullable
import com.bcebhagalpur.cheashu.R
import com.bcebhagalpur.cheashu.dashboard.activity.*

class BottomSheetDialog : RoundedBottomSheetDialogFragment() {
    override fun onCreateView(inflater: LayoutInflater, @Nullable container: ViewGroup?, @Nullable savedInstanceState: Bundle?): View

    {
        val v: View = inflater.inflate(R.layout.bottomsheet_upload_room,container, false)
        val apartment: TextView = v.findViewById(R.id.apartment)
        val pg:        TextView = v.findViewById(R.id.pg)
        val hostel:    TextView = v.findViewById(R.id.hostel)
        val villa:     TextView = v.findViewById(R.id.villa)
        val house:     TextView = v.findViewById(R.id.houses)
        val marriageHall: TextView = v.findViewById(R.id.marriageHall)
        val farmHouse: TextView = v.findViewById(R.id.farmHouse)
        val commercialSpace:TextView=v.findViewById(R.id.commercialSpace)
        val simpleFlat:TextView=v.findViewById(R.id.simpleFlat)
        val doubleRoom:TextView=v.findViewById(R.id.doubleRoom)
        val singleRoom:TextView=v.findViewById(R.id.singleRoom)

         apartment.setOnClickListener {
            apartment.setBackgroundResource(R.drawable.selected_property_type_background)
            apartment.setTextColor(resources.getColor(R.color.white))
            val intent=Intent(context,OwnerActivity::class.java)
            intent.putExtra("propertyType","Apartment")
            startActivity(intent)
        }

        doubleRoom.setOnClickListener {
            doubleRoom.setBackgroundResource(R.drawable.selected_property_type_background)
            doubleRoom.setTextColor(resources.getColor(R.color.white))
            val intent=Intent(context,OwnerActivity::class.java)
            intent.putExtra("propertyType","Double Room")
            startActivity(intent)
        }

        singleRoom.setOnClickListener {
            singleRoom.setBackgroundResource(R.drawable.selected_property_type_background)
            singleRoom.setTextColor(resources.getColor(R.color.white))
            val intent=Intent(context,OwnerActivity::class.java)
            intent.putExtra("propertyType","Single Room")
            startActivity(intent)
        }

        simpleFlat.setOnClickListener {
            simpleFlat.setBackgroundResource(R.drawable.selected_property_type_background)
            simpleFlat.setTextColor(resources.getColor(R.color.white))
            val intent=Intent(context,OwnerActivity::class.java)
            intent.putExtra("propertyType","MarriageHall")
            startActivity(intent)
        }

        marriageHall.setOnClickListener {
            marriageHall.setBackgroundResource(R.drawable.selected_property_type_background)
            marriageHall.setTextColor(resources.getColor(R.color.white))
            val intent=Intent(context,MarriageHallActivity::class.java)
            intent.putExtra("propertyType","MarriageHall")
            startActivity(intent)
        }

       commercialSpace.setOnClickListener {
            commercialSpace.setBackgroundResource(R.drawable.selected_property_type_background)
            commercialSpace.setTextColor(resources.getColor(R.color.white))
            val intent=Intent(context,WareHouseActivity::class.java)
            intent.putExtra("propertyType","Commercial Space")
            startActivity(intent)
        }

        pg.setOnClickListener {
            pg.setBackgroundResource(R.drawable.selected_property_type_background)
            pg.setTextColor(resources.getColor(R.color.white))
            val intent=Intent(context,PgActivity::class.java)
            intent.putExtra("propertyType","Pg")
            startActivity(intent)
        }

        hostel.setOnClickListener {
            pg.setBackgroundResource(R.drawable.selected_property_type_background)
            pg.setTextColor(resources.getColor(R.color.white))
            val intent=Intent(context,PgActivity::class.java)
            intent.putExtra("propertyType","Hostel")
            startActivity(intent)
        }

        villa.setOnClickListener {
            villa.setBackgroundResource(R.drawable.selected_property_type_background)
            villa.setTextColor(resources.getColor(R.color.white))
            val intent=Intent(context,VillaActivity::class.java)
            intent.putExtra("propertyType","Villa")
            startActivity(intent)
        }

       house.setOnClickListener {
            house.setBackgroundResource(R.drawable.selected_property_type_background)
            house.setTextColor(resources.getColor(R.color.white))
            val intent=Intent(context,VillaActivity::class.java)
            intent.putExtra("propertyType","House")
            startActivity(intent)
        }

        return v

    }
}
