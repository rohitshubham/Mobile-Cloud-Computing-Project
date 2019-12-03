package mcc.group14.apiclientapp.views

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import mcc.group14.apiclientapp.R

class SignupActivity : AppCompatActivity() {
    private var mAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        this.title = "Signup"

        val signupBtn = findViewById<Button>(R.id.signup_btn)
        signupBtn.setOnClickListener{
            val userEmail = findViewById<EditText>(R.id.email).text.toString()
            val userPassword = findViewById<EditText>(R.id.password).text.toString()
            val retypedPassword = findViewById<EditText>(R.id.retyped_password).text.toString()
            if (isValidSignup(userEmail, userPassword, retypedPassword)) {
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
