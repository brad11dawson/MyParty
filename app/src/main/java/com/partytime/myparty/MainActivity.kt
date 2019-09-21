package com.partytime.myparty

import android.content.ClipDescription
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    // [START declare_database]
    private lateinit var db: FirebaseFirestore
    // [END declare_database]
    private var partyList = ArrayList<PartyListItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        db = FirebaseFirestore.getInstance()

        val schoolSpinner: Spinner = findViewById(R.id.schoolSpinner)
        schoolSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(p0: AdapterView<*>?) {

            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                val school: String = schoolSpinner.selectedItem.toString()
                partyList.clear()
                fillPartyList(school)
            }
        }

        partyListView.hasFixedSize()
        partyListView.setLayoutManager(LinearLayoutManager(this))
        partyListView.itemAnimator = DefaultItemAnimator()
        partyListView.adapter = RecyclerAdapter(partyList, R.layout.partyrowlayout)
    }

    class RecyclerAdapter(val parties : List<PartyListItem>, val itemLayout: Int) : RecyclerView.Adapter<RecyclerViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolder {
            var view = LayoutInflater.from(parent.context).inflate(itemLayout, parent, false)
            return RecyclerViewHolder(view)
        }

        override fun getItemCount(): Int {
            return parties.size
        }

        override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) {
            var party = parties.get(position)
            holder.updateParty(party)

            holder.parentLayout.setOnClickListener {
                Log.d(TAG, "about to go to party view page. party id: ${party.partyId}")
                val context: Context = it.context
                var intent = Intent(context, PartyViewActivity::class.java)
                intent.putExtra("partyId", party.partyId)
                context.startActivity(intent)
            }
        }

    }

    class RecyclerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var nameView: TextView
        private var descriptionView: TextView
        private var dateTimeView: TextView
        var parentLayout: LinearLayout

        init {
            nameView = itemView.findViewById<TextView>(R.id.partyNameView)
            descriptionView = itemView.findViewById<TextView>(R.id.partyDescription)
            dateTimeView = itemView.findViewById<TextView>(R.id.dateTimeView)
            parentLayout = itemView.findViewById(R.id.partyLayout)
        }

        fun updateParty(party: PartyListItem) {
            nameView?.text = party.partyName
            descriptionView?.text = party.partyDescription
            dateTimeView?.text = party.dateTime
        }
    }

    class PartyListItem(name: String, description: String, id: String, date: String, time: String) {
        var partyName: String
        var partyDescription: String
        var partyId: String
        var dateTime: String

        init {
            partyName = name
            partyDescription = description
            dateTime = "$date at $time p.m."
            partyId = id
        }
    }

    fun signOut(view: View) {
        FirebaseAuth.getInstance().signOut()
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }

    fun gotoAddParty(view: View) {
        val intent = Intent(this, addPartyActivity::class.java)
        startActivity(intent)
    }


    fun fillPartyList(school: String) {
        db.collection("parties")
            .whereEqualTo("school", school)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val name = document.data.getValue("title").toString()
                    val description = document.data.getValue("description").toString()
                    val date = document.data.getValue("date").toString()
                    val time = document.data.getValue("time").toString()
                    val id = document.id
                    Log.d(TAG, "title of document: $name")
                    Log.d(TAG, "description of document: $description")
                    val partylistItem = PartyListItem(name, description, id, date, time)
                    partyList.add(partylistItem)
                }
                partyListView.adapter?.notifyDataSetChanged()
            }
    }

    companion object {
        private const val TAG = "MainActivity"
    }
}
