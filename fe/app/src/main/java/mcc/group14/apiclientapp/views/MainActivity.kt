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
            val intent =
                Intent(this, ProjectsActivity::class.java)
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

