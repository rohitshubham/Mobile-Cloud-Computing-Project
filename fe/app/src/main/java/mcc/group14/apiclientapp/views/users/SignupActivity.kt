package mcc.group14.apiclientapp.views.users

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.JsonParser
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import mcc.group14.apiclientapp.R
import mcc.group14.apiclientapp.api.ProjectApiClient
import mcc.group14.apiclientapp.data.UserRegistration
import mcc.group14.apiclientapp.views.users.LoginActivity
import retrofit2.HttpException

class SignupActivity : AppCompatActivity() {
    private var mAuth = FirebaseAuth.getInstance()
    val projectApi = ProjectApiClient.create()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        this.title = "Signup"

        val signupBtn = findViewById<Button>(R.id.signup_btn)
        signupBtn.setOnClickListener{
            val userEmail = findViewById<EditText>(R.id.email).text.toString()
            val userPassword = findViewById<EditText>(R.id.password).text.toString()
            val retypedPassword = findViewById<EditText>(R.id.retyped_password).text.toString()
            val userDisplayName = findViewById<EditText>(R.id.display_name).text.toString()

            if (isValidSignup(userEmail, userPassword, retypedPassword)) {
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
                //this.projectApi.createUser(user_registration)
                val TAG = "CreateUser"
                var disposable = projectApi.createUser(user_registration)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                        {
                                result -> Log.d(TAG, "signup successful, " + result.toString())
                                var t = Toast.makeText(this@SignupActivity,  "Registration successful!", Toast.LENGTH_LONG)
                                t.show()
                                val intent = Intent(this, LoginActivity::class.java)
                                startActivity(intent)
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
                                        val suggestions_msg = "${suggestions_obj.get("suggestion_1").asString}, " +
                                                "${suggestions_obj.get("suggestion_2").asString} or " +
                                                "${suggestions_obj.get("suggestion_3").asString}"

                                        error_msg = "The display name \"${userDisplayName}\" is taken. Try ${suggestions_msg}"
                                    } else if (response.get("error").asString == "DisplayNameNotProvided") {
                                        error_msg = "Please provide a display name"
                                    }

                                    var t = Toast.makeText(this@SignupActivity, error_msg, Toast.LENGTH_LONG)
                                    t.view.setBackgroundColor(Color.RED)
                                    t.show()
                                }

                        }
                    )

            } else {
                var t = Toast.makeText(this@SignupActivity,  "Please make sure email and passwords are valid. Passwords must be at least 8 characters long and should match", Toast.LENGTH_LONG)
                //var t = Toast.makeText(this@SignupActivity,  "${userEmail}===${isValidPassword(userPassword)}", Toast.LENGTH_LONG)
                t.view.setBackgroundColor(Color.RED)
                t.show()
            }

        }
    }

    private fun isValidSignup(email: String, password: String, retyped_password: String):Boolean {
        return password == retyped_password && isValidPassword(password) && isValidEmail(email)
    }

    private fun isValidPassword(pw: String): Boolean {
        return pw.length >= 8
    }

    private fun isValidEmail(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    // todo
    private fun isValidDisplayName(dp_name: String): Boolean {
        return false
    }

    private fun checkEmailExists(email: String): Boolean {
        return false
    }

    private fun checkDisplayNameExists(dp_name: String): Boolean {
        return false
    }

}
