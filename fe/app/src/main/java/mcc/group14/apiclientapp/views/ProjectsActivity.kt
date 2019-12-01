package mcc.group14.apiclientapp.views

import android.app.ListActivity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
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

    private lateinit var userSettBtn: Button
    private lateinit var addProjectBtn: Button

    private lateinit var userEmail: String
    private lateinit var userAuth : String

    private var projectList: MutableList<UserProject>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.projects)

        this.title = "Your projects"

        userEmail = "news@aalto.fi"
        userAuth = "abc123"


        initUI()

        setListeners()

        // 1. get list of projects
        // 2. download a project on user tap

        val projectApi = ProjectApiClient.create()

        // downloading the list of projects
        var disposable = projectApi.getProjectsList(userEmail)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread()).map {
                if (it.success == "true") it.payload else throw Throwable("APIError")
            }
            .subscribe(
                {
                    result -> Log.d(TAG,"Project list received " +
                        "$result")
                    this.projectList = result
                    val proj_names = mutableListOf<String>()
                    for (proj in projectList.orEmpty()){
                        proj_names.add(proj.project_name)
                    }
                    Log.d(TAG, proj_names.toString())
                    // TODO: @Sasha beatify the listView
                    val adapter = ArrayAdapter<String>(this,
                        android.R.layout.simple_list_item_1, proj_names)
                    listAdapter = adapter
                },
                {
                        error -> Log.e(TAG, error.message)
                    Toast.makeText(applicationContext,"Error fetching the list of projects",
                        Toast.LENGTH_SHORT)

                }
            )
/*

            )*/
    }

    // we use this method to pass to the ProjectDetail
    override fun onListItemClick(l: ListView?, v: View?, position: Int, id: Long) {
        val intent =
            Intent(this, ProjectDetailActivity::class.java)
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

    private fun setListeners() {
        addProjectBtn.setOnClickListener {
            // call ProjectPresenter.addProjectToUser
        }

        userSettBtn.setOnClickListener {
            val intent =
                Intent(this, UserSettingsActivity::class.java).apply {
                    putExtra("USER_EMAIL", userEmail)
                    putExtra("USER_AUTH", userAuth)
                }
            startActivity(intent)
        }
    }
}
