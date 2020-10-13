package com.example.recipe

object Constants {
    //db name and version
    const val DB_NAME = "RECIPES_DB"
    const val DB_VERSION = 1

    // table name
    const val TABLE_NAME = "RECIPES_TABLE"

    //columns/fields of table
    const val R_ID = "ID"
    const val R_IMAGE = "IMAGE"
    const val R_NAME = "NAME"
    const val R_TYPE = "TYPE"
    const val R_INGREDIENTS = "INGREDIENTS"
    const val R_STEPS = "STEPS"
    const val R_ADDED_TIMESTAMP = "ADDED_TIMESTAMP"
    const val R_UPDATED_TIMESTAMP = "UPDATED_TIMESTAMP"

    // create table query
    const val CREATE_TABLE = (
            "CREATE TABLE " + TABLE_NAME + "("
                    + R_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + R_IMAGE + " TEXT,"
                    + R_NAME + " TEXT,"
                    + R_TYPE + " TEXT,"
                    + R_INGREDIENTS + " TEXT,"
                    + R_STEPS + " TEXT,"
                    + R_ADDED_TIMESTAMP + " TEXT,"
                    + R_UPDATED_TIMESTAMP + " TEXT"
                    + ")"
            )
}