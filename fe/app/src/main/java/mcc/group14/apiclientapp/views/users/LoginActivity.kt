package mcc.group14.apiclientapp.views.users

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import mcc.group14.apiclientapp.R
import mcc.group14.apiclientapp.views.projects.dashboard.ProjectsActivity

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        this.title = "Login"

        val login_btn = findViewById<Button>(R.id.login_btn)

        // opens projects activity
        login_btn.setOnClickListener{
            // TODO: @Kirthi get the following from the user, userAuth is the cookie,
            //  go for sharedPreferences

            // Go to the dashboard (ProjectsActivity)
            // Note: this is the way you pass stuff among activities, the only problem is that
            // object passed like this must be serializable, that is why mAuth should prolly be
            // static.
            val userEmail = "news@aalto.fi"
            val userAuth = "abc123"

            val intent =
                Intent(this, ProjectsActivity::class.java).apply {
                    putExtra("USER_EMAIL", userEmail)
                    putExtra("USER_AUTH", userAuth)
                }
            startActivity(intent)
        }
    }
}
