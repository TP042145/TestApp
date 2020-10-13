package com.example.recipe

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    //action bar
    private var actionBar: ActionBar? = null;

    //db helper
    lateinit var dbHelper: RecipeDBHelper

    //orderby query
    private val SORT_NAME = "${Constants.R_NAME} ASC"

    private var temptext: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //init action bar
        actionBar = supportActionBar
        //title of action bar
        actionBar!!.title = "Recipe List"


        //init db helper
        dbHelper = RecipeDBHelper(this)

        loadRecipe()

        btnAdd.setOnClickListener {
            startActivity(Intent(this, AddRecipeActivity::class.java))
        }
    }


    private fun loadRecipe() {
        val adapterRecipe = AdapterRecipe(this,dbHelper.loadAllRecipe(SORT_NAME))
        recipeRv.adapter = adapterRecipe
    }

    private fun searchRecords(query:String) {
        val adapterRecipe = AdapterRecipe(this,dbHelper.searchRecords(query, SORT_NAME))
        recipeRv.adapter = adapterRecipe
    }

    override fun onResume() {
        super.onResume()
        if (TextUtils.isEmpty(temptext)) {
            loadRecipe()
        }
        else {
            searchRecords(temptext)
        }
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        //inflate menu
        menuInflater.inflate(R.menu.menu_main, menu)

        //searchview
        val item = menu.findItem(R.id.action_search)
        val searchView = item.actionView as SearchView

        searchView.setOnQueryTextListener(object :
            SearchView.OnQueryTextListener{
            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText != null) {
                    temptext = newText
                    searchRecords(newText)
                }
                return true
            }

            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    temptext = query
                    searchRecords(query)
                }
                return true
            }
        })

        return super.onCreateOptionsMenu(menu)
    }

    // Toolbar options handler
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_deleteall -> {
                deleteVerification()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun deleteVerification() {
        var builder = AlertDialog.Builder(this)
        builder.setPositiveButton("Yes") { _, _ ->
            dbHelper.deleteAllRecipe()
            onResume()
            Toast.makeText(this, "Successfully deleted all recipe!", Toast.LENGTH_LONG).show()
        }
        builder.setNegativeButton("No") {_, _ ->}
        builder.setTitle("Delete all?")
        builder.setMessage("Are you sure you want to delete all user?")
        builder.create().show()
    }
}