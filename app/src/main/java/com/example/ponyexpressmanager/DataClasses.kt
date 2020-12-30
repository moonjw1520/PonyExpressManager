package com.example.ponyexpressmanager

import android.util.Log
import com.example.ponyexpressmanager.CListCollector.list_Matching
import com.example.ponyexpressmanager.CListCollector.list_event
import okhttp3.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList

data class D_CarsInfo(var id:String, var num_str: String)

data class D_PMenu(var id:String, var kind:String, var kind_str:String, var name: String, var desc: String)

data class D_SMenu(var id:String, var kind:String, var kind_str:String, var name: String )

data class D_DeliveryPerson(var id:String, var name: String, var mdn: String, var car_id:String, var car_num:String)

class CEvent(var id: String,  var signature: String, var signature_datetime: String, var send_sms: String, var signature_send_sms: String, var send_sms_datetime: String, var user_name: String, var mdn: String, var file1: String, var event_date : String, var event_time : String, var title: String,  var place : String, var user_count : String, var event: String)
{
    var num =0;
    var List_delivery_man = arrayListOf<D_DeliveryPerson>()
    var List_Menu = arrayListOf<D_SMenu>()
}

data class D_Event(var id: String,  var signature: String, var signature_datetime: String, var send_sms: String, var signature_send_sms: String, var send_sms_datetime: String, var user_name: String, var mdn: String, var file1: String, var event_date : String, var event_time : String, var title: String,  var place : String, var user_count : String, var event: String, var car_id: String, var car_num: String)

class CDeliveryPerson(var id:String, var name:String, var mdn:String)
{
    var List_Event = arrayListOf<D_Event>()
}


data class D_MatchingInfo(var id:String, var event_id: String,var car_id: String, var delivery_id: String)
data class D_MatchingMenuInfo(var id:String, var event_id: String,var menu_id: String)


object CListCollector
{
    val TAG = "로그"
    var ChannelId=""
    val AWS_DOMAIN="http://mnk082.ga:4500"
    var MY_nickName = ""
    var MY_kakaoId = ""
    var MY_token = ""
    var NOW_DATE=""
    var list_delivery_man: ArrayList<CDeliveryPerson> = ArrayList()
    var list_event: ArrayList<CEvent> = ArrayList()
    var list_Matching: ArrayList<D_MatchingInfo> = ArrayList()
    var list_MatchingMenu: ArrayList<D_MatchingMenuInfo> = ArrayList()
    var list_food_menu: ArrayList<D_PMenu> = ArrayList()
    var list_cars_info: ArrayList<D_CarsInfo> = ArrayList()

}

object  CGet {
    fun GetKindStr(a_nFlag: Int): String {
        var KindStr: String
        when (a_nFlag) {
            1 -> KindStr = "당일 "
            2 -> KindStr = "1박 2일 "
            3 -> KindStr = "2박 3일 "
            4 -> KindStr = "3박 4일 "
            else -> KindStr = "기타 "
        }
        return KindStr
    }
}

object  CPushHttp
{
    fun InsertPushData()
    {
        val url = CListCollector.AWS_DOMAIN +"/hans/insert_push_info"
        val client : OkHttpClient = OkHttpClient()

        if(CListCollector.MY_kakaoId == "" || CListCollector.MY_nickName == "" || CListCollector.MY_kakaoId == "null"  || CListCollector.MY_nickName == "null") { return }

        val body: RequestBody = FormBody.Builder().add("kakao_id", CListCollector.MY_kakaoId).add("token",
            CListCollector.MY_token
        ).add("nick_name", CListCollector.MY_nickName).build()
        val request = Request.Builder().url(url).post(body).build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                //에러 메세지 출력
                Log.d("로그", e.toString())
            }
            override fun onResponse(call: Call, response: Response) {
                Log.d("로그", "${response.body.toString()}")
                //Update Main UI

            }
        })
    }


    fun SendPushInsert()
    {
        val url = CListCollector.AWS_DOMAIN +"/hans/event/send_push_insert"
        val client : OkHttpClient = OkHttpClient()

        val body: RequestBody = FormBody.Builder().add("kakao_id","v123").build()
        val request = Request.Builder().url(url).post(body).build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                //에러 메세지 출력
                Log.d("로그", e.toString())
            }
            override fun onResponse(call: Call, response: Response) {
                Log.d("로그", "${response.body.toString()}")
                //Update Main UI
                //runOnUiThread {
                //Toast.makeText(applicationContext, "푸시 보냈습니다.", Toast.LENGTH_SHORT).show()
                //finish()
                //}
            }
        })
    }

    fun updatePushInfo(a_token : String)
    {
        CListCollector.MY_token = a_token

        val url = CListCollector.AWS_DOMAIN +"/hans/update_push_info"
        val client : OkHttpClient = OkHttpClient()

        if(CListCollector.MY_kakaoId == "" || CListCollector.MY_nickName == "" || CListCollector.MY_kakaoId == "null"  || CListCollector.MY_nickName == "null") { return }

        val body: RequestBody = FormBody.Builder().add("kakao_id", CListCollector.MY_kakaoId).add("token", CListCollector.MY_token).build()
        val request = Request.Builder().url(url).post(body).build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                //에러 메세지 출력
                Log.d("로그", e.toString())
            }
            override fun onResponse(call: Call, response: Response) {
                Log.d("로그", "${response.body.toString()}")
                //Update Main UI

            }
        })

    }





}





object CRefreshDatabase
{
    fun ProcRefresh()
    {
        getEventList()
        getDeliveryList()
        getMatchingList()
        EventListSync()
        DeliveryManListSync()
    }



    fun getNow(a_nFlag : Int) : String{
        val calendar: Calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val mon = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val min = calendar.get(Calendar.MINUTE)
        val sec = calendar.get(Calendar.SECOND)

        var month = mon + 1
        var now =""

        when(a_nFlag)
        {
            1-> now=year.toString() + "-" + month + "-" + day + " " + hour + ":" + min + ":" + sec
            2-> now=year.toString() + "-" + month + "-" + day
            3-> now=hour.toString()+":"+min.toString()+":"+sec.toString()
        }

        return now
    }


    fun synEventJustNum(a_nInx : Int)
    {
        var i : Int
        for(  i  in a_nInx  until  list_event.size)
        {
            list_event[i].num = i+1
        }
    }

    fun DeleteEventInEvent(a_nInx : Int)
    {
        CListCollector.list_event.removeAt(a_nInx)
    }

    fun DeleteEventInDeliveryMan(a_nInx : Int)
    {
        for(i in 0 until  CListCollector.list_delivery_man.size)
        {
            for(inx in 0 until CListCollector.list_delivery_man[i].List_Event.size)
            {
                if(CListCollector.list_delivery_man[i].List_Event[inx].id == CListCollector.list_event[a_nInx].id)
                {
                    CListCollector.list_delivery_man[i].List_Event.removeAt(inx)
                }
            }
        }
    }


    fun SynEventInDeliveryMan(a_nInx : Int, a_nFlag : Int)
    {
        for(i in 0 until  CListCollector.list_delivery_man.size)
        {
            for(inx in 0 until CListCollector.list_delivery_man[i].List_Event.size)
            {
                if(CListCollector.list_delivery_man[i].List_Event[inx].id == CListCollector.list_event[a_nInx].id)
                {
                    when(a_nFlag){
                        1 -> CListCollector.list_delivery_man[i].List_Event[inx].event_date = CListCollector.list_event[a_nInx].event_date
                        2 -> CListCollector.list_delivery_man[i].List_Event[inx].event_time = CListCollector.list_event[a_nInx].event_time
                        3 ->{ CListCollector.list_delivery_man[i].List_Event[inx].signature = CListCollector.list_event[a_nInx].signature
                            CListCollector.list_delivery_man[i].List_Event[inx].signature_datetime = CListCollector.list_event[a_nInx].signature_datetime
                        }
                        4 ->{ CListCollector.list_delivery_man[i].List_Event[inx].send_sms  = CListCollector.list_event[a_nInx].send_sms
                            CListCollector.list_delivery_man[i].List_Event[inx].send_sms_datetime = CListCollector.list_event[a_nInx].send_sms_datetime
                            CListCollector.list_delivery_man[i].List_Event[inx].signature_send_sms = CListCollector.list_event[a_nInx].signature_send_sms
                        }
                    }

                }
            }
        }
    }

    fun getEventList()
    {
        val url = "http://mnk082.ga:5000/hans/events"
        val client = OkHttpClient()
        val request = Request.Builder().url(url).build()

        CListCollector.list_event.clear()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                //에러 메세지 출력
                Log.d("로그", e.toString())
            }

            override fun onResponse(call: Call, response: Response) {
                val str_response1 = response.body!!.string()
                var json_array: JSONArray = JSONArray(str_response1)
                var size: Int = json_array.length()

                //list_event = ArrayList()

                for (i in 0..size - 1) {
                    var json_objdetail: JSONObject = json_array.getJSONObject(i)
                    var event: CEvent = CEvent(
                        json_objdetail.getString("id"),
                        json_objdetail.getString("signature"),
                        json_objdetail.getString("signature_datetime"),
                        json_objdetail.getString("send_sms"),
                        json_objdetail.getString("signature_send_sms"),
                        json_objdetail.getString("send_sms_datetime"),
                        json_objdetail.getString("user_name"),
                        json_objdetail.getString("mdn"),
                        json_objdetail.getString("file1"),
                        json_objdetail.getString("event_date"),
                        json_objdetail.getString("event_time"),
                        json_objdetail.getString("title"),
                        json_objdetail.getString("place"),
                        json_objdetail.getString("user_count"),
                        json_objdetail.getString("event")
                    )
                    CListCollector.list_event.add(event)
                }
                //Update Main UI
                //runOnUiThread {
                //getDeliveryList() //배달기사 가져오고
                //    }
            }
        })

    }


    fun getDeliveryList()
    {
        val url = "http://mnk082.ga:5000/hans/delivery_list"
        val client = OkHttpClient()
        val request = Request.Builder().url(url).build()

        CListCollector.list_delivery_man.clear()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                //에러 메세지 출력
                Log.d("로그", e.toString())
            }

            override fun onResponse(call: Call, response: Response) {
                val str_response1 = response.body!!.string()
                var json_array: JSONArray = JSONArray(str_response1)
                var size: Int = json_array.length()

                //CListCollector.list_delivery_man = ArrayList()
                for (i in 0..size - 1) {
                    var json_objdetail: JSONObject = json_array.getJSONObject(i)
                    var person: CDeliveryPerson = CDeliveryPerson(
                        json_objdetail.getString("id"),
                        json_objdetail.getString("name"),
                        json_objdetail.getString("mdn")
                    )
                    CListCollector.list_delivery_man.add(person)
                }
                //Update Main UI
//                runOnUiThread {
//                    getMatchingList() //매칭정보 가져온다
//
//                }
            }
        })

    }



    fun getMatchingList()
    {
        val url = "http://mnk082.ga:5000/hans/matching"

        val client = OkHttpClient()
        val request = Request.Builder().url(url).build()

        CListCollector.list_Matching.clear()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                //에러 메세지 출력
                Log.d("로그", e.toString())
            }


            override fun onResponse(call: Call, response: Response) {
                val str_response1 = response.body!!.string()
                var json_array: JSONArray = JSONArray(str_response1)
                var size: Int = json_array.length()

                for (i in 0..size - 1) {
                    var json_objdetail: JSONObject = json_array.getJSONObject(i)
                    var matching_info: D_MatchingInfo = D_MatchingInfo(
                        json_objdetail.getString("id"),
                        json_objdetail.getString("event_id"),
                        json_objdetail.getString("car_id"),
                        json_objdetail.getString("delivery_id")
                    )
                    CListCollector.list_Matching.add(matching_info)
                }
                //Update Main UI
//                runOnUiThread {
//                    EventListSync()
//                    DeliveryManListSync()
//
//                }
            }
        })
    }



    fun EventListSync()
    {
        for(i in 0 until  CListCollector.list_event.size)
        {
            for(inx in 0 until CListCollector.list_Matching.size)
            {
                if(CListCollector.list_event[i].id == CListCollector.list_Matching[inx].event_id)
                {
                    for(index in 0 until CListCollector.list_delivery_man.size )
                    {
                        if(CListCollector.list_Matching[inx].delivery_id == CListCollector.list_delivery_man[index].id)
                        {
                            CListCollector.list_event[i].List_delivery_man.add(D_DeliveryPerson(CListCollector.list_delivery_man[index].id, CListCollector.list_delivery_man[index].name, CListCollector.list_delivery_man[index].mdn, "0","미정") )
                        }
                    }
                }
            }
        }
    }


    fun DeliveryManListSync()
    {
        for(i in 0 until  CListCollector.list_delivery_man.size)
        {
            for(inx in 0 until list_Matching.size)
            {
                if(CListCollector.list_delivery_man[i].id == list_Matching[inx].delivery_id)
                {
                    var car_id = "0"
                    var car_num = "미정"
                    //carsinfo

                    for(j in 0 until CListCollector.list_cars_info.size)
                    {
                        if(list_Matching[inx].car_id == CListCollector.list_cars_info[j].id)
                        {
                            car_id = CListCollector.list_cars_info[j].id
                            car_num = CListCollector.list_cars_info[j].num_str
                        }
                    }


                    for(index in 0 until list_event.size )
                    {
                        if(list_Matching[inx].event_id == list_event[index].id)
                        {
                            CListCollector.list_delivery_man[i].List_Event.add(
                                D_Event(CListCollector.list_event[index].id,
                                    CListCollector.list_event[index].signature,
                                    CListCollector.list_event[index].signature_datetime,
                                    CListCollector.list_event[index].send_sms,
                                    CListCollector.list_event[index].signature_send_sms,
                                    CListCollector.list_event[index].send_sms_datetime,
                                    CListCollector.list_event[index].user_name,
                                    CListCollector.list_event[index].mdn,
                                    CListCollector.list_event[index].file1,
                                    CListCollector.list_event[index].event_date,
                                    CListCollector.list_event[index].event_time,
                                    CListCollector.list_event[index].title,
                                    CListCollector.list_event[index].place,
                                    CListCollector.list_event[index].user_count,
                                    CListCollector.list_event[index].event,
                                    car_id,
                                    car_num))
                            break
                        }
                    }
                }
            }
        }
    }



}