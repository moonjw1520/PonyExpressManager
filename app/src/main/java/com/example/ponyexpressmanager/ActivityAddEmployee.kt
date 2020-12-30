package com.example.ponyexpressmanager

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_add_employee.*
import kotlinx.android.synthetic.main.activity_event_detail.*
import kotlinx.android.synthetic.main.activity_event_detail.et_event_detail
import okhttp3.*
import okio.IOException
import org.json.JSONArray
import org.json.JSONObject

class ActivityAddEmployee : AppCompatActivity() {

    var m_name = ""
    var m_mdn = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_employee)



        et_employee_name.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                Log.d("로그", p0.toString())
                m_name = p0.toString()
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        })

        et_employee_mdn.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                Log.d("로그", p0.toString())
                m_mdn = p0.toString()
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        })
    }




    fun InsertEmployee(v : View)
    {
        val url = CListCollector.AWS_DOMAIN +"/pony/Insert_Employee"
        val client : OkHttpClient = OkHttpClient()

        val body: RequestBody = FormBody.Builder().add("name",m_name).add("mdn", m_mdn).build()
        val request = Request.Builder().url(url).post(body).build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                //에러 메세지 출력
                Log.d("로그", e.toString())
            }
            override fun onResponse(call: Call, response: Response) {
                Log.d("로그", "${response.body.toString()}")
                //Update Main UI
                runOnUiThread {
                    getEmployeeID()
                }

            }
        })
    }



    fun getEmployeeID()
    {
        var id : String
        val url = CListCollector.AWS_DOMAIN+"/pony/select_employee_id"
        val client : OkHttpClient = OkHttpClient()
        val body: RequestBody = FormBody.Builder()
            .add("name", m_name)
            .add("mdn", m_mdn)
            .build()

        val request = Request.Builder().url(url).post(body).build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                //에러 메세지 출력
                Log.d("로그", e.toString())
            }

            override fun onResponse(call: Call, response: Response) {
                val str_response1 = response.body!!.string()
                var json_array: JSONArray = JSONArray(str_response1)

                var json_objdetail: JSONObject = json_array.getJSONObject(0)
                id = json_objdetail.getString("id")

                //Update Main UI
                runOnUiThread {
                    var tmpDeliveryMan  = CDeliveryPerson(id, m_name, m_mdn)

                    CListCollector.list_delivery_man.add(tmpDeliveryMan)
                    Toast.makeText(applicationContext, "직원이 추가 되었습니다.", Toast.LENGTH_SHORT).show()
                    finish()

                }
            }
        })

    }


    fun justOK(v : View)
    {
        finish()
    }




}