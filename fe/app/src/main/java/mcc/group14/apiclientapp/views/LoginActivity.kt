package mcc.group14.apiclientapp.views

import android.content.Context
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import mcc.group14.apiclientapp.R

class LoginActivity : AppCompatActivity() {
    private var mAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        this.title = "Login"

        val login_btn = findViewById<Button>(R.id.login_btn)
        login_btn.setOnClickListener{
            val userEmail = findViewById<EditText>(R.id.email).text.toString()
            val userPassword = findViewById<EditText>(R.id.password).text.toString()

            mAuth.signInWithEmailAndPassword(userEmail, userPassword).addOnCompleteListener {task ->
                // If login successful, get the userAuth(a token) and send it to the next activity (ProjectActivity)
                val user = mAuth.currentUser
                //var t1 = Toast.makeText(this@LoginActivity, "email:${userEmail} and password: ${userPassword}", Toast.LENGTH_LONG)
                //t1.show()
                if (task.isSuccessful && user != null) {
                    val userAuth:String = user.uid
                    var t = Toast.makeText(this@LoginActivity,  "Logged in ${userEmail}!", Toast.LENGTH_LONG)
                    t.show()
                    // add email and userAuth (UUID) to SharedPreferences
                    val sharedprefs =  getSharedPreferences("USER_AUTH_DATA", Context.MODE_PRIVATE)
                    var editor = sharedprefs.edit()
                    editor.putString("user_email", userEmail)
                    editor.putString("user_auth", userAuth)
                    editor.commit()

                    // To retrieve these values from sharedprefs use
                    // sharedprefs.getString("user_email","defaultName")
                    // sharedprefs.getString("user_auth","defaultName")

                    // Login successful, redirect to dashboard
                    val intent =
                        Intent(this, ProjectsActivity::class.java).apply {
                            putExtra("USER_EMAIL", userEmail)
                            putExtra("USER_AUTH", userAuth)
                        }
                    startActivity(intent)
                } else {
                    var t = Toast.makeText(this@LoginActivity,  "Could not log in. Please try again.", Toast.LENGTH_LONG)
                    t.view.setBackgroundColor(Color.RED) // makes the failed login warning red for 'readability', can be modified @TODO Sasha
                    t.show()
                }
            }
        }
    }
}
