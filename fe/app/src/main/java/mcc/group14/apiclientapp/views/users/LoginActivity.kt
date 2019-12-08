package mcc.group14.apiclientapp.views.users

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.*
import com.google.firebase.auth.FirebaseAuth
import com.valdesekamdem.library.mdtoast.MDToast
import mcc.group14.apiclientapp.views.projects.dashboard.ProjectsDashboardMainActivity
import java.lang.Exception



class LoginActivity : AppCompatActivity() {

    private var mAuth = FirebaseAuth.getInstance()

    private lateinit var spinner: ProgressBar
    private lateinit var loginBtn: Button
    private lateinit var passET: EditText
    private lateinit var emailET: EditText
    companion object{
        private val TAG: String = "LoginActivity"
    }

    override fun onBackPressed() {
        // leaves the back stack as it is,
        // just puts the task (all activities) in background.
        // Same as if user pressed Home button.

        moveTaskToBack(true)
        super.onBackPressed()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(mcc.group14.apiclientapp.R.layout.activity_login)

        this.title = "Login"

        val signUpText = findViewById<TextView>(mcc.group14.apiclientapp.R.id.signUp_text)

        setUpListeners()

        activateLoginButton()

        signUpText.setOnClickListener{

            val intent =  Intent(this, SignupActivity::class.java)
            startActivity(intent)

            passET.setText("")
        }


        loginBtn.setOnClickListener{

            val userEmail = emailET.text.toString()

            val userPassword = passET.text.toString()

            setLoading()

            //Validating email
            if (!userEmail.trim().matches(Regex("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"))){
                val mdToast = MDToast.makeText(this@LoginActivity, "Invalid email address.", 3, MDToast.TYPE_ERROR)
                mdToast.show()

                activateLoginButton()

                return@setOnClickListener
            }


            try {
                mAuth.signInWithEmailAndPassword(userEmail, userPassword)
                    .addOnCompleteListener { task ->
                        // If login successful, get the userAuth(a token) and send it to the next mActivity (ProjectActivity)
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
                                Intent(this,
                                    ProjectsDashboardMainActivity::class.java).apply {

                                    putExtra("USER_EMAIL", userEmail)
                                    putExtra("USER_AUTH", userAuth)
                                }

                            setLoading()

                            startActivity(intent)

                            passET.setText("")

                        } else {
                            val mdToast = MDToast.makeText(
                                this@LoginActivity,
                                "Could not log in. Please try again.",
                                3,
                                MDToast.TYPE_ERROR
                            )
                            mdToast.show()

                            activateLoginButton()
                        }
                    }
            } catch (e:Exception){

                activateLoginButton()

                Log.d(TAG, e.message)

                val mdToast = MDToast.makeText(this@LoginActivity,
                    "Oops! Something went wrong!", 3, MDToast.TYPE_ERROR)
                mdToast.show()

            }
        }
    }

    private fun setUpListeners() {
        spinner = findViewById(mcc.group14.apiclientapp.R.id.progressBar3)
        loginBtn = findViewById(mcc.group14.apiclientapp.R.id.btn_login)
        passET = findViewById<EditText>(mcc.group14.apiclientapp.R.id.txt_password_login)
        emailET = findViewById<EditText>(mcc.group14.apiclientapp.R.id.txt_email_signin)
    }

    override fun onRestart() {
        super.onRestart()
        activateLoginButton()
    }

    private fun setLoading() {
        spinner.visibility = View.VISIBLE
        loginBtn.visibility = View.INVISIBLE
    }

    private fun activateLoginButton() {
        spinner.visibility = View.INVISIBLE
        loginBtn.visibility = View.VISIBLE
    }
}
