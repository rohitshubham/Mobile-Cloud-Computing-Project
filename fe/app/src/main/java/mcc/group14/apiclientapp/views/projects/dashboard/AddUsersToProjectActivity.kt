package mcc.group14.apiclientapp.views.projects.dashboard

import android.app.SearchManager
import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.SearchView
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import layout.SearchAdapter
import mcc.group14.apiclientapp.R
import mcc.group14.apiclientapp.api.UsersApiClient
import mcc.group14.apiclientapp.data.UserSearch

class AddUsersToProjectActivity : AppCompatActivity() {

    val TAG = "AddUsersToProjectsActivity"

    // stuff for searching users
    private lateinit var recyclerView: RecyclerView
    private lateinit var layoutManager: RecyclerView.LayoutManager
    private var users: MutableList<UserSearch>? = null
    private lateinit var adapter: SearchAdapter

    private val MINIMUM_STRING_LENGTH = 5

    private val usersApiClient: UsersApiClient = UsersApiClient.create()

    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_users_to_project)

        initUI()

        //fetchUsers("a")
    }

    private fun initUI() {
        recyclerView = findViewById(R.id.users_recycler)
        layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager
        recyclerView.setHasFixedSize(true)
        progressBar = findViewById(R.id.projects_listing_prog)

        progressBar.visibility = View.GONE
    }

    public fun fetchUsers(capturedText: String){
        progressBar.visibility = ProgressBar.VISIBLE

        var disposable = usersApiClient.searchForUsers(capturedText)
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread()).map {
                if (it.success == "true") it.payload else throw Throwable("APIError")
            }
            .subscribe(
                { result ->
                    progressBar.visibility = View.GONE
                    users = result
                    adapter = SearchAdapter(users, this)
                    recyclerView.adapter = adapter
                    adapter.notifyDataSetChanged()

                    Log.d(TAG, "User list received $result")

                },
                { error ->
                    progressBar.visibility = View.GONE
                    Log.e(TAG, error.message)
                    Toast.makeText(
                        applicationContext, "Network error",
                        Toast.LENGTH_SHORT).show()
                }
            )
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        var inflater : MenuInflater = menuInflater
        inflater.inflate(R.menu.search_menu, menu)

        var searchManager: SearchManager = getSystemService(Context.SEARCH_SERVICE)
                as SearchManager
        var searchView : SearchView = menu?.findItem(
            R.id.search_user_menu_item)?.actionView as SearchView

        searchView.setSearchableInfo(
            searchManager.getSearchableInfo(componentName)
        )
        searchView.setIconifiedByDefault(false)
        searchView.setOnQueryTextListener( object : SearchView.OnQueryTextListener {

            override fun onQueryTextSubmit(query: String?): Boolean {
                if (validQuery(query, MINIMUM_STRING_LENGTH)) {
                    fetchUsers(query!!)
                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (validQuery(newText, MINIMUM_STRING_LENGTH)) {
                    fetchUsers(newText!!)
                }
                return false
            }

        })
        return true
    }

    private fun validQuery(newText: String?, minStringLength: Int) =
        newText != null && newText != "" && newText.length >= minStringLength
}
