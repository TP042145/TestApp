package com.example.recipe

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

//database helper class contain CRUD methods
class RecipeDBHelper(context: Context?): SQLiteOpenHelper (
    context,
    Constants.DB_NAME,
    null,
    Constants.DB_VERSION
) {
    override fun onCreate(db: SQLiteDatabase) {
        //create table on that db
        db.execSQL(Constants.CREATE_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        //upgrade db (if any change)
        //drop older table if exits

        db.execSQL("DROP TABLE IF EXISTS" + Constants.TABLE_NAME)
        onCreate(db)
    }


    //insert function
    fun insertData(
        image:String?,
        name:String?,
        type:String?,
        ingredients:String?,
        steps:String?,
        addedTime:String?,
        updatedTime:String?
    ): Long{
        //get writable db
        val db = this.writableDatabase
        val values = ContentValues()

        //insert data
        values.put(Constants.R_IMAGE, image)
        values.put(Constants.R_NAME, name)
        values.put(Constants.R_TYPE, type)
        values.put(Constants.R_INGREDIENTS, ingredients)
        values.put(Constants.R_STEPS, steps)
        values.put(Constants.R_ADDED_TIMESTAMP, addedTime)
        values.put(Constants.R_UPDATED_TIMESTAMP, updatedTime)


        // insert row (include id)
        val id = db.insert(Constants.TABLE_NAME,null, values)

        //close db connection
        db.close()

        //return id
        return id
    }


    //get all data
    fun loadAllRecipe(orderBy:String):ArrayList<ModelRecipe> {
        val recordList = ArrayList<ModelRecipe>()
        val selectQuery = "SELECT * FROM ${Constants.TABLE_NAME} ORDER BY $orderBy"
        val db = this.writableDatabase
        val cursor = db.rawQuery(selectQuery, null)

        if (cursor.moveToFirst()) {
            do {
                val modelRecipe = ModelRecipe (
                    ""+cursor.getInt(cursor.getColumnIndex(Constants.R_ID)),
                    ""+cursor.getString(cursor.getColumnIndex(Constants.R_IMAGE)),
                    ""+cursor.getString(cursor.getColumnIndex(Constants.R_NAME)),
                    ""+cursor.getString(cursor.getColumnIndex(Constants.R_TYPE)),
                    ""+cursor.getString(cursor.getColumnIndex(Constants.R_INGREDIENTS)),
                    ""+cursor.getString(cursor.getColumnIndex(Constants.R_STEPS)),
                    ""+cursor.getString(cursor.getColumnIndex(Constants.R_ADDED_TIMESTAMP)),
                    ""+cursor.getString(cursor.getColumnIndex(Constants.R_UPDATED_TIMESTAMP))
                )
                recordList.add(modelRecipe)
            } while (cursor.moveToNext())
        }
        db.close()
        return recordList
    }


    //search data
    fun searchRecords(query:String, orderBy:String):ArrayList<ModelRecipe> {
        val recordList = ArrayList<ModelRecipe>()
        val selectQuery = "SELECT * FROM ${Constants.TABLE_NAME} WHERE ${Constants.R_TYPE} LIKE '%$query%' OR ${Constants.R_NAME} LIKE '%$query%' ORDER BY $orderBy"
        val db = this.writableDatabase
        val cursor = db.rawQuery(selectQuery, null)

        if (cursor.moveToFirst()) {
            do {
                val modelRecipe = ModelRecipe (
                    ""+cursor.getInt(cursor.getColumnIndex(Constants.R_ID)),
                    ""+cursor.getString(cursor.getColumnIndex(Constants.R_IMAGE)),
                    ""+cursor.getString(cursor.getColumnIndex(Constants.R_NAME)),
                    ""+cursor.getString(cursor.getColumnIndex(Constants.R_TYPE)),
                    ""+cursor.getString(cursor.getColumnIndex(Constants.R_INGREDIENTS)),
                    ""+cursor.getString(cursor.getColumnIndex(Constants.R_STEPS)),
                    ""+cursor.getString(cursor.getColumnIndex(Constants.R_ADDED_TIMESTAMP)),
                    ""+cursor.getString(cursor.getColumnIndex(Constants.R_UPDATED_TIMESTAMP))
                )
                recordList.add(modelRecipe)
            } while (cursor.moveToNext())
        }
        db.close()
        return recordList
    }


    //update data
    fun updateRecord (
        id:String,
        image:String?,
        name:String?,
        type:String?,
        ingredients:String?,
        steps:String?,
        updatedTime:String?
    ): Long
    {
        //get writable db
        val db = this.writableDatabase
        val values = ContentValues()

        //get data
        values.put(Constants.R_IMAGE, image)
        values.put(Constants.R_NAME, name)
        values.put(Constants.R_TYPE, type)
        values.put(Constants.R_INGREDIENTS, ingredients)
        values.put(Constants.R_STEPS, steps)
        values.put(Constants.R_UPDATED_TIMESTAMP, updatedTime)

        //update
        return db.update(Constants.TABLE_NAME,
            values,
            "${Constants.R_ID}=?",
            arrayOf(id)).toLong()
    }


    fun deleteRecipe(id:String) {
        val db = writableDatabase
        db.delete(
            Constants.TABLE_NAME,
            "${Constants.R_ID}=?",
            arrayOf(id)
        )
        db.close()
    }

    fun deleteAllRecipe() {
        val db = writableDatabase
        db.execSQL("DELETE FROM ${Constants.TABLE_NAME}")
        db.close()
    }

}