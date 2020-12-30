package com.example.ponyexpressmanager

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ListView
import android.widget.Toast
import com.example.ponyexpressmanager.CListCollector.AWS_DOMAIN
import com.example.ponyexpressmanager.CListCollector.list_cars_info
import com.example.ponyexpressmanager.CListCollector.list_delivery_man
import com.example.ponyexpressmanager.CListCollector.list_event
import kotlinx.android.synthetic.main.activity_list_delivery_man.*
import okhttp3.*
import okio.IOException

class ActivityListCarsInfo : AppCompatActivity() {
    lateinit var m_event_id : String
    var m_nDelivery_index : Int = 0
    var m_nEvent_Index : Int = 0
    var m_nflag=0

    private lateinit var m_Adapter : AdapterListCarsInfo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_cars_info)
        m_event_id = intent.getStringExtra("id") as String
        m_nDelivery_index = intent.getIntExtra("delivery_index",0)
        m_nEvent_Index = intent.getIntExtra("event_index",0)
        m_nflag = intent.getIntExtra("flag",0)


        btn_Ok.setOnClickListener {

            var car_id ="0"
            var car_num ="미정"
            val checkedItem = listView.checkedItemPositions

            if( list_cars_info.size== 0 ) {   finish()   }

            if(m_nflag == 0) {
                CListCollector.list_event[m_nEvent_Index].List_delivery_man[m_nDelivery_index].car_id =  car_id
                CListCollector.list_event[m_nEvent_Index].List_delivery_man[m_nDelivery_index].car_num = car_num
            }

            (0 until m_Adapter.count)
                .filter { checkedItem.get(it) }
                .forEach {
                    car_id = list_cars_info[it].id
                    car_num = list_cars_info[it].num_str
                    if(m_nflag == 0) {
                        CListCollector.list_event[m_nEvent_Index].List_delivery_man[m_nDelivery_index].car_id  = list_cars_info[it].id
                        CListCollector.list_event[m_nEvent_Index].List_delivery_man[m_nDelivery_index].car_num = list_cars_info[it].num_str
                    }
                }
            if(m_nflag == 0)
            {
                for (i in 0 until list_delivery_man.size)
                {
                    if (list_event[m_nEvent_Index].List_delivery_man[m_nDelivery_index].id == list_delivery_man[i].id) {
                        for (inx in 0 until list_delivery_man[i].List_Event.size) {
                            if (list_event[m_nEvent_Index].id == list_delivery_man[i].List_Event[inx].id) {
                                list_delivery_man[i].List_Event[inx].car_id = car_id
                                list_delivery_man[i].List_Event[inx].car_num = car_num
                            }
                        }
                    }
                }
                UpdateMatchingCarsInfo()

            }
            else
            {
                //배차 정보리스트에서 삭제한다.
                for(j in 0 until  list_cars_info.size)
                {
                    if(list_cars_info[j].id == car_id)
                    {
                        list_cars_info.removeAt(j)
                        break
                    }
                }
                //이벤트에 배차된 직원들 중 삭제되는 차량이 있으면 지워주고 미정으로 바꿔준다.
                for(i in 0 until list_event.size)
                {
                    for(inx in 0 until  list_event[i].List_delivery_man.size)
                    {
                        if(list_event[i].List_delivery_man[inx].id == car_id)
                        {
                            list_event[i].List_delivery_man[inx].id = "0"
                            list_event[i].List_delivery_man[inx].car_num = "미정"
                        }
                    }
                }

                DeleteCarsInfo(car_id)
                
            }
            finish()

        }

        this.btn_Cancel.setOnClickListener {
            finish()
        }

        m_Adapter =  AdapterListCarsInfo(this@ActivityListCarsInfo, CListCollector.list_cars_info)
        listView.adapter = m_Adapter
        //리스트뷰의 선택모드를 다중선택모드로 설정
        listView.choiceMode = ListView.CHOICE_MODE_SINGLE
    }

    fun UpdateMatchingCarsInfo() {
        val url = "$AWS_DOMAIN/hans/update_matching_cars_info"
        val client : OkHttpClient = OkHttpClient()

        val body: RequestBody = FormBody.Builder().add("car_id",list_event[m_nEvent_Index].List_delivery_man[m_nDelivery_index].car_id).add("event_id",m_event_id).add("delivery_id",list_event[m_nEvent_Index].List_delivery_man[m_nDelivery_index].id).build()
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

    fun DeleteCarsInfo(a_id : String) {
        val url = "${CListCollector.AWS_DOMAIN}/pony/delete_carsinfo"
        val client: OkHttpClient = OkHttpClient()
        Log.d("로그", "Delete car_id: ${a_id}")
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