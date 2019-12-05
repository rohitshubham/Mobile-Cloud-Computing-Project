package mcc.group14.apiclientapp.views.users

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import mcc.group14.apiclientapp.R
import mcc.group14.apiclientapp.views.projects.dashboard.ProjectsActivity
import com.valdesekamdem.library.mdtoast.MDToast
import java.lang.Exception

class LoginActivity : AppCompatActivity() {
    private var mAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        this.title = "Login"

        val loginBtn = findViewById<Button>(R.id.btn_login)
        val signUpText = findViewById<TextView>(R.id.signUp_text)


        signUpText.setOnClickListener{
            val intent =
                Intent(this, SignupActivity::class.java)
            startActivity(intent)
        }


        loginBtn.setOnClickListener{

            val userEmail = findViewById<EditText>(R.id.txt_email_signin).text.toString()
            val userPassword = findViewById<EditText>(R.id.txt_password_login).text.toString()


            //Validating email
            if (!userEmail.trim().matches(Regex("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"))){
                val mdToast = MDToast.makeText(this@LoginActivity, "Invalid email address.", 3, MDToast.TYPE_ERROR)
                mdToast.show()
                return@setOnClickListener
            }


            try {
                mAuth.signInWithEmailAndPassword(userEmail, userPassword)
                    .addOnCompleteListener { task ->
                        // If login successful, get the userAuth(a token) and send it to the next activity (ProjectActivity)
                        val user = mAuth.currentUser
                        //var t1 = Toast.makeText(this@LoginActivity, "email:${userEmail} and password: ${userPassword}", Toast.LENGTH_LONG)
                        //t1.show()
                        if (task.isSuccessful && user != null) {
                            val userAuth: String = user.uid

                            val mdToast = MDToast.makeText(
                                this@LoginActivity,
                                "Logged in as ${user.displayName}!",
                                2,
                                MDToast.TYPE_SUCCESS
                            )
                            mdToast.show()

                            // add email and userAuth (UUID) to SharedPreferences
                            val sharedprefs =
                                getSharedPreferences("USER_AUTH_DATA", Context.MODE_PRIVATE)
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
                            val mdToast = MDToast.makeText(
                                this@LoginActivity,
                                "Could not log in. Please try again.",
                                3,
                                MDToast.TYPE_ERROR
                            )
                            mdToast.show()


                        }
                    }
            }catch (e:Exception){
                val mdToast = MDToast.makeText(this@LoginActivity, "Oops! Something went wrong!", 3, MDToast.TYPE_ERROR)
                mdToast.show()
            }
        }
    }
}
