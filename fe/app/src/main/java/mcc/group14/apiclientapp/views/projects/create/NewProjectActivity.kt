package mcc.group14.apiclientapp.views.projects.create

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import mcc.group14.apiclientapp.R
import mcc.group14.apiclientapp.api.ProjectApiClient
import mcc.group14.apiclientapp.data.ProjectDetail


class NewProjectActivity : AppCompatActivity() {

    private val TAG = "NewProjectActivity"

    private val PROJECT_SETTINGS_ACTIVITY = 0

    private lateinit var project: ProjectDetail

    private lateinit var userEmail: String
    private lateinit var userAuth: String

    private val projectApi: ProjectApiClient = ProjectApiClient.create()

    private lateinit var projectNameET: EditText
    private lateinit var projectDescriptionET: EditText
    private lateinit var isSharedCB: CheckBox
    private lateinit var moreProjectSettingsBtn: Button
    private lateinit var createProjectBtn: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(mcc.group14.apiclientapp.R.layout.activity_new_project)

        title = "New project"

        // get the info from login/sign-up
        userEmail = intent.getStringExtra("USER_EMAIL")
        userAuth = intent.getStringExtra("USER_AUTH")

        initUI()
        setListeners()
    }

    private fun initUI() {
        projectNameET = findViewById(R.id.project_name_et)
        projectDescriptionET = findViewById(R.id.project_description_et)
        isSharedCB = findViewById(R.id.is_personal_cb)
        moreProjectSettingsBtn = findViewById(R.id.extra_project_settings_btn)
        createProjectBtn = findViewById(R.id.create_project_btn)
    }

    private fun setListeners() {
        // createProjectButton listener
        createProjectBtn.setOnClickListener {
            if (checkFieldsAndInitProj()) return@setOnClickListener
            // posting the newly created project
            postProject()
        }

        moreProjectSettingsBtn.setOnClickListener(){
            if (checkFieldsAndInitProj()) return@setOnClickListener

            val intent =
                Intent(this,
                    OptionalProjectSettingsActivity::class.java).apply {
                    putExtra("USER_EMAIL", userEmail)
                    putExtra("USER_AUTH", userAuth)
                    putExtra("PROJECT", project)
                }
            startActivityForResult(intent, PROJECT_SETTINGS_ACTIVITY)
        }
    }

    // get the ProjectDetail modified by the ProjectOptionalSettings
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // terminates newProjectActivity and sends the
        if (requestCode == PROJECT_SETTINGS_ACTIVITY){
            if (resultCode == Activity.RESULT_OK){
                data?.let {
                    project =  it.getSerializableExtra("PROJECT")
                            as ProjectDetail
                    Log.d(TAG,"$project")
                    // TODO: ++ post the complete project and terminate activity
                    // note post, uploads the current project and *terminates* this activity!
                    postProject()
                }
            }
        }

    }

    private fun checkFieldsAndInitProj(): Boolean {
        val capturedName = projectNameET.text.toString()
        val capturedDescription = projectDescriptionET.text.toString()

        // check if required fields have been filled yet, if they have not toast error
        if (checkRequiredFields(capturedName, capturedDescription)) return true

        // if isSharedCB is not checked then the project is personal
        val capturedIsPersonalCB = isSharedCB.isChecked

        // final
        initProject(capturedName, capturedDescription, capturedIsPersonalCB)

        Log.d(TAG, "Project creation: $project")
        return false
    }

    private fun initProject(
        capturedName: String,
        capturedDescription: String,
        capturedIsPersonalCB: Boolean
    ) {
        project = ProjectDetail(
            name = capturedName,
            description = capturedDescription,
            project_type = if (capturedIsPersonalCB) "Team" else "Personal",
            requester_email = userEmail,

            // always include userEmailRequester
            // TODO: ++ implement AddMembersToProject
            team_members = userEmail,
            // TODO: these data are got from OptionalProjectSettingsActivity
            badge = "DEFAULT_BADGE",
            // TODO: validate keywords
            keywords = "DEFAULT_KEYWORDS",
            deadline = "2019-08-15"
        )
    }

    private fun postProject() {
        var disposable = projectApi.createProject(project)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { result ->
                    Log.v(TAG, "POSTED: $result")
                    val returnIntent = Intent()
                   // returnIntent.putExtra("ERROR", )
                    if (result.success.orEmpty().toBoolean()) {
                        setResult(Activity.RESULT_OK, returnIntent)
                    }
                    finish()
                },
                { error ->
                    Log.e(TAG, error.message)
                    Toast.makeText(
                        applicationContext, "Network problem",
                        Toast.LENGTH_SHORT
                    )
                }
            )
    }

    private fun checkRequiredFields(
        capturedName: String,
        capturedDescription: String
    ): Boolean {
        var missingField: String? = null
        if (capturedName == "") {
            missingField = "name"
            Toast.makeText(
                applicationContext,
                "Please fill project $missingField field.", Toast.LENGTH_LONG
            ).show()
            return true
        }
        if (capturedDescription == "") {
            missingField = "description"
            Toast.makeText(
                applicationContext,
                "Please fill project $missingField field.", Toast.LENGTH_LONG
            ).show()
            return true
        }
        return false
    }
}
