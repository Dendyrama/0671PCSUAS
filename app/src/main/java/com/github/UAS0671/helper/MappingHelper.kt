package com.github.UAS0671.helper

import android.database.Cursor
import com.github.UAS0671.db.DatabaseUas
import com.github.UAS0671.model.Favourite
import java.util.*

object MappingHelper {

    fun mapCursorToArrayList(notesCursor: Cursor?): ArrayList<Favourite> {
        val favList = ArrayList<Favourite>()

        notesCursor?.apply {
            while (moveToNext()) {
                val username = getString(getColumnIndexOrThrow(DatabaseUas.FavColumns.USERNAME))
                val name = getString(getColumnIndexOrThrow(DatabaseUas.FavColumns.NAME))
                val avatar = getString(getColumnIndexOrThrow(DatabaseUas.FavColumns.AVATAR))
                val company = getString(getColumnIndexOrThrow(DatabaseUas.FavColumns.COMPANY))
                val location = getString(getColumnIndexOrThrow(DatabaseUas.FavColumns.LOCATION))
                val repository = getInt(getColumnIndexOrThrow(DatabaseUas.FavColumns.REPOSITORY))
                val followers = getInt(getColumnIndexOrThrow(DatabaseUas.FavColumns.FOLLOWERS))
                val following = getInt(getColumnIndexOrThrow(DatabaseUas.FavColumns.FOLLOWING))
                val favourite =
                    getString(getColumnIndexOrThrow(DatabaseUas.FavColumns.FAVOURITE))
                favList.add(
                    Favourite(
                        username,
                        name,
                        avatar,
                        company,
                        location,
                        repository,
                        followers,
                        following,
                        favourite
                    )
                )
            }
        }
        return favList
    }
}