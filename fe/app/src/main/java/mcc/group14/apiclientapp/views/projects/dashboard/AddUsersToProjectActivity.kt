package mcc.group14.apiclientapp.views.projects.dashboard

import android.app.Activity
import android.app.SearchManager
import android.content.Context
import android.content.Intent
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
import android.widget.TextView
import android.widget.Toast
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import layout.SearchAdapter
import mcc.group14.apiclientapp.R
import mcc.group14.apiclientapp.api.ProjectApiClient
import mcc.group14.apiclientapp.api.UsersApiClient
import mcc.group14.apiclientapp.data.ProjectDetail
import mcc.group14.apiclientapp.data.UserProject
import mcc.group14.apiclientapp.data.UserSearch

class AddUsersToProjectActivity : AppCompatActivity() {

    val TAG = "AddUsersToProjectsActivity"

    // stuff for searching users
    private lateinit var recyclerView: RecyclerView
    private lateinit var layoutManager: RecyclerView.LayoutManager
    private lateinit var adapter: SearchAdapter

    private var users: MutableList<UserSearch>? = null
    private lateinit var project: UserProject

    private val MINIMUM_STRING_LENGTH = 5

    private val usersApiClient: UsersApiClient = UsersApiClient.create()
    private val projectApiClient: ProjectApiClient = ProjectApiClient.create()

    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_users_to_project)

        project = intent.getSerializableExtra("PROJECT")
                as UserProject


        initUI(project.name)
    }

    private fun initUI(projectName: String) {
        title = "Add a member to $projectName"
        recyclerView = findViewById(R.id.users_recycler)
        layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager
        recyclerView.setHasFixedSize(true)
        progressBar = findViewById(R.id.projects_listing_prog)

        progressBar.visibility = View.GONE
    }

    public fun fetchUsers(capturedText: String) {
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
                        Toast.LENGTH_SHORT
                    ).show()
                }
            )
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        var inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.search_menu, menu)

        var searchManager: SearchManager = getSystemService(Context.SEARCH_SERVICE)
                as SearchManager
        var searchView: SearchView = menu?.findItem(
            R.id.search_user_menu_item
        )?.actionView as SearchView

        searchView.setSearchableInfo(
            searchManager.getSearchableInfo(componentName)
        )
        searchView.setIconifiedByDefault(false)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

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

    public fun onUserSearchRecyclerItemClick(view: View) {
        val tappedTV = view as TextView

        val tappedUserIndex = users.orEmpty().indexOfFirst { it.display_name == tappedTV.text }

        val tappedUserEmail = users.orEmpty()[tappedUserIndex].email_id

        project.team_members += ",${tappedUserEmail}"

        val returnIntent = Intent()

        projectApiClient.modifyProject(project)
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread()).map {
                if (it.success == "true") it.payload else throw Throwable(it.error)
            }
            .subscribe(
                { result ->
                    setResult(Activity.RESULT_OK, returnIntent)
                    Log.d(TAG, "ResponseWrapper body $result")
                    finish()
                },
                { error ->
                    Log.e(TAG, error.message)
                    finish()
                })
    }
}