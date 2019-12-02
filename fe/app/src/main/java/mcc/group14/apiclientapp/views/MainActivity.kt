package mcc.group14.apiclientapp.views

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import mcc.group14.apiclientapp.R

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
            //  static cookie is also fine...
            val userEmail = "news@aalto.fi"
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

