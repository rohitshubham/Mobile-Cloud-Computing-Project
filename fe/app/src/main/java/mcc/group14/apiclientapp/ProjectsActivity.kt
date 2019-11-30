package mcc.group14.apiclientapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button


// Projects activity
class ProjectsActivity : AppCompatActivity() {

    private lateinit var userEmail: String
    private lateinit var userAuth : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.projects)

        this.title = "Your projects"

        userEmail = "user1@mail.org"
        userAuth = "abc123"

        val userSettBtn = findViewById<Button>(R.id.user_settings_btn)
        findViewById<Button>(R.id.user_settings_btn)

        userSettBtn.setOnClickListener{
            val intent =
                Intent(this, UserSettingsActivity::class.java).
                    apply {
                        putExtra("USER_EMAIL", userEmail)
                        putExtra("USER_AUTH", userAuth)
                    }
            startActivity(intent)
        }
    }
}
