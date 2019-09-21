package com.partytime.myparty

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SignupActivity : AppCompatActivity() {
    // [START declare_auth]
    private lateinit var auth: FirebaseAuth
    // [END declare_auth]
    // [START declare_database]
    private lateinit var db: FirebaseFirestore
    // [END declare_database]

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        // [START initialize_auth]
        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()
        // [END initialize_auth]
        if (auth.currentUser != null) {
            gotoMain()
        }

        db = FirebaseFirestore.getInstance()
    }

    // [START on_start_check_user]
    public override fun onStart() {
        super.onStart()
    }
    // [END on_start_check_user]

    private fun createAccount(email: String, password: String, fName: String, lName: String, age: Int) {
        Log.d(TAG, "createEmail:$email")
        Log.d(TAG, "createPassword:$password")
        Log.d(TAG, "createFirstname:$fName")
        Log.d(TAG, "createLastName:$lName")
        Log.d(TAG, "createAge:$age")

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "createUserWithEmail:success")

                    //create a user with the given signup details
                    val user = hashMapOf(
                        "firstName" to fName,
                        "lastName" to lName,
                        "age" to age,
                        "email" to email
                    )

                    db.collection("users").document(email)
                        .set(user)
                        .addOnSuccessListener {
                            gotoMain()
                        }
                        .addOnFailureListener {exception ->
                            Log.w("SignuoActivity", "Error adding user", exception)
                            Toast.makeText(baseContext, "Error creating account",
                                Toast.LENGTH_LONG).show()
                        }
                    gotoMain()
                } else {
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    Toast.makeText(baseContext, "Please enter a valid email and password. Passwords must " +
                            "contain at least 6 characters.",
                        Toast.LENGTH_LONG).show()
                }
            }
        // [END create_user_with_email]
    }

    private fun validateSignup(emailInput: EditText, passwordInput: EditText, fNameInput: EditText, lNameInput: EditText, ageInput: EditText): Boolean {
        var valid = true

        val fName = fNameInput.text.toString()
        if (TextUtils.isEmpty(fName)) {
            fNameInput.error = "Required."
            valid = false
        } else {
            fNameInput.error = null
        }

        val lName = lNameInput.text.toString()
        if (TextUtils.isEmpty(lName)) {
            lNameInput.error = "Required."
            valid = false
        } else {
            lNameInput.error = null
        }

        val email = emailInput.text.toString()
        if (TextUtils.isEmpty(email)) {
            emailInput.error = "Required."
            valid = false
        } else {
            emailInput.error = null
        }

        val age = ageInput.text.toString()
        if (TextUtils.isEmpty(age)) {
            ageInput.error = "Required."
            valid = false
        } else {
            ageInput.error = null
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

    fun clickSignup(view: View) {
        val firstNameText = findViewById<EditText>(R.id.firstNameInput)
        val lastNameText = findViewById<EditText>(R.id.lastNameInput)
        val ageText = findViewById<EditText>(R.id.ageInput)
        val emailText = findViewById<EditText>(R.id.emailInput)
        val passwordText = findViewById<EditText>(R.id.passwordInput)

        val valid = validateSignup(emailText, passwordText, firstNameText, lastNameText, ageText)

        if (valid) {
            val fName = firstNameText.text.toString()
            val lName = lastNameText.text.toString()
            val ageString = ageText.text.toString()
            val age = Integer.parseInt(ageString)
            val email = emailText.text.toString()
            val password = passwordText.text.toString()
            createAccount(email, password, fName, lName, age)
        }
    }

    fun gotoLogin(view: View) {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }

    private  fun gotoMain() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    companion object {
        private const val TAG = "SignupActivity"
    }
}
