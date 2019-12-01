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


class NewProjectActivity : AppCompatActivity() {

    private val TAG = "NewProjectActivity"

    private lateinit var project: ProjectDetail

    private lateinit var userEmail: String
    private lateinit var userAuth: String

    private val projectApi: ProjectApiClient = ProjectApiClient.create()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(mcc.group14.apiclientapp.R.layout.activity_new_project)

        title = "New project"

        // get the info from login/sign-up
        userEmail = intent.getStringExtra("USER_EMAIL")
        userAuth = intent.getStringExtra("USER_AUTH")


        var projectNameET = findViewById<EditText>(mcc.group14.apiclientapp.R.id.project_name_et)
        val projectDescriptionET = findViewById<EditText>(mcc.group14.apiclientapp.R.id.project_description_et)
        val isSharedCB = findViewById<CheckBox>(mcc.group14.apiclientapp.R.id.is_personal_cb)
        val moreProjectSettingsBtn = findViewById<Button>(mcc.group14.apiclientapp.R.id.extra_project_settings_btn)
        val createProjectBtn = findViewById<Button>(mcc.group14.apiclientapp.R.id.create_project_btn)

        createProjectBtn.setOnClickListener{
            val capturedName = projectNameET.text.toString()
            val capturedDescription = projectDescriptionET.text.toString()

            // check if required fields have been filled yet, if they have not toast error
            var missingField : String?= null
            if (capturedName == "" || capturedName == "Project name"){
                missingField = "name"
                Toast.makeText(applicationContext,
                    "Please fill project $missingField field.", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            if (capturedDescription == "" || capturedDescription == "Project description"){
                missingField = "description"
                Toast.makeText(applicationContext,
                    "Please fill project $missingField field.", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            // if isSharedCB is not checked then the project is personal
            val capturedIsPersonalCB = isSharedCB.isChecked
            // final

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

            Log.d(TAG, "Project creation: $project")



            // posting the newly created project
            var disposable = projectApi.createProject(project)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { result -> Log.v(TAG, "" + result )
                        val returnIntent = Intent()
                        setResult(Activity.RESULT_CANCELED, returnIntent)
                        finish()
                    },
                    { error -> Log.e(TAG, error.message )
                        Toast.makeText(applicationContext, "Network problem",
                            Toast.LENGTH_SHORT)
                    }
                )
        }
    }


}
