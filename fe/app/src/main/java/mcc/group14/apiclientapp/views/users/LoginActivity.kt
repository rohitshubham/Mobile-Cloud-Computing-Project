package mcc.group14.apiclientapp.views.users

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.*
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.iid.FirebaseInstanceId
import mcc.group14.apiclientapp.R
import com.valdesekamdem.library.mdtoast.MDToast
import mcc.group14.apiclientapp.views.projects.dashboard.ProjectsDashboardMainActivity
import java.lang.Exception

class LoginActivity : AppCompatActivity() {
    private var mAuth = FirebaseAuth.getInstance()
    var TAG = "MyMessagingActivity"
    var registrationtoken = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        this.title = "Login"

        val loginBtn = findViewById<Button>(R.id.btn_login)
        val signUpText = findViewById<TextView>(R.id.signUp_text)
        val spinner = findViewById<ProgressBar>(R.id.progressBar3)
        loginBtn.visibility = View.VISIBLE
        spinner.visibility = View.INVISIBLE;
        signUpText.setOnClickListener{
            val intent =
                Intent(this, SignupActivity::class.java)
            startActivity(intent)
        }

//        FirebaseInstanceId.getInstance().instanceId
//            .addOnCompleteListener(OnCompleteListener { task ->
//                if (!task.isSuccessful) {
//                    Log.w(TAG, "getInstanceId failed", task.exception)
//                    return@OnCompleteListener
//                }
//
//                // Get new Instance ID token
//                val token = task.result?.token
//
//                // Log and toast
//                val msg = getString(R.string.msg_token_fmt, token)
//                Log.d(TAG, msg)
//                //Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
//                registrationtoken = msg
//            })

        loginBtn.setOnClickListener{

            val userEmail = findViewById<EditText>(R.id.txt_email_signin).text.toString()
            val userPassword = findViewById<EditText>(R.id.txt_password_login).text.toString()
            loginBtn.visibility = View.INVISIBLE;
            spinner.visibility = View.VISIBLE;

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
                                Intent(this, ProjectsDashboardMainActivity::class.java).apply {
                                    putExtra("USER_EMAIL", userEmail)
                                    putExtra("USER_AUTH", userAuth)
                                }
                            loginBtn.visibility = View.VISIBLE
                            spinner.visibility = View.INVISIBLE
                            startActivity(intent)
                        } else {
                            loginBtn.visibility = View.VISIBLE
                            spinner.visibility = View.INVISIBLE
                            val mdToast = MDToast.makeText(
                                this@LoginActivity,
                                "Invalid email or password",
                                3,
                                MDToast.TYPE_ERROR
                            )
                            mdToast.show()



                        }
                    }
            }catch (e:Exception){
                loginBtn.visibility = View.VISIBLE
                spinner.visibility = View.INVISIBLE

                val mdToast = MDToast.makeText(this@LoginActivity, "Oops! Something went wrong!", 3, MDToast.TYPE_ERROR)
                mdToast.show()
            }
        }
    }
}
