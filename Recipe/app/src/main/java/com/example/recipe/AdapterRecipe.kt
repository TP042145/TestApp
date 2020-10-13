package com.example.recipe

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class AdapterRecipe() : RecyclerView.Adapter<AdapterRecipe.HolderRecord>() {

    private var context: Context?=null
    private var recordList:ArrayList<ModelRecipe>? = null

    constructor(context: Context?, recordList: ArrayList<ModelRecipe>?) : this() {
        this.context = context
        this.recordList = recordList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterRecipe.HolderRecord {
        return HolderRecord(
            LayoutInflater.from(context).inflate(R.layout.rv_layout, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return recordList!!.size
    }

    override fun onBindViewHolder(holder: AdapterRecipe.HolderRecord, position: Int) {
        val model = recordList!!.get(position)

        val id = model.id
        val name = model.name
        val type = model.type
        var image = model.image
        var ingredients = model.ingredients
        var steps = model.steps
        var addedTime = model.addedTime
        var updatedTime = model.updatedTime

        holder.nameRecipe.text = name
        holder.typeRecipe.text = type
        holder.rvImage.setImageURI(Uri.parse(image))

        holder.itemView.setOnClickListener {
            val intent = Intent(context, DetailRecipeActivity::class.java)
            intent.putExtra("RECIPE_ID", id)
            context!!.startActivity(intent)
        }
    }


    inner class HolderRecord(itemView: View): RecyclerView.ViewHolder(itemView) {

        //view from row_layout.xml
        var rvImage: ImageView = itemView.findViewById(R.id.rvImage)
        var nameRecipe: TextView = itemView.findViewById(R.id.nameRecipe)
        var typeRecipe: TextView = itemView.findViewById(R.id.typeRecipe)
    }
}