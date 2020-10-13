package com.example.recipe

import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import kotlinx.android.synthetic.main.activity_edit_recipe.*
import java.util.*

class EditRecipeActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    //action bar
    private var actionBar: ActionBar?=null

    //permission constants
    private val CAMERA_REQUEST_CODE = 100
    private val STORAGE_REQUEST_CODE = 101

    //image pick constant
    private val IMAGE_PICK_CAMERA_CODE = 102
    private val IMAGE_PICK_GALLERY_CODE = 103

    //arrays of permissions
    private lateinit var cameraPermissions:Array<String> //camera and storage
    private lateinit var storagePermissions:Array<String> // only storage

    //variables that will contain data to save in database
    private var imageUri: Uri? = null
    private var recipename: String = ""
    private var recipetype: String = ""
    private var recipeingredients: String = ""
    private var recipesteps: String = ""
    private var tempimage: String = ""

    lateinit var dbHelper:RecipeDBHelper


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_recipe)

        //init action bar
        actionBar = supportActionBar
        //title of action bar
        actionBar!!.title = "Edit Recipe"
        actionBar!!.setDisplayHomeAsUpEnabled(true)
        actionBar!!.setDisplayShowHomeEnabled(true)

        //init db helper class
        dbHelper = RecipeDBHelper(this)

        //init permissions arrays
        cameraPermissions = arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        storagePermissions = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)

        val spinner: Spinner = findViewById(R.id.spinnerRecipeType)
        spinner.onItemSelectedListener = this

        showRecipeDetail()

        //click imageview to pick image
        recipeImage.setOnClickListener {
            imagePickDialog();
        }
        //click add button to add the recipe
        btnUpdateRecipe.setOnClickListener {
            updateData();
        }

    }

    private fun showRecipeDetail() {
        val recordID=intent.getStringExtra("RECIPE_ID")
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
                val timeUpdated = android.text.format.DateFormat.format("dd//MM/yyyy hh:mm:aa", calendar2)


                //Create an ArrayAdapter using the string array and a default spinner layout
                val spinner: Spinner = findViewById(R.id.spinnerRecipeType)
                val dataAdapter =
                    ArrayAdapter.createFromResource(
                        this,
                        R.array.recipe_types_arrays,
                        android.R.layout.simple_spinner_item
                    ).also { adapter ->
                        // Specify the layout to use when the list of choices appears
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        // Apply the adapter to the spinner
                        spinner.adapter = adapter
                    }


                //set data
                recipeName.setText(name)
                recipeIngredients.setText(ingredients)
                recipeSteps.setText(steps)
                tempimage = image

                //set spinner
                val spinnerPosition: Int = dataAdapter.getPosition(type)
                spinnerRecipeType.setSelection(spinnerPosition)

                if (image == "null") {
                    recipeImage.setImageResource(R.drawable.ic_food_black)
                }
                else {
                    recipeImage.setImageURI(Uri.parse(image))
                }


            } while (cursor.moveToNext())
        }

        // close db collection
        db.close()
    }


    private fun updateData() {
        //get data
        val recordID=intent.getStringExtra("RECIPE_ID")
        recipename = "" + recipeName.text.toString().trim()
        recipeingredients = "" + recipeIngredients.text.toString().trim()
        recipesteps = "" + recipeSteps.text.toString().trim()
        recipetype = spinnerRecipeType.selectedItem.toString();

        val timestamp = System.currentTimeMillis()

        if (imageUri == null) {
            if(inputCheck(recipename, recipetype, recipeingredients, recipesteps)) {
                val id = dbHelper.updateRecord(
                    ""+recordID,
                    ""+tempimage,
                    ""+recipename,
                    ""+recipetype,
                    ""+recipeingredients,
                    ""+recipesteps,
                    ""+timestamp
                )
                Toast.makeText(this,"Recipe has been updated", Toast.LENGTH_SHORT).show()
                onBackPressed()
            }
            else {
                Toast.makeText(this, "Please fill in all info", Toast.LENGTH_LONG).show()
            }
        }
        else {
            if(inputCheck(recipename, recipetype, recipeingredients, recipesteps)) {
                val id = dbHelper.updateRecord(
                    ""+recordID,
                    ""+imageUri,
                    ""+recipename,
                    ""+recipetype,
                    ""+recipeingredients,
                    ""+recipesteps,
                    ""+timestamp
                )
                Toast.makeText(this,"Recipe has been updated", Toast.LENGTH_SHORT).show()
                onBackPressed()
            }
            else {
                Toast.makeText(this, "Please fill in all info", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun inputCheck (recipeName: String, recipeType: String, recipeIngredients: String, recipeSteps: String): Boolean{
        return !(TextUtils.isEmpty(recipeName) || TextUtils.isEmpty(recipeIngredients) || TextUtils.isEmpty(recipeSteps) || recipeType.equals("Select Recipe Type"))
    }

    private fun imagePickDialog() {
        //options to display in dialog
        val options = arrayOf("Camera", "Gallery")

        //dialog
        var builder = AlertDialog.Builder(this)

        //title
        builder.setTitle("Pick Image From")

        //set items/options
        builder.setItems(options) {dialog, which ->
            if (which == 0) {
                //camera clicked
                if (!checkCameraPermissions()) {
                    //permission not granted
                    requestCameraPermission()
                }
                else {
                    //permission granted
                    pickFromCamera()
                }
            }
            else {
                //gallery clicked
                if(!checkStoragePermission()) {
                    requestStoragePermission()
                }
                else {
                    pickFromGallery()
                }
            }
        }
        //show dialog
        builder.show()
    }


    private fun pickFromGallery() {
        val galleryIntent = Intent(Intent.ACTION_PICK)
        galleryIntent.type = "image/*" // only image to be picked
        startActivityForResult(
            galleryIntent,
            IMAGE_PICK_GALLERY_CODE
        )
    }


    private fun requestStoragePermission() {
        //request the storage permission
        ActivityCompat.requestPermissions(this, storagePermissions, STORAGE_REQUEST_CODE)
    }


    private fun checkStoragePermission(): Boolean {
        //check if storage permission is enabled or not
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
    }


    private fun pickFromCamera() {
        val values = ContentValues()
        values.put(MediaStore.Images.Media.TITLE, "Image Title")
        values.put(MediaStore.Images.Media.DESCRIPTION, "Image Description")

        //put image uri
        imageUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)

        //open camera
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
        startActivityForResult(
            cameraIntent,
            IMAGE_PICK_CAMERA_CODE
        )
    }


    private fun requestCameraPermission() {
        // request the camera permissions
        ActivityCompat.requestPermissions(this, cameraPermissions, CAMERA_REQUEST_CODE)
    }


    private fun checkCameraPermissions(): Boolean {
        //check if camera permission (camera and storage) are enabled or not
        val results = ContextCompat.checkSelfPermission(this,
            Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        val results1 = ContextCompat.checkSelfPermission(this,
            Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED

        return results && results1
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode) {
            CAMERA_REQUEST_CODE-> {
                if (grantResults.isNotEmpty()) {
                    //if allowed returns true otherwise false
                    val cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED
                    val storageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED
                    if (cameraAccepted && storageAccepted) {
                        pickFromCamera()
                    }
                    else {
                        Toast.makeText(this,"Camera and Storage permissions are required", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            STORAGE_REQUEST_CODE-> {
                if (grantResults.isNotEmpty()) {
                    val storageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED
                    if (storageAccepted) {
                        pickFromGallery()
                    }
                    else {
                        Toast.makeText(this,"Storage permissions is required", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        //image picked from camera or gallery will be received here
        if(resultCode == Activity.RESULT_OK) {
            //image is picked
            if (requestCode == IMAGE_PICK_GALLERY_CODE) {
                //picked from gallery
                //crop image
                CropImage.activity(data!!.data)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1,1)
                    .start(this)
            }
            else if (requestCode == IMAGE_PICK_CAMERA_CODE) {
                //picked from camera
                //crop image
                CropImage.activity(imageUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1,1)
                    .start(this)
            }
            else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                //cropped image received
                var result = CropImage.getActivityResult(data)
                if (resultCode == Activity.RESULT_OK) {
                    val resultUri = result.uri
                    imageUri = resultUri

                    //set image
                    recipeImage.setImageURI(resultUri)
                }
                else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    //error
                    val error = result.error
                    Toast.makeText(this, ""+error , Toast.LENGTH_SHORT).show()
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }


    override fun onNothingSelected(parent: AdapterView<*>?) {
        // do nothing
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        // do nothing
    }

    // Back button
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }
}