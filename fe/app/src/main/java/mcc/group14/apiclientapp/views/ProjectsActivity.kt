package mcc.group14.apiclientapp.views

import android.app.Activity
import android.app.ListActivity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ListView
import android.widget.Toast
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import mcc.group14.apiclientapp.R
import mcc.group14.apiclientapp.api.ProjectApiClient
import mcc.group14.apiclientapp.data.UserProject


// Projects activity
class ProjectsActivity : ListActivity() {

    val TAG = "ProjectActivity"

    val NEW_PROJECT_ACTIVITY = 0
    val USER_SETTINGS_ACTIVITY = 1


    private lateinit var userSettBtn: Button
    private lateinit var addProjectBtn: Button

    private lateinit var userEmail: String
    private lateinit var userAuth : String

    private lateinit var projectsLW: ListView

    val projectApi = ProjectApiClient.create()
    private var projectList: MutableList<UserProject>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.projects)

        this.title = "Your projects"

        // get the info from login/sign-up
        userEmail = intent.getStringExtra("USER_EMAIL")
        userAuth = intent.getStringExtra("USER_AUTH")

        initUI()

        setListeners()
        // downloading the list of projects
        refreshProjectList()
    }

    private fun refreshProjectList() {
        var disposable = this.projectApi.getProjectsList(userEmail)
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread()).map {
                if (it.success == "true") it.payload else throw Throwable("APIError")
            }
            .subscribe(
                { result ->
                    Log.d(
                        TAG, "Project list received " +
                                "$result"
                    )
                    setListView(result)
                },
                { error ->
                    Log.e(TAG, error.message)
                    Toast.makeText(
                        applicationContext, "Network error",
                        Toast.LENGTH_SHORT
                    ).show()

                }
            )
    }

    private fun setListView(result: MutableList<UserProject>?) {
        this.projectList = result
        val proj_names = mutableListOf<String>()
        for (proj in projectList.orEmpty()) {
            proj_names.add(proj.project_name)
        }
        Log.d(TAG, proj_names.toString())

        // TODO: @Sasha beautify the listView
        // info here:
        // https://www.vogella.com/tutorials/AndroidListView/article.html

        // inserting project names in listView
        val adapter = ProjectListAdapter(this,
            ArrayList(projectList.orEmpty()))
        listView.adapter = adapter

    }


    /*TODO, @Max today:
    * 1. finish projects ->
    *     1.2 AddUserToProject;
    *     1.3 AddTaskToProject.
    * 2. finish users ->
    *     2.1 UserSettingsActivity.
    *
    * */

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

    private fun initUI() {
        addProjectBtn = findViewById<Button>(R.id.new_project_btn)
        userSettBtn = findViewById<Button>(R.id.user_settings_btn)
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

    private fun setListeners() {
        addProjectBtn.setOnClickListener {
            val intent =
                Intent(this, NewProjectActivity::class.java).apply {
                    putExtra("USER_EMAIL", userEmail)
                    putExtra("USER_AUTH", userAuth)
                }
            startActivityForResult(intent, NEW_PROJECT_ACTIVITY)
        }

        userSettBtn.setOnClickListener {
            val intent =
                Intent(this, UserSettingsActivity::class.java).apply {
                    putExtra("USER_EMAIL", userEmail)
                    putExtra("USER_AUTH", userAuth)
                }
            startActivityForResult(intent, USER_SETTINGS_ACTIVITY)
        }
    }
}
