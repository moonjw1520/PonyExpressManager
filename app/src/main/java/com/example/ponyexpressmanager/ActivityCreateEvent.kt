package com.example.ponyexpressmanager

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.ponyexpressmanager.CListCollector.list_event
import com.example.ponyexpressmanager.CPushHttp.SendPushInsert
import com.example.ponyexpressmanager.CRefreshDatabase.getNow
import kotlinx.android.synthetic.main.activity_event_detail.*
import okhttp3.*
import okio.IOException
import org.json.JSONArray
import org.json.JSONObject
import java.util.*

class ActivityCreateEvent : AppCompatActivity() {

    var m_title = ""
    var m_event_date =""
    var m_event_time =""
    var m_palce =""
    var m_user_count =""
    var m_user_name=""
    var m_mdn =""
    var m_event_detail=""
    var m_now_date = ""
    var m_now_time = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_event)

        //행사 제목 저장
        et_event_title.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                Log.d("로그", p0.toString())
                m_title = p0.toString()
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        })
        //행사 장소 저장
        et_event_place.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                Log.d("로그", p0.toString())
                m_palce = p0.toString()
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        })

        //행사 인원 저장
        et_user_count.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                Log.d("로그", p0.toString())
                m_user_count = p0.toString()
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        })

        //고객 이름
        txt_name.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                Log.d("로그", p0.toString())
                m_user_name = p0.toString()
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        })

        //고객 전화번호
        txt_mdn.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                Log.d("로그", p0.toString())
                m_mdn = p0.toString()
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        })

        //행사 내용 저장
        et_event_detail.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                Log.d("로그", p0.toString())
                //m_modifyCotent = p0.toString()
                m_event_detail= p0.toString()
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        })
    }

    //-------------------------------------이벤트 관련수정 end 2020-12-17-------------------------------------


    fun makeZeroPlus(a_nNum: Int) : String
    {
        var str : String
        if( 10> a_nNum) {  str = "0${a_nNum}" }
        else            { str = "${a_nNum}" }
        return str
    }

    //-------------------------------------날짜 시간 Dialog 2020-12-17-------------------------------------
    // 시간 다이얼로그
    fun SetEventDate(v: View)
    {
        var calendar = Calendar.getInstance()
        var year = calendar.get(Calendar.YEAR)
        var month = calendar.get(Calendar.MONTH)
        var day = calendar.get(Calendar.DAY_OF_MONTH)

        var listener = DatePickerDialog.OnDateSetListener { datePicker, i, i2, i3 ->
            // i년 i2월 i3일


            txt_event_date.text = "${i}-${makeZeroPlus(i2 + 1)}-${makeZeroPlus(i3)}"
            m_event_date = "${i}-${makeZeroPlus(i2 + 1)}-${makeZeroPlus(i3)}"


        }

        var picker = DatePickerDialog(this, listener, year, month, day)
        picker.show()
    }

    // 시간 다이얼로그
    fun SetEventTime(v: View)
    {
        var calendar = Calendar.getInstance()
        var hour = calendar.get(Calendar.HOUR)
        var minute = calendar.get(Calendar.MINUTE)

        var listener = TimePickerDialog.OnTimeSetListener { timePicker, i, i2 ->
            //txt_event_time.text = "${i}:${i2}"
            txt_event_time.text = "${makeZeroPlus(i)}:${makeZeroPlus(i2)}"
            m_event_time = "${makeZeroPlus(i)}:${makeZeroPlus(i2)}"
        }

        var picker = TimePickerDialog(this, listener, hour, minute, false) // true하면 24시간 제
        picker.show()
    }


    fun just_Cencel(v: View)
    {
        finish()
    }


    fun InsertEvent(v: View)
    {
        m_now_date = getNow(2)
        m_now_time = getNow(3)

        val url = CListCollector.AWS_DOMAIN+"/hans/event/insert_event"
        val client : OkHttpClient = OkHttpClient()
        val body: RequestBody = FormBody.Builder()
            .add("ret_date", m_now_date)
            .add("ret_time", m_now_time)
            .add("user_name", m_user_name)
            .add("mdn", m_mdn)
            .add("event_date", m_event_date)
            .add("event_time", m_event_time)
            .add("title", m_title)
            .add("place", m_palce)
            .add("user_count", m_user_count)
            .add("event", m_event_detail)
            .build()


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

                    getEventID()

                }

            }
        })
    }

    fun getEventID()
    {
        var id : String
        val url = CListCollector.AWS_DOMAIN+"/hans/event/select_event_id"
        val client : OkHttpClient = OkHttpClient()
        val body: RequestBody = FormBody.Builder()
            .add("ret_date", m_now_date)
            .add("ret_time", m_now_time)
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
                    var tmpEvent  = CEvent(id,"null","null","null","null","null",m_user_name,m_mdn,"null",m_event_date,m_event_time,m_title,m_palce,m_user_count,m_event_detail)
                    tmpEvent.num = list_event.size+1
                    list_event.add(tmpEvent)
                    Toast.makeText(applicationContext, "행사가 추가 되었습니다.", Toast.LENGTH_SHORT).show()
                    SendPushInsert()
                    finish()

                }
            }
        })

    }



}