package com.tuk.management

import android.content.ContentValues.TAG
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import com.google.firebase.database.*

class RequestHandleActivity : AppCompatActivity() {
    private lateinit var database: FirebaseDatabase
    private lateinit var userRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_request_handle)

        database = FirebaseDatabase.getInstance()
        userRef = database.getReference("user")

        val userId = intent.getStringExtra("userId")
        val chargeRequest = intent.getLongExtra("chargeRequest", 0L)
        val chargedate = intent.getStringExtra("chargedate")

        findViewById<Button>(R.id.btn_confirm).setOnClickListener {
            if (userId != null && chargedate != null) {
                val chargePointRef = userRef.child(userId).child("ChargePoint").child(chargedate)
                chargePointRef.runTransaction(object : Transaction.Handler {
                    override fun doTransaction(mutableData: MutableData): Transaction.Result {
                        val chargePoint = mutableData.getValue(ChargePoint::class.java)
                            ?: return Transaction.success(mutableData)
                        chargePoint.chargeAllow = chargeRequest.toInt()
                        mutableData.value = chargePoint
                        return Transaction.success(mutableData)
                    }

                    override fun onComplete(
                        databaseError: DatabaseError?,
                        committed: Boolean,
                        dataSnapshot: DataSnapshot?
                    ) {
                        // Handle completion
                    }
                })
                // 추가된 코드: userPoint 업데이트
                val userPointRef = userRef.child(userId).child("userPoint")
                userPointRef.runTransaction(object : Transaction.Handler {
                    override fun doTransaction(mutableData: MutableData): Transaction.Result {
                        val currentPoint = mutableData.getValue(Long::class.java)
                        if (currentPoint != null) {
                            mutableData.value = currentPoint + chargeRequest
                        }
                        return Transaction.success(mutableData)
                    }

                    override fun onComplete(
                        databaseError: DatabaseError?,
                        committed: Boolean,
                        dataSnapshot: DataSnapshot?
                    ) {
                        // Handle completion
                        finish()
                    }
                })
            }

        }

        findViewById<Button>(R.id.btn_reject).setOnClickListener {
            if (userId != null && chargedate != null) {
                val chargePointRef = userRef.child(userId).child("ChargePoint").child(chargedate)
                chargePointRef.runTransaction(object : Transaction.Handler {
                    override fun doTransaction(mutableData: MutableData): Transaction.Result {
                        val chargePoint = mutableData.getValue(ChargePoint::class.java)
                            ?: return Transaction.success(mutableData)
                        chargePoint.chargeAllow = -1 // 거절된 경우 ChargeAllow 값을 -1로 설정
                        mutableData.value = chargePoint
                        return Transaction.success(mutableData)
                    }

                    override fun onComplete(
                        databaseError: DatabaseError?,
                        committed: Boolean,
                        dataSnapshot: DataSnapshot?
                    ) {
                        // Handle completion
                        finish()
                    }
                })
                // userPoint는 변경하지 않습니다.
            }

        }
    }
}
