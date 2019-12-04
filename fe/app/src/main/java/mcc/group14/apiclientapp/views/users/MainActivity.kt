package mcc.group14.apiclientapp.views.users

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import mcc.group14.apiclientapp.R
import mcc.group14.apiclientapp.views.projects.dashboard.ProjectsActivity

// TODO - legend:
//    --: not urgent at all
//    - : not very urgent
//    + : quite urgent
//    ++: very urgent

// Sign-up activity
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        this.title = "Signup"

        val signup_btn = findViewById<Button>(R.id.signup_btn)
        val login_btn = findViewById<Button>(R.id.login_btn)

        // opens projects activity
        signup_btn.setOnClickListener{

            // TODO: @Kirthi get the following from the user, userAuth is the cookie,
            //  go for sharedPreferences
            val userEmail = "this@that.com"
            // val userEmail = "news@aalto.fi"
            val userAuth = "abc123"

            // Go to the dashboard (ProjectsActivity)
            // Note: this is the way you pass stuff among activities, look at how I did it
            // in NewProjectActivity (I made ProjectDetail Serializable and just passed the stuff around)
            val intent =
                Intent(this, ProjectsActivity::class.java).apply {
                    putExtra("USER_EMAIL", userEmail)
                    putExtra("USER_AUTH", userAuth)
                }
            startActivity(intent)
        }

        // opens login activity
        login_btn.setOnClickListener{
            val intent =
                Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }

}

