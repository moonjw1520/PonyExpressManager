package com.example.ponyexpressmanager

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.ponyexpressmanager.CGet.GetKindStr
import kotlinx.android.synthetic.main.activity_add_car.*
import kotlinx.android.synthetic.main.activity_add_packege.*
import okhttp3.*
import okio.IOException
import org.json.JSONArray
import org.json.JSONObject

class ActivityAddPackege : AppCompatActivity() {

    var m_package_kind = ""
    var m_package_name = ""
    var m_package_desc = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_packege)

        et_package_name.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                Log.d("로그", p0.toString())
                m_package_name = p0.toString()
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        })

        et_package_desc.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                Log.d("로그", p0.toString())
                m_package_desc = p0.toString()
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        })

        radio_group.setOnCheckedChangeListener { radioGroup, i ->
            when(i){
                R.id.rb_etc -> m_package_kind ="0"
                R.id.rb_today -> m_package_kind ="1"
                R.id.rb_one_day -> m_package_kind ="2"
                R.id.rb_two_day -> m_package_kind ="3"
                R.id.rb_three_day -> m_package_kind ="4"
            }
        }

        btn_add_package.setOnClickListener {
            InsertPackage(it)
        }

        btn_cancel_package.setOnClickListener {
            finish()
        }

    }

    fun InsertPackage(v : View)
    {
        val url = CListCollector.AWS_DOMAIN +"/pony/insert_package"
        val client : OkHttpClient = OkHttpClient()

        val body: RequestBody = FormBody.Builder().add("kind",m_package_kind).add("name",m_package_name).add("desc",m_package_desc).build()
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
                    getPackageID()
                }

            }
        })
    }

    fun getPackageID()
    {
        var id : String
        val url = CListCollector.AWS_DOMAIN+"/pony/select_package_id"
        val client : OkHttpClient = OkHttpClient()
        val body: RequestBody = FormBody.Builder()
            .add("kind", m_package_kind)
            .add("name", m_package_name)
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
                    var tmpMenuInfo  = D_PMenu(id, m_package_kind, GetKindStr(m_package_kind.toInt()), m_package_name, m_package_desc )
                    CListCollector.list_food_menu.add(tmpMenuInfo)
                    Toast.makeText(applicationContext, "패키지가 추가 되었습니다.", Toast.LENGTH_SHORT).show()
                    finish()

                }
            }
        })

    }


}