package com.partytime.myparty

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_add_party.*

class addPartyActivity : AppCompatActivity() {

    // [START declare_auth]
    private lateinit var auth: FirebaseAuth
    // [END declare_auth]
    // [START declare_database]
    private lateinit var db: FirebaseFirestore
    // [END declare_database]



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_party)

        // [START initialize_auth]
        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()
        // [END initialize_auth]
        if (auth.currentUser == null) {
            gotoLogin()
        }

        db = FirebaseFirestore.getInstance()

    }

    private fun gotoLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }

    private fun createParty(partyTitle: String, partyDescription: String, partyAddress: String, school: String, partyType: String, is21: Boolean, isAlcoholFree: Boolean, attendanceLimit: Boolean, date: String, time: String) {
        val user = auth.currentUser
        user?.let {
            val hostEmail = user.email

            val party = hashMapOf(
                "hostEmail" to hostEmail,
                "title" to partyTitle,
                "description" to partyDescription,
                "address" to partyAddress,
                "school" to school,
                "type" to partyType,
                "is21" to is21,
                "isAlcoholFree" to isAlcoholFree,
                "attendanceLimit" to attendanceLimit,
                "date" to date,
                "time" to time
            )

            db.collection("parties")
                .add(party)
                .addOnSuccessListener {
                    gotoHome() // maybe change l8r
                }
        }
    }

    private fun validateAddParty(partyTitleInput: EditText, partyDescriptionInput: EditText, partyAddressInput: EditText, dateInput: EditText, timeInput: EditText): Boolean {

        var valid = true

        val partyTitle = partyTitleInput.text.toString()
        if (TextUtils.isEmpty(partyTitle)) {
            partyTitleInput.error = "Required."
            valid = false
        } else {
            partyTitleInput.error = null
        }

        val partyDescription = partyDescriptionInput.text.toString()
        if (TextUtils.isEmpty(partyDescription)) {
            partyDescriptionInput.error = "Required."
            valid = false
        } else {
            partyDescriptionInput.error = null
        }

        val partyAddress = partyAddressInput.text.toString()
        if (TextUtils.isEmpty(partyAddress)) {
            partyAddressInput.error = "Required."
            valid = false
        } else {
            partyAddressInput.error = null
        }

        val partyDate = dateInput.text.toString()
        if (TextUtils.isEmpty(partyDate)) {
            dateInput.error = "Required."
            valid = false
        } else {
            dateInput.error = null
        }

        val partyTime = timeInput.text.toString()
        if (TextUtils.isEmpty(partyTime)) {
            timeInput.error = "Required."
            valid = false
        } else {
            timeInput.error = null
        }

        return valid
    }


    fun addPartyClick(view: View) {

        val partyTitle = findViewById<EditText>(R.id.partyTitleInput)
        val partyDescription = findViewById<EditText>(R.id.partyDescriptionInput)
        val partyAddress = findViewById<EditText>(R.id.partyAdressInput)
        val school = findViewById<Spinner>(R.id.schoolSpinner)
        val partyGroup = findViewById<RadioGroup>(R.id.radioGroup)
        val partyType = findViewById<RadioButton>(partyGroup.checkedRadioButtonId)
        val is21 = findViewById<Switch>(R.id.is21Toggle)
        val isAlcoholFree = findViewById<Switch>(R.id.isAlcoholFreeToggle)
        val attendanceLimit = findViewById<Switch>(R.id.isAttendanceLimit)
        val partyDate = findViewById<EditText>(R.id.dateText)
        val partyTime = findViewById<EditText>(R.id.timeText)

        val valid = validateAddParty(partyTitle, partyDescription, partyAddress, partyDate, partyTime)

        if (valid) {
            val pTitle = partyTitle.text.toString()
            val pDescription = partyDescription.text.toString()
            val pAddress = partyAddress.text.toString()
            val sSchool = school.selectedItem.toString()
            val pType = partyType.text.toString()
            val pIs21 = is21.isChecked()
            val pIsAlcoholFree = isAlcoholFree.isChecked()
            val pAttendanceLimit = attendanceLimit.isChecked()
            val date = partyDate.text.toString()
            val time = partyTime.text.toString()

            createParty(pTitle, pDescription, pAddress, sSchool, pType, pIs21, pIsAlcoholFree, pAttendanceLimit, date, time)
        }
        val text = "Party Successfully Added"
        val duration = Toast.LENGTH_SHORT
        val toast = Toast.makeText(applicationContext, text, duration)
        toast.show()
    }

    private fun gotoHome() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }
    companion object {
        private const val TAG = "AddPartyActivity"
    }

}
