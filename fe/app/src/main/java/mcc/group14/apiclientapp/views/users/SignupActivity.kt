package mcc.group14.apiclientapp.views.users

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.*
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.JsonParser
import com.valdesekamdem.library.mdtoast.MDToast
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import mcc.group14.apiclientapp.R
import mcc.group14.apiclientapp.api.ProjectApiClient
import mcc.group14.apiclientapp.data.UserRegistration
import mcc.group14.apiclientapp.views.projects.dashboard.ProjectsActivity
import mcc.group14.apiclientapp.views.projects.dashboard.ProjectsDashboardMainActivity
import mcc.group14.apiclientapp.views.users.LoginActivity
import retrofit2.HttpException
import java.lang.Exception

class SignupActivity : AppCompatActivity() {
    private var mAuth = FirebaseAuth.getInstance()
    val projectApi = ProjectApiClient.create()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        val spinner = findViewById<ProgressBar>(R.id.progressBar2)
        this.title = "Signup"
        spinner.visibility = View.INVISIBLE;
        val signupBtn = findViewById<Button>(R.id.btn_signup)
        val signInText = findViewById<TextView>(R.id.signIn_text)


        signInText.setOnClickListener{
            val intent =
                Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }


        signupBtn.setOnClickListener{
            val userEmail = findViewById<EditText>(R.id.txt_email_signup).text.toString()
            val userPassword = findViewById<EditText>(R.id.txt_password_signup).text.toString()
            val retypedPassword = findViewById<EditText>(R.id.txt_password_confirm).text.toString()
            val userDisplayName = findViewById<EditText>(R.id.txt_displayName).text.toString()

            if (isValidSignup(userEmail, userPassword, retypedPassword,userDisplayName)) {
                /*
                mAuth.createUserWithEmailAndPassword(userEmail, userPassword).addOnCompleteListener {task ->
                    if (task.isSuccessful) { // Show success msg and Redirect to login if signup succeeds
                        var t = Toast.makeText(this@SignupActivity,  "Registration successful!", Toast.LENGTH_LONG)
                        t.show()
                        val intent = Intent(this, LoginActivity::class.java)
                        startActivity(intent)
                    } else {
                        var t = Toast.makeText(this@SignupActivity,  "Sign up failed", Toast.LENGTH_LONG)
                        t.view.setBackgroundColor(Color.RED)
                        t.show()
                    }
                }
                 */
                //var disposable = this.projectApi.

                val user_registration = UserRegistration(
                    email_id = userEmail,
                    display_name = userDisplayName,
                    password = userPassword
                )
                spinner.visibility = View.VISIBLE;
                //this.projectApi.createUser(user_registration)
                val TAG = "CreateUser"
                var disposable = projectApi.createUser(user_registration)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                        {

                                result -> Log.d(TAG, "Signup successful, " + result.toString())
                                spinner.visibility = View.INVISIBLE;
                                val mdToast = MDToast.makeText(this@SignupActivity, "Registration successful!", 3, MDToast.TYPE_SUCCESS)
                                mdToast.show()
                                //Sign up done, now log in the user
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
                                                    this@SignupActivity,
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
                                                startActivity(intent)
                                            } else {
                                                val mdToast = MDToast.makeText(
                                                    this@SignupActivity,
                                                    "Could not log in. Please try again.",
                                                    3,
                                                    MDToast.TYPE_ERROR
                                                )
                                                mdToast.show()

                                                //======================Could not log in. So let him log in by himself
                                                val intent = Intent(this, LoginActivity::class.java)
                                                startActivity(intent)

                                            }
                                        }
                                }catch (e: Exception){
                                    spinner.visibility = View.INVISIBLE;
                                    //======================Some exception occured. So let him log in by himself
                                    val mdToast = MDToast.makeText(
                                        this@SignupActivity,
                                        "Could not log in. Please try again.",
                                        3,
                                        MDToast.TYPE_ERROR
                                    )
                                    mdToast.show()

                                    //======================Could not log in. So let him log in by himself
                                    val intent = Intent(this, LoginActivity::class.java)
                                    startActivity(intent)
                                }



                        },
                        {
                                error -> Log.d(TAG, "sign up failed, " + error.message.toString())
                                if (error is HttpException) {
                                    var error_msg = "Sign up failed."
                                    val errorJsonString = error.response().errorBody()?.string()
                                    val response = JsonParser().parse(errorJsonString).asJsonObject


                                    if (response.get("error").asString == "DisplayNameAlreadyExists") {
                                        //Log.d(TAG, response.get("payload").toString()
                                        //val suggestions = response.get("payload").asJsonObject
                                        val suggestions_obj = response.get("payload").asJsonObject
                                        val suggestions_msg = "1. ${suggestions_obj.get("suggestion_1").asString}\n" +
                                                "2. ${suggestions_obj.get("suggestion_2").asString}\n" +
                                                "3. ${suggestions_obj.get("suggestion_3").asString}\n"

                                        error_msg = "The display name \"${userDisplayName}\" is taken.\nYou can try one of the following\n${suggestions_msg}"
                                    } else if (response.get("error").asString == "DisplayNameNotProvided") {
                                        error_msg = "Please provide a display name"
                                    }

                                    val mdToast = MDToast.makeText(
                                        this@SignupActivity,
                                        error_msg,
                                        15,
                                        MDToast.TYPE_ERROR
                                    )
                                    mdToast.show()



                                }

                        }
                    )

            } else {
                spinner.visibility = View.INVISIBLE;
                val mdToast = MDToast.makeText(this@SignupActivity, "Please make sure that the Email is valid and Display name is not empty.\n Passwords must be at least 8 characters long and should match", MDToast.LENGTH_LONG, MDToast.TYPE_ERROR)
                mdToast.show()
            }

        }
    }

    private fun isValidSignup(email: String, password: String, retyped_password: String,userDisplayName:String):Boolean {
        return password == retyped_password && isValidPassword(password) && isValidEmail(email)  && isValidDisplayName(userDisplayName)
    }

    private fun isValidPassword(pw: String): Boolean {
        return pw.length >= 8
    }

    private fun isValidEmail(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    // todo
    private fun isValidDisplayName(dp_name: String): Boolean {
        return ""!=dp_name
    }

    private fun checkEmailExists(email: String): Boolean {
        return false
    }

    private fun checkDisplayNameExists(dp_name: String): Boolean {
        return false
    }

}
