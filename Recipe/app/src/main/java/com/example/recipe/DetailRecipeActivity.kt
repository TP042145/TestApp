package com.example.recipe

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import kotlinx.android.synthetic.main.activity_detail_recipe.*
import java.util.*

class DetailRecipeActivity : AppCompatActivity() {

    //action bar
    private var actionBar: ActionBar?=null

    //dv helper
    private var dbHelper:RecipeDBHelper?=null

    private var recordID:String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_recipe)

        //init action bar
        actionBar = supportActionBar
        //title of action bar
        actionBar!!.title = "Recipe Detail"
        actionBar!!.setDisplayHomeAsUpEnabled(true)
        actionBar!!.setDisplayShowHomeEnabled(true)

        //init db helper class
        dbHelper = RecipeDBHelper(this)

        //get record id from intent
        val intent = intent
        recordID = intent.getStringExtra("RECIPE_ID")

        showRecipeDetail()
    }


    private fun showRecipeDetail() {
        val selectQuery =
            "SELECT * FROM ${Constants.TABLE_NAME} WHERE ${Constants.R_ID} =\"$recordID\""

        val db = dbHelper!!.writableDatabase
        var cursor = db.rawQuery(selectQuery, null)

        if (cursor.moveToFirst()) {
            do {

                val id = ""+cursor.getInt(cursor.getColumnIndex(Constants.R_ID))
                val image = ""+cursor.getString(cursor.getColumnIndex(Constants.R_IMAGE))
                val name = ""+cursor.getString(cursor.getColumnIndex(Constants.R_NAME))
                val type = ""+cursor.getString(cursor.getColumnIndex(Constants.R_TYPE))
                val ingredients = ""+cursor.getString(cursor.getColumnIndex(Constants.R_INGREDIENTS))
                val steps = ""+cursor.getString(cursor.getColumnIndex(Constants.R_STEPS))
                val addedTimeStamp = ""+cursor.getString(cursor.getColumnIndex(Constants.R_ADDED_TIMESTAMP))
                val updatedTimeStamp = ""+cursor.getString(cursor.getColumnIndex(Constants.R_UPDATED_TIMESTAMP))

                // convert timestamp
                val calendar1 = Calendar.getInstance(Locale.getDefault())
                calendar1.timeInMillis = addedTimeStamp.toLong()
                val timeAdded = android.text.format.DateFormat.format("dd/MM/yyyy hh:mm:aa", calendar1)

                val calendar2 = Calendar.getInstance(Locale.getDefault())
                calendar2.timeInMillis = updatedTimeStamp.toLong()
                val timeUpdated = android.text.format.DateFormat.format("dd/MM/yyyy hh:mm:aa", calendar2)


                //set data
                txtRecipeName.text = name
                txtrecipeType.text = type
                txtrecipeIngredients.text = ingredients
                txtrecipeSteps.text = steps
                txtrecipeAddedTime.text = "Added Time: $timeAdded"
                txtrecipeUpdateTime.text = "Last Update: $timeUpdated"

                if (image == "null") {
                    imageRecipe.setImageResource(R.drawable.ic_food_black)
                }
                else {
                    imageRecipe.setImageURI(Uri.parse(image))
                }

            } while (cursor.moveToNext())
        }

        // close db collection
        db.close()
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        //inflate menu
        menuInflater.inflate(R.menu.menu_options, menu)
        return super.onCreateOptionsMenu(menu)
    }

    // Toolbar options handler
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_update -> {
                editRecipe()
                true
            }
            R.id.action_delete -> {
                deleteVerification()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }


    // pass data
    private fun editRecipe() {
        val id = recordID.toString().trim()
        val intent = Intent(this, EditRecipeActivity::class.java)
        intent.putExtra("RECIPE_ID", id)
        startActivity(intent)
        finish()
    }


    private fun deleteVerification() {
        var builder = AlertDialog.Builder(this)
        builder.setPositiveButton("Yes") { _, _ ->
            deleteRecipe()
        }
        builder.setNegativeButton("No") {_, _ ->}
        builder.setTitle("Delete selected recipe?")
        builder.setMessage("Are you sure you want to delete selected recipe?")
        builder.create().show()
    }


    private fun deleteRecipe() {
        //get id
        val id = recordID.toString().trim()
        dbHelper!!.deleteRecipe(id)
        Toast.makeText(this, "Successfully deleted the selected recipe!", Toast.LENGTH_LONG).show()
        onBackPressed()
    }


    // Back button
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }
}