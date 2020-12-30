package com.example.ponyexpressmanager

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_add_car.*
import kotlinx.android.synthetic.main.activity_add_employee.*
import okhttp3.*
import okio.IOException
import org.json.JSONArray
import org.json.JSONObject

class ActivityAddCar : AppCompatActivity() {

    var mCarNum = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_car)

        et_car_num.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                Log.d("로그", p0.toString())
                mCarNum = p0.toString()
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        })


        btn_cancel_car.setOnClickListener {
            finish()
        }
    }

    fun InsertCar(v : View)
    {
        val url = CListCollector.AWS_DOMAIN +"/pony/insert_car"
        val client : OkHttpClient = OkHttpClient()

        val body: RequestBody = FormBody.Builder().add("car_num",mCarNum).build()
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
                    getCarID()
                }

            }
        })
    }

    fun getCarID()
    {
        var id : String
        val url = CListCollector.AWS_DOMAIN+"/pony/select_car_id"
        val client : OkHttpClient = OkHttpClient()
        val body: RequestBody = FormBody.Builder()
            .add("car_num", mCarNum)
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
                    var tmpCarInfo  = D_CarsInfo(id, mCarNum)
                    CListCollector.list_cars_info.add(tmpCarInfo)
                    Toast.makeText(applicationContext, "차량이 추가 되었습니다.", Toast.LENGTH_SHORT).show()
                    finish()

                }
            }
        })

    }

}