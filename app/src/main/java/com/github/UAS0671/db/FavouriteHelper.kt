package com.github.UAS0671.db

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.SQLException
import android.database.sqlite.SQLiteDatabase
import com.github.UAS0671.db.DatabaseUas.FavColumns.Companion.TABLE_NAME
import com.github.UAS0671.db.DatabaseUas.FavColumns.Companion.USERNAME

class FavouriteHelper(context: Context) {

    private var dataBaseHelper: DatabaseHelper = DatabaseHelper(context)
    private var database: SQLiteDatabase = dataBaseHelper.writableDatabase

    companion object {
        private const val DATABASE_TABLE = TABLE_NAME
        private var INSTANCE: FavouriteHelper? = null
        fun getInstance(context: Context): FavouriteHelper = INSTANCE ?: synchronized(this) {
            INSTANCE ?: FavouriteHelper(context)
        }
    }

    // get access to write database
    @Throws(SQLException::class)
    fun open() {
        database = dataBaseHelper.writableDatabase
    }

    // close the database connection
    fun close() {
        dataBaseHelper.close()
        if (database.isOpen)
            database.close()
    }

    //get all data in database
    fun queryAll(): Cursor {
        return database.query(
            DATABASE_TABLE,
            null,
            null,
            null,
            null,
            null,
            "$USERNAME DESC"
        )
    }

    // get data by id
    fun queryById(id: String): Cursor {
        return database.query(
            DATABASE_TABLE,
            null,
            "$USERNAME = ?",
            arrayOf(id),
            null,
            null,
            null,
            null
        )
    }

    // like the name is for insert data to database
    fun insert(values: ContentValues?): Long {
        return database.insert(DATABASE_TABLE, null, values)
    }

    // this for update data
    fun update(id: String, values: ContentValues?): Int {
        return database.update(DATABASE_TABLE, values, "$USERNAME = ?", arrayOf(id))
    }

    // and this for delete
    fun deleteById(id: String): Int {
        return database.delete(DATABASE_TABLE, "$USERNAME = '$id'", null)
    }

}