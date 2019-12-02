package mcc.group14.apiclientapp.views

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import mcc.group14.apiclientapp.api.ProjectApiClient
import mcc.group14.apiclientapp.data.ProjectDetail
import android.app.Activity
import mcc.group14.apiclientapp.R


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

        if (requestCode == PROJECT_SETTINGS_ACTIVITY){
            if (resultCode == Activity.RESULT_OK){
                data?.let {
                    project =  it.getSerializableExtra("PROJECT")
                            as ProjectDetail
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
            project_type = if (capturedIsPersonalCB) "T" else "P",
            requester_email = userEmail,

            // TODO: these data are got from OptionalProjectSettingsActivity
            badge = "img.png",
            // TODO: validate keyword
            keywords = "k1, k2, k3",
            deadline = "2019-05-08",
            // always include userEmailRequester
            team_members = "usr1@mail.org, usr2@mail.ru, $userEmail"
        )
    }

    private fun postProject() {
        var disposable = projectApi.createProject(project)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { result ->
                    Log.v(TAG, "" + result)
                    val returnIntent = Intent()
                    setResult(Activity.RESULT_CANCELED, returnIntent)
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
        if (capturedName == "" || capturedName == "Project name") {
            missingField = "name"
            Toast.makeText(
                applicationContext,
                "Please fill project $missingField field.", Toast.LENGTH_LONG
            ).show()
            return true
        }
        if (capturedDescription == "" || capturedDescription == "Project description") {
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
