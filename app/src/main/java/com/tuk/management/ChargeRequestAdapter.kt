package com.tuk.management

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button

class ChargeRequestAdapter(context: Context, private var dataList: MutableList<ChargePointWithDate>) :
    ArrayAdapter<ChargePointWithDate>(context, 0, dataList) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view: View = convertView ?: LayoutInflater.from(context).inflate(R.layout.item_charge_request, parent, false)
        val chargeRequestWithDate = getItem(position) as ChargePointWithDate
        val chargeRequest = chargeRequestWithDate.chargePoint

        val button: Button = view.findViewById(R.id.btn_charge_request)
        button.text = "${chargeRequest.userId}님이 ${chargeRequest.chargeRequest} 원 충전 요청"
        button.setOnClickListener {
            val intent = Intent(context, RequestHandleActivity::class.java).apply {
                putExtra("userId", chargeRequest.userId)
                putExtra("chargeRequest", chargeRequest.chargeRequest)
                putExtra("chargedate", chargeRequestWithDate.chargedate)
            }
            context.startActivity(intent)
        }

        return view
    }
}
