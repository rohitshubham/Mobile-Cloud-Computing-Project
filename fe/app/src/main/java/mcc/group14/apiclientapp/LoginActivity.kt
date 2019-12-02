package mcc.group14.apiclientapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import kotlinx.android.synthetic.main.activity_main.*

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        this.title = "Login"

        val login_btn = findViewById<Button>(R.id.login_btn)

        // opens projects activity
        login_btn.setOnClickListener{
            val intent =
                Intent(this, ProjectsActivity::class.java)
            startActivity(intent)
        }
    }
}