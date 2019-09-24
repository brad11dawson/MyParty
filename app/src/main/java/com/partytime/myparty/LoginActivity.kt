package com.partytime.myparty

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {


    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()
        if (auth.currentUser != null) {
            gotoMain()
        }
    }

    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
    }

    private fun loginEmail(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Success. Now we take them to the main screen
                    Log.d(TAG, "signInWithEmail:success")
                    gotoMain()
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithEmail:failure", task.exception)
                    Toast.makeText(baseContext, "Email and password did not match.l",
                        Toast.LENGTH_SHORT).show()
                    //updateUI(null)
                }
            }
    }

    private fun validateLogin(emailInput: EditText, passwordInput: EditText): Boolean {
        var valid = true

        val email = emailInput.text.toString()
        if (TextUtils.isEmpty(email)) {
            emailInput.error = "Required."
            valid = false
        } else {
            emailInput.error = null
        }

        val password = passwordInput.text.toString()
        if (TextUtils.isEmpty(password)) {
            passwordInput.error = "Required."
            valid = false
        } else {
            passwordInput.error = null
        }
        return valid
    }

    fun loginButton(view: View) {
        val emailText = findViewById<EditText>(R.id.emailInput)
        val passwordText = findViewById<EditText>(R.id.passwordInput)
        val isValid = validateLogin(emailText, passwordText)
        if (isValid) {
            val email = emailText.text.toString()
            val password = passwordText.text.toString()
            loginEmail(email, password)
        }
    }

    fun gotoSignup(view: View) {
        val intent = Intent(this, SignupActivity::class.java)
        startActivity(intent)
    }

    private  fun gotoMain() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    companion object {
        private const val TAG = "LoginActivity"
    }
}
