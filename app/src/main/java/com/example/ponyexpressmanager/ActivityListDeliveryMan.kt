package com.example.ponyexpressmanager

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ListView
import android.widget.Toast
import com.example.ponyexpressmanager.CListCollector.list_delivery_man
import com.example.ponyexpressmanager.CListCollector.list_event
import kotlinx.android.synthetic.main.activity_list_delivery_man.*
import okhttp3.*
import okio.IOException

class ActivityListDeliveryMan : AppCompatActivity() {

    lateinit var m_event_id : String
    lateinit var m_event_date : String
    var m_nEvent_Index : Int = 0
    //var arraList_items: ArrayList<CDeliveryPerson> = ArrayList()
    var send_sms_list: ArrayList<D_DeliveryPerson> = ArrayList()
    private lateinit var m_Adapter : AdapterListDelivery


    var m_nFlag = 0 // 0: 이벤트 배정 1: 삭제
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_delivery_man)

        m_event_id = intent.getStringExtra("id") as String
        m_event_date = intent.getStringExtra("event_date") as String
        m_nEvent_Index = intent.getIntExtra("event_index",0)
        m_nFlag = intent.getIntExtra("flag",0)


        //확인 버튼 해당 이벤트를 찾아서 배정된 직원을 리스트에 넣는다.

        btn_Ok.setOnClickListener {
            val checkedItem = listView.checkedItemPositions

            if(list_delivery_man.size == 0 ) { finish() }

            deleteEventInDeliveryMan()

            //for문과 동일
            //(m_Adapter.count downTo 0)
            (0 until  m_Adapter.count)
                .filter { checkedItem.get(it) }
                .forEach { send_sms_list.add(D_DeliveryPerson(list_delivery_man[it].id,list_delivery_man[it].name, list_delivery_man[it].mdn,  "0", "미정") )

                    if(m_nFlag == 0) {
                        list_delivery_man[it].List_Event.add(
                            D_Event(
                                CListCollector.list_event[m_nEvent_Index].id,
                                CListCollector.list_event[m_nEvent_Index].signature,
                                CListCollector.list_event[m_nEvent_Index].signature_datetime,
                                CListCollector.list_event[m_nEvent_Index].send_sms,
                                CListCollector.list_event[m_nEvent_Index].signature_send_sms,
                                CListCollector.list_event[m_nEvent_Index].send_sms_datetime,
                                CListCollector.list_event[m_nEvent_Index].user_name,
                                CListCollector.list_event[m_nEvent_Index].mdn,
                                CListCollector.list_event[m_nEvent_Index].file1,
                                CListCollector.list_event[m_nEvent_Index].event_date,
                                CListCollector.list_event[m_nEvent_Index].event_time,
                                CListCollector.list_event[m_nEvent_Index].title,
                                CListCollector.list_event[m_nEvent_Index].place,
                                CListCollector.list_event[m_nEvent_Index].user_count,
                                CListCollector.list_event[m_nEvent_Index].event,
                                "0",
                                "미정"
                            )
                        )
                    }

                }


            if(m_nFlag == 0)
            {
                CListCollector.list_event[m_nEvent_Index].List_delivery_man.clear()
                for (inx in 0 until send_sms_list.size)
                {
                    CListCollector.list_event[m_nEvent_Index].List_delivery_man.add(send_sms_list.get(inx))
                }
                DeleteMatching()
            }
            else //직원 삭제 할때
            {
                for(i in 0 until send_sms_list.size)
                {
                    for(inx in 0 until list_delivery_man.size)
                    {
                        if(send_sms_list[i].id == list_delivery_man[inx].id)
                        {
                            list_delivery_man.removeAt(inx)
                            break
                        }
                    }
                }

                for(i in 0 until send_sms_list.size)
                {
                    for(inx in 0 until list_event.size)
                    {
                        for(index in 0 until  list_event[i].List_delivery_man.size)
                        {
                            if(send_sms_list[i].id == list_event[i].List_delivery_man[index].id)
                            {
                                list_event[i].List_delivery_man.removeAt(index)
                            }
                        }
                    }
                }



                for(i in 0 until send_sms_list.size)
                {
                    DeleteEmployee(send_sms_list[i].id)
                }
            }

            finish()
        }

        btn_Cancel.setOnClickListener {
            finish()
        }

        //배달기사 리스트를 가져옴
        //getDeliveryList()

        m_Adapter =  AdapterListDelivery(this@ActivityListDeliveryMan ,list_delivery_man, m_nEvent_Index, m_event_id)
        listView.adapter = m_Adapter
        //리스트뷰의 선택모드를 다중선택모드로 설정
        listView.choiceMode = ListView.CHOICE_MODE_MULTIPLE
    }

    fun deleteEventInDeliveryMan()
    {
        //해당 이벤트 매칭을  DB에서 지운다
        for (i in 0 until list_delivery_man.size) {
            for (index in 0 until list_delivery_man[i].List_Event.size) {
                if (list_delivery_man[i].List_Event[index].id == CListCollector.list_event[m_nEvent_Index].id) {
                    list_delivery_man[i].List_Event.removeAt(index)
                    break
                }
            }
        }
    }


    //-------------------matching DB insert--------------------------
    //fun InsertMatching(v: View, a_delivery_id: String) {
    fun InsertMatching(a_delivery_id: String) {
        val url = "${CListCollector.AWS_DOMAIN}/hans/matching"
        val client : OkHttpClient = OkHttpClient()

        val body: RequestBody = FormBody.Builder().add("event_id",m_event_id).add("delivery_id",a_delivery_id).build()
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
                    Log.d("로그","InsertMatching.....")
                    Toast.makeText( applicationContext, "저장 되었습니다.", Toast.LENGTH_SHORT).show()
                }

            }
        })
    }

    //-------------------matching DB delete--------------------------
    fun DeleteMatching() {
        val url = "${CListCollector.AWS_DOMAIN}/hans/matching_delete"
        val client: OkHttpClient = OkHttpClient()
        Log.d("로그", "Delete event_id: ${m_event_id}")
        val body: RequestBody = FormBody.Builder().add("event_id", m_event_id).build()
        val request = Request.Builder().url(url).post(body).build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                //에러 메세지 출력
                Log.d("로그", e.toString())
            }

            override fun onResponse(call: Call, response: Response) {
                Log.d("로그", "${response.body.toString()}")
                //Update Main UI
                Log.d("로그","DeleteMatching.....")
                runOnUiThread {
                    //Toast.makeText(v.context, "저장 되었습니다.", Toast.LENGTH_SHORT).show()

                    for(i in 0 until  list_event[m_nEvent_Index].List_delivery_man.size)
                    {
                        Log.d("로그","insert name :${list_event[m_nEvent_Index].List_delivery_man[i].name}")
                        InsertMatching(list_event[m_nEvent_Index].List_delivery_man[i].id)
                    }
                }
            }
        })
    }

    fun DeleteEmployee(a_id : String) {
        val url = "${CListCollector.AWS_DOMAIN}/pony/delete_employee"
        val client: OkHttpClient = OkHttpClient()
        Log.d("로그", "Delete delivery_id: ${a_id}")
        val body: RequestBody = FormBody.Builder().add("id", a_id).build()
        val request = Request.Builder().url(url).post(body).build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                //에러 메세지 출력
                Log.d("로그", e.toString())
            }

            override fun onResponse(call: Call, response: Response) {
                Log.d("로그", "${response.body.toString()}")
                //Update Main UI
                Log.d("로그","DeleteMatching.....")
                runOnUiThread {
                    Toast.makeText(applicationContext, "삭제 되었습니다.", Toast.LENGTH_SHORT).show()

                }
            }
        })
    }


}