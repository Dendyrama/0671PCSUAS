package com.github.UAS0671.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.github.UAS0671.db.DatabaseUas.FavColumns.Companion.TABLE_NAME

internal class DatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "dbUas"
        private const val DATABASE_VERSION = 1
        private const val SQL_CREATE_TABLE_NOTE = "CREATE TABLE $TABLE_NAME" +
                " (${DatabaseUas.FavColumns.USERNAME} TEXT PRIMARY KEY  NOT NULL," +
                " ${DatabaseUas.FavColumns.NAME} TEXT NOT NULL," +
                " ${DatabaseUas.FavColumns.AVATAR} TEXT NOT NULL," +
                " ${DatabaseUas.FavColumns.COMPANY} TEXT NOT NULL," +
                " ${DatabaseUas.FavColumns.LOCATION} TEXT NOT NULL," +
                " ${DatabaseUas.FavColumns.REPOSITORY} INTEGER NOT NULL," +
                " ${DatabaseUas.FavColumns.FOLLOWERS} INTEGER NOT NULL," +
                " ${DatabaseUas.FavColumns.FOLLOWING} INTEGER NOT NULL," +
                " ${DatabaseUas.FavColumns.FAVOURITE} TEXT NOT NULL)"
    }

    // create the SQLite database table
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(SQL_CREATE_TABLE_NOTE)
    }

    // if the table of database is exist, this function will delete it
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }
}