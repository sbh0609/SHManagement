package com.tuk.management


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ListView
import com.google.firebase.database.*


class MainActivity : AppCompatActivity() {

    private lateinit var database: FirebaseDatabase
    private lateinit var userRef: DatabaseReference
    private lateinit var chargePointAdapter: ChargeRequestAdapter
    private var chargePointList = ArrayList<ChargePointWithDate>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        database = FirebaseDatabase.getInstance()
        userRef = database.getReference("user")

        chargePointAdapter = ChargeRequestAdapter(this, chargePointList)
        findViewById<ListView>(R.id.list_view).adapter = chargePointAdapter

        userRef.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(dataSnapshot: DataSnapshot, prevChildKey: String?) {
                val userId = dataSnapshot.key
                if (userId != null) {
                    addChargePointListener(userId)
                }
            }

            override fun onChildRemoved(dataSnapshot: DataSnapshot) {}

            override fun onChildChanged(dataSnapshot: DataSnapshot, prevChildKey: String?) {}

            override fun onChildMoved(dataSnapshot: DataSnapshot, prevChildKey: String?) {}

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    private fun addChargePointListener(userId: String) {
        val chargePointRef = userRef.child(userId).child("ChargePoint")
        chargePointRef.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(dataSnapshot: DataSnapshot, prevChildKey: String?) {
                val chargePoint = dataSnapshot.getValue(ChargePoint::class.java)
                val chargedate = dataSnapshot.key
                if (chargePoint != null && chargedate != null) {
                    // chargeAllow 필드가 0이 아니면, 해당 요청은 이미 처리되었음을 의미하므로 리스트에 추가하지 않음.
                    if (chargePoint.chargeAllow == 0) {
                        val chargePointWithDate = ChargePointWithDate(chargePoint, chargedate)
                        chargePointList.add(chargePointWithDate)
                        chargePointAdapter.notifyDataSetChanged()
                    }
                }
            }


            override fun onChildChanged(dataSnapshot: DataSnapshot, prevChildKey: String?) {
                val updatedChargePoint = dataSnapshot.getValue(ChargePoint::class.java)
                val updatedChargedate = dataSnapshot.key
                if (updatedChargePoint != null && updatedChargedate != null) {
                    val iterator = chargePointList.iterator()
                    while (iterator.hasNext()) {
                        val cpwd = iterator.next()
                        if (cpwd.chargePoint.userId == updatedChargePoint.userId && cpwd.chargedate == updatedChargedate) {
                            if (updatedChargePoint.chargeAllow != 0) {
                                iterator.remove()
                            } else {
                                cpwd.chargePoint.chargeAllow = updatedChargePoint.chargeAllow
                            }
                            chargePointAdapter.notifyDataSetChanged()
                            break
                        }
                    }
                }
            }

            override fun onChildRemoved(dataSnapshot: DataSnapshot) {}

            override fun onChildMoved(dataSnapshot: DataSnapshot, prevChildKey: String?) {}

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }
}

data class ChargePoint(
    var userId: String = "",
    var chargeRequest: Long = 0L,
    var chargeAllow: Int = 0
)
data class ChargePointWithDate(
    val chargePoint: ChargePoint,
    val chargedate: String
)
