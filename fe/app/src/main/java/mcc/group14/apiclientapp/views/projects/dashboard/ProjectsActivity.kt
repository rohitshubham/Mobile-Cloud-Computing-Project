package mcc.group14.apiclientapp.views.projects.dashboard

import android.app.Activity
import android.app.ListActivity
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.PopupMenu
import android.util.Log
import android.view.View
import android.widget.ListView
import android.widget.ProgressBar
import android.widget.Toast
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import mcc.group14.apiclientapp.R
import mcc.group14.apiclientapp.api.ProjectApiClient
import mcc.group14.apiclientapp.data.UserProject
import mcc.group14.apiclientapp.views.projects.ProjectDetailActivity
import mcc.group14.apiclientapp.views.projects.create.NewProjectActivity
import mcc.group14.apiclientapp.views.users.UserSettingsActivity


// Projects activity
class ProjectsActivity : ListActivity(){

    val TAG = "ProjectsActivity"

    val NEW_PROJECT_ACTIVITY = 0
    val USER_SETTINGS_ACTIVITY = 1
  //  val ADD_USERS_ACTIVITY = 2

    private lateinit var userEmail: String
    private lateinit var userAuth : String

    private lateinit var progressBar: ProgressBar

    val projectApi = ProjectApiClient.create()
    //private var projectList: MutableList<ProjectDetail>? = null
    private var projectList: MutableList<UserProject>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.projects)

        this.title = "Your projects"

        // get the info from login/sign-up
        userEmail = intent.getStringExtra("USER_EMAIL")
        userAuth = intent.getStringExtra("USER_AUTH")

        initUI()

        // downloading the list of projects
        refreshProjectList()
    }

    private fun initUI() {
        progressBar = findViewById(R.id.projects_listing_prog)
    }

    private fun refreshProjectList() {
        var disposable = this.projectApi.getProjectsList(userEmail)
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread()).map {
                if (it.success == "true") it.payload else throw Throwable("APIError")
            }
            .subscribe(
                { result ->
                    progressBar.visibility = View.GONE
                    Log.d(
                        TAG, "Project list received " +
                                "$result"
                    )
                    setListView(result)
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

    //private fun setListView(result: MutableList<ProjectDetail>?) {
    private fun setListView(result: MutableList<UserProject>?) {
        this.projectList = result
        val proj_names = mutableListOf<String>()
        for (proj in projectList.orEmpty()) {
            //proj_names.add(proj.name)
            proj_names.add(proj.project_name)
        }
        Log.d(TAG, proj_names.toString())

        // TODO: integrate image support

        // inserting project names in listView
        val adapter = ProjectListAdapter(
            this,
            ArrayList(projectList.orEmpty())
        )
        listView.adapter = adapter

    }

    // we use this method to pass to the ProjectDetail
    override fun onListItemClick(l: ListView?, v: View?, position: Int, id: Long) {
        val intent =
            Intent(this, ProjectDetailActivity::class.java)
        // pass project_id and prject_name to PrjectDetailActivity
        intent.putExtra("PROJECT_ID",
            projectList.orEmpty()[position].project_id)
        intent.putExtra("PROJECT_NAME",
            projectList.orEmpty()[position].project_name)
        startActivity(intent)
    }

    // use it for returning from other activities,
    // NB: refresh only if needed
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == NEW_PROJECT_ACTIVITY){
            if (resultCode == Activity.RESULT_OK){
                this.refreshProjectList()

                Toast.makeText(applicationContext,
                    "Project created successfully.",
                    Toast.LENGTH_LONG).show()
            }else {
                Toast.makeText(applicationContext,
                    "Error: project not created.",
                    Toast.LENGTH_LONG).show()
            }
        } else if (requestCode == USER_SETTINGS_ACTIVITY){
            if (resultCode == Activity.RESULT_OK){
                Toast.makeText(applicationContext, "User successfully edited",
                    Toast.LENGTH_LONG).show()
            } else{
                Toast.makeText(applicationContext, "Error: user not edited",
                    Toast.LENGTH_LONG).show()
            }
        }
    }

    public fun startNewProjectActivity(v: View) {
        val intent =
            Intent(this, NewProjectActivity::class.java).apply {
                putExtra("USER_EMAIL", userEmail)
                putExtra("USER_AUTH", userAuth)
            }
        startActivityForResult(intent, NEW_PROJECT_ACTIVITY)
    }

    public fun onUserSettingsClick(v: View) {
        val intent =
            Intent(this, UserSettingsActivity::class.java).apply {
                putExtra("USER_EMAIL", userEmail)
                putExtra("USER_AUTH", userAuth)
            }
        startActivityForResult(intent, USER_SETTINGS_ACTIVITY)
    }

    // it manages click on the overflow menu (three dots on each project_list_row)
    fun showPopUp(view: View) {
        val popupMenu = PopupMenu(this, view)
        val inflater = popupMenu.menuInflater
        inflater.inflate(R.menu.overflow_menu, popupMenu.menu)
        popupMenu.show()

        popupMenu.setOnMenuItemClickListener {
            when(it.itemId) {
                R.id.add_user_overflow_item -> {
                    val intent: Intent =
                        Intent(this,
                            AddUsersToProjectActivity::class.java)

                    startActivity(intent)
                    false
                }
            }
            true
        }
    }
}
