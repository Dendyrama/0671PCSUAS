package com.github.UAS0671.view

import android.annotation.SuppressLint
import android.content.ContentValues
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.github.UAS0671.R
import com.github.UAS0671.db.DatabaseUas.FavColumns.Companion.AVATAR
import com.github.UAS0671.db.DatabaseUas.FavColumns.Companion.CONTENT_URI
import com.github.UAS0671.db.DatabaseUas.FavColumns.Companion.FAVOURITE
import com.github.UAS0671.db.DatabaseUas.FavColumns.Companion.FOLLOWERS
import com.github.UAS0671.db.DatabaseUas.FavColumns.Companion.FOLLOWING
import com.github.UAS0671.db.DatabaseUas.FavColumns.Companion.NAME
import com.github.UAS0671.db.DatabaseUas.FavColumns.Companion.REPOSITORY
import com.github.UAS0671.db.DatabaseUas.FavColumns.Companion.USERNAME
import com.github.UAS0671.db.FavouriteHelper
import com.github.UAS0671.model.DataUsers
import com.github.UAS0671.model.Favourite
import com.github.UAS0671.viewModel.ViewPagerDetailAdapter
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.item_row_users.username

class DetailActivity : AppCompatActivity(), View.OnClickListener {

    companion object {
        const val EXTRA_DATA = "extra_data"
        const val EXTRA_FAV = "extra_data"
        const val EXTRA_NOTE = "extra_note"
        const val EXTRA_POSITION = "extra_position"
    }

    private var isFavourite = false
    private lateinit var gitHelper: FavouriteHelper
    private var favourites: Favourite? = null
    private lateinit var imageAvatar: String

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        gitHelper = FavouriteHelper.getInstance(applicationContext)
        gitHelper.open()

        // if the data is get from SQLite database will do the if statement and when the data from GITHUB API will go to else statement
        // also check is favourite or not
        favourites = intent.getParcelableExtra(EXTRA_NOTE)
        if (favourites != null) {
            setDataObject()
            isFavourite = true
            val checked: Int = R.drawable.ic_favorite_black_24dp
            btn_fav.setImageResource(checked)
        } else {
            setData()
        }

        viewPagerConfig()
        btn_fav.setOnClickListener(this)
    }

    // view pager settings
    private fun viewPagerConfig() {
        val viewPagerDetailAdapter = ViewPagerDetailAdapter(this, supportFragmentManager)
        view_pager.adapter = viewPagerDetailAdapter
        tabs.setupWithViewPager(view_pager)
        supportActionBar?.elevation = 0f
    }

    // to make custom title bar
    private fun setActionBarTitle(title: String) {
        if (supportActionBar != null) {
            supportActionBar!!.title = title
        }
    }

    // set the data to view from GITHUB API
    @SuppressLint("SetTextI18n")
    private fun setData() {
        val dataUser = intent.getParcelableExtra(EXTRA_DATA) as DataUsers
        setActionBarTitle("Detail of " + dataUser.name.toString())
        name.text = dataUser.name.toString()
        username.text = dataUser.username.toString()
        repo.text = dataUser.repository.toString()
        followerss.text = dataUser.followers.toString()
        followings.text = dataUser.following.toString()
        Glide.with(this)
            .load(dataUser.avatar.toString())
            .into(avatars)
        imageAvatar = dataUser.avatar.toString()
    }

    // set data to view form SQLite database
    @SuppressLint("SetTextI18n")
    private fun setDataObject() {
        val favUser = intent.getParcelableExtra(EXTRA_NOTE) as Favourite
        setActionBarTitle("Detail of " + favUser.name.toString())
        name.text = favUser.name.toString()
        username.text = favUser.username.toString()
        repo.text = favUser.repository.toString()
        followerss.text = favUser.followers.toString()
        followings.text = favUser.following.toString()
        Glide.with(this)
            .load(favUser.avatar.toString())
            .into(avatars)
        imageAvatar = favUser.avatar.toString()
    }

    // on click buttton will call this func, but this one just only have for favourite button
    override fun onClick(view: View) {
        val checked: Int = R.drawable.ic_favorite_black_24dp
        val unChecked: Int = R.drawable.ic_favorite_border_black_24dp
        if (view.id == R.id.btn_fav) {
            if (isFavourite) {
                gitHelper.deleteById(favourites?.username.toString())
                Toast.makeText(this, "Deleted from favourite list", Toast.LENGTH_SHORT).show()
                btn_fav.setImageResource(unChecked)
                isFavourite = false
            } else {
                val dataUsername = username.text.toString()
                val dataName = name.text.toString()
                val dataAvatar = imageAvatar
                val dataRepository = repo.text.toString()
                val dataFollowers = followerss.text.toString()
                val dataFollowing = followings.text.toString()
                val dataFavourite = "1"

                val values = ContentValues()
                values.put(USERNAME, dataUsername)
                values.put(NAME, dataName)
                values.put(AVATAR, dataAvatar)
                values.put(REPOSITORY, dataRepository)
                values.put(FOLLOWERS, dataFollowers)
                values.put(FOLLOWING, dataFollowing)
                values.put(FAVOURITE, dataFavourite)

                isFavourite = true
                contentResolver.insert(CONTENT_URI, values)
                Toast.makeText(this, "Added to favourite list", Toast.LENGTH_SHORT).show()
                btn_fav.setImageResource(checked)
            }
        }
    }

    // this func is running when the view of detail is not showing to delete the view and when open again will get the newest data
    override fun onDestroy() {
        super.onDestroy()
        gitHelper.close()
    }
}
