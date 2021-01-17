package com.github.UAS0671.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.UAS0671.R
import com.github.UAS0671.model.DataUsers
import com.github.UAS0671.viewModel.ListDataUsersAdapter
import com.github.UAS0671.viewModel.userFilterList
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import cz.msebera.android.httpclient.Header
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject

class MainActivity : AppCompatActivity() {

    private var title: String = "Github User"
    private var listData: ArrayList<DataUsers> = ArrayList()
    private lateinit var adapter: ListDataUsersAdapter

    companion object {
        private val TAG = MainActivity::class.java.simpleName
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setActionBarTitle(title)

        adapter = ListDataUsersAdapter(listData)
        progressBar.visibility = View.INVISIBLE

        recyclerViewConfig()
        searchData()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.options_menu, menu)
        return true
    }

    // menu on click setting
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.favouriteMenu -> {
                val i = Intent(this, FavActivity::class.java)
                startActivity(i)
                true
            }
            R.id.settingMenu -> {
                val i = Intent(this, SettingActivity::class.java)
                startActivity(i)
                true
            }
            else -> true
        }
    }

    // searching data from GITHUB API when enter clicked
    private fun searchData() {
        user_search.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                if (query.isEmpty()) {
                    return true
                } else {
                    listData.clear()
                    getDataGitSearch(query)
                }
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                return false
            }
        })
    }

    // using it for customizing your recyclerView
    private fun recyclerViewConfig() {
        recycleView.layoutManager = LinearLayoutManager(recycleView.context)
        recycleView.setHasFixedSize(true)
        recycleView.addItemDecoration(
            DividerItemDecoration(
                recycleView.context,
                DividerItemDecoration.VERTICAL
            )
        )
    }

    // from the function getDataGit will run this func for get the detail of user and save it to DataUsers as parcel
    private fun getDataGitDetail(id: String) {
        progressBar.visibility = View.VISIBLE
        val client = AsyncHttpClient()
        client.addHeader("Authorization", "token 6fe9dff2e5e43d25eb3abe9ff508a750b972f725")
        client.addHeader("User-Agent", "request")
        val url = "https://api.github.com/users/$id"
        client.get(url, object : AsyncHttpResponseHandler() {
            override fun onSuccess(
                statusCode: Int,
                headers: Array<Header>,
                responseBody: ByteArray
            ) {
                progressBar.visibility = View.INVISIBLE
                val result = String(responseBody)
                Log.d(TAG, result)
                try {
                    val jsonObject = JSONObject(result)
                    val username: String? = jsonObject.getString("login").toString()
                    val name: String? = jsonObject.getString("name").toString()
                    val avatar: String? = jsonObject.getString("avatar_url").toString()
                    val company: String? = jsonObject.getString("company").toString()
                    val location: String? = jsonObject.getString("location").toString()
                    val repository: Int = jsonObject.getInt("public_repos")
                    val followers: Int = jsonObject.getInt("followers")
                    val following: Int = jsonObject.getInt("following")
                    val isFav = "0"
                    listData.add(
                        DataUsers(
                            username,
                            name,
                            avatar,
                            company,
                            location,
                            repository,
                            followers,
                            following,
                            isFav
                        )
                    )
                    showRecyclerList()
                } catch (e: Exception) {
                    Toast.makeText(this@MainActivity, e.message, Toast.LENGTH_SHORT)
                        .show()
                    e.printStackTrace()
                }
            }

            override fun onFailure(
                statusCode: Int,
                headers: Array<Header>,
                responseBody: ByteArray,
                error: Throwable
            ) {
                progressBar.visibility = View.INVISIBLE
                val errorMessage = when (statusCode) {
                    401 -> "$statusCode : Bad Request"
                    403 -> "$statusCode : Forbidden"
                    404 -> "$statusCode : Not Found"
                    else -> "$statusCode : ${error.message + " DETAIL"}"
                }
                Toast.makeText(this@MainActivity, errorMessage, Toast.LENGTH_LONG)
                    .show()
            }
        })
    }

    // this one get all data from GITHUB API and will be use for get the detail of user when the username like "what user search" on the function getDataGitDetail
    private fun getDataGitSearch(id: String) {
        progressBar.visibility = View.VISIBLE
        val client = AsyncHttpClient()
        client.addHeader("Authorization", "token 6fe9dff2e5e43d25eb3abe9ff508a750b972f725")
        client.addHeader("User-Agent", "request")
        val url = "https://api.github.com/search/users?q=$id"
        client.get(url, object : AsyncHttpResponseHandler() {
            override fun onSuccess(
                statusCode: Int,
                headers: Array<Header>,
                responseBody: ByteArray
            ) {
                progressBar.visibility = View.INVISIBLE
                val result = String(responseBody)
                Log.d(TAG, result)
                try {
                    val jsonArray = JSONObject(result)
                    val item = jsonArray.getJSONArray("items")
                    for (i in 0 until item.length()) {
                        val jsonObject = item.getJSONObject(i)
                        val username: String = jsonObject.getString("login")
                        getDataGitDetail(username)
                    }
                } catch (e: Exception) {
                    Toast.makeText(this@MainActivity, e.message, Toast.LENGTH_SHORT)
                        .show()
                    e.printStackTrace()
                }
            }

            override fun onFailure(
                statusCode: Int,
                headers: Array<Header>,
                responseBody: ByteArray,
                error: Throwable
            ) {
                progressBar.visibility = View.INVISIBLE
                val errorMessage = when (statusCode) {
                    401 -> "$statusCode : Bad Request"
                    403 -> "$statusCode : Forbidden"
                    404 -> "$statusCode : Not Found"
                    else -> "$statusCode : ${error.message + " GIT"}"
                }
                Toast.makeText(this@MainActivity, errorMessage, Toast.LENGTH_LONG)
                    .show()
            }
        })
    }

    private fun setActionBarTitle(title: String) {
        if (supportActionBar != null) {
            supportActionBar?.title = title
        }
    }

    private fun showRecyclerList() {
        recycleView.layoutManager = LinearLayoutManager(this)
        val listDataAdapter =
            ListDataUsersAdapter(userFilterList)
        recycleView.adapter = adapter

        listDataAdapter.setOnItemClickCallback(object : ListDataUsersAdapter.OnItemClickCallback {
            override fun onItemClicked(dataUsers: DataUsers) {

            }
        })
    }

    override fun onResume() {
        super.onResume()
        listData.clear()
        user_search.setQuery("", false)
    }
}