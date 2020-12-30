package com.example.ponyexpressmanager

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ponyexpressmanager.CGet.GetKindStr
import com.example.ponyexpressmanager.CListCollector.AWS_DOMAIN
import com.example.ponyexpressmanager.CListCollector.MY_kakaoId
import com.example.ponyexpressmanager.CListCollector.MY_nickName
import com.example.ponyexpressmanager.CListCollector.MY_token
import com.example.ponyexpressmanager.CListCollector.NOW_DATE
import com.example.ponyexpressmanager.CListCollector.TAG
import com.example.ponyexpressmanager.CListCollector.list_Matching
import com.example.ponyexpressmanager.CListCollector.list_MatchingMenu
import com.example.ponyexpressmanager.CListCollector.list_cars_info
import com.example.ponyexpressmanager.CListCollector.list_delivery_man
import com.example.ponyexpressmanager.CListCollector.list_event
import com.example.ponyexpressmanager.CListCollector.list_food_menu
import com.example.ponyexpressmanager.CRefreshDatabase.getNow
import com.google.android.material.snackbar.Snackbar
import com.kakao.sdk.user.UserApiClient
import kotlinx.android.synthetic.main.activity_event_main.*
import kotlinx.android.synthetic.main.toolbar.*
import okhttp3.*
import okio.IOException
import org.json.JSONArray
import org.json.JSONObject

class ActivityEventMain : AppCompatActivity(), EventRecyclerviewInterface, StateRecyclerviewInterface
{
    //var m_PushManager = CPushManager()

    var m_listStatEventDate: ArrayList<String> = ArrayList()

    lateinit  var m_RecyclerEventAdt : RecyclerEventAdapter
    lateinit  var m_RecyclerStateEventAdt : RecyclerStateAdapter
    var m_nPosition=0
    var m_nStatePosition=0
    var m_PushManager = CPushManager()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event_main)

        setSupportActionBar(tool_bar)
        supportActionBar!!.setDisplayShowTitleEnabled(false)    // 타이틀 안보이게 하기


        //push 채널 만들어주기
        m_PushManager.createNotificationChannel(this, NotificationManagerCompat.IMPORTANCE_DEFAULT,
            false, getString(R.string.app_name), "App notification channel") // 1


        setSupportActionBar(tool_bar)
        supportActionBar!!.setDisplayShowTitleEnabled(false)    // 타이틀 안보이게 하기

        //오늘 날짜 가져오기
        NOW_DATE = getNow(2)

        //내 닉네임 가져오기
        UserApiClient.instance.me { user, error ->
            MY_kakaoId = "${user?.id}"
            MY_nickName = user?.kakaoAccount?.profile?.nickname.toString()
            Log.d(TAG,"kakaoid : ${MY_kakaoId}")
            m_PushManager.linitToken()
        }

        //m_PushManager.linitToken()

        //push정보값 DB에 넣기
        //InsertPushData()


        list_event.clear()
        list_delivery_man.clear()
        list_Matching.clear()
        list_MatchingMenu.clear()
        list_food_menu.clear()
        list_cars_info.clear()

        getEventList()


    }

    // 3.툴바 메뉴 버튼을 설정
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)       // main_menu 메뉴를 toolbar 메뉴 버튼으로 설정
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // 클릭된 메뉴 아이템의 아이디 마다 when 구절로 클릭시 동작을 설정한다.
        when(item!!.itemId){
            android.R.id.home->{ // 메뉴 버튼
                Snackbar.make(tool_bar,"Menu pressed", Snackbar.LENGTH_SHORT).show()
            }
            R.id.menu_create_event->{ // 새로운 이벤트 만들기
                var intent = Intent(this,ActivityCreateEvent::class.java)
                startActivity(intent)
                //Snackbar.make(tool_bar,"Search menu pressed",Snackbar.LENGTH_SHORT).show()
            }

            R.id.menu_admin->{ // 관리 페이지
                var intent = Intent(this,ActivityAdmin::class.java)
                startActivity(intent)
            }

            R.id.menu_logout->{ // 로그아웃 버튼
                //Snackbar.make(tool_bar,"Account menu pressed",Snackbar.LENGTH_SHORT).show()
                UserApiClient.instance.logout { error ->
                    if (error != null) {
                        Toast.makeText(this, "로그아웃 실패 $error", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "로그아웃 성공", Toast.LENGTH_SHORT).show()
                    }
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
                }
            }
            R.id.menu_ulink->{ // 탈퇴버튼
                //Snackbar.make(tool_bar,"Logout menu pressed",Snackbar.LENGTH_SHORT).show()
                UserApiClient.instance.unlink { error ->
                    if (error != null) {
                        Toast.makeText(this, "회원 탈퇴 실패 $error", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "회원 탈퇴 성공", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
                    }
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }


    fun setStatEventList()
    {
        m_listStatEventDate.clear()
        for(i in 0 until list_event.size)
        {
            var flag =0
            for(inx in 0 until m_listStatEventDate.size)
            {
                if(m_listStatEventDate[inx] == list_event[i].event_date)
                {
                    flag =1
                }

            }
            if(flag == 0)
            {
                m_listStatEventDate.add(list_event[i].event_date)
            }
        }
    }




    //MyRecyclerviewInterface
    //이벤트 상세페이지 불러오기
    override fun onItemClicked(position: Int, a_evetData : CEvent) {
        Log.d("로그", "MainActivity Clicked")

        m_nPosition=position
        val intent = Intent(this, ActivityEventDetail::class.java)
        intent.putExtra("id",a_evetData.id)
        intent.putExtra("signature",a_evetData.signature)
        intent.putExtra("signature_datetime",a_evetData.signature_datetime)
        intent.putExtra("send_sms",a_evetData.send_sms)
        intent.putExtra("signature_send_sms",a_evetData.signature_send_sms)
        intent.putExtra("send_sms_datetime",a_evetData.send_sms_datetime)
        intent.putExtra("user_name",a_evetData.user_name)
        intent.putExtra("mdn",a_evetData.mdn)
        intent.putExtra("file1",a_evetData.file1)
        intent.putExtra("event_date",a_evetData.event_date)
        intent.putExtra("event_time",a_evetData.event_time)
        intent.putExtra("title",a_evetData.title)
        intent.putExtra("place",a_evetData.place)
        intent.putExtra("user_count",a_evetData.user_count)
        intent.putExtra("event",a_evetData.event)

        startActivity(intent)
    }

    override fun onStateEventClicked(position: Int, aEventData: String)
    {
        Log.d("로그","EvnetState 상세 페이지 불러오기")

    }

    fun StateEventClicked(position: Int, aEventData: String)
    {
        m_nStatePosition=position

        val intent = Intent(this, ActivityEventDetailDay::class.java)
        intent.putExtra("eventDate",aEventData)
        startActivity(intent)

        Log.d("로그","EvnetState 상세 페이지 불러오기 position: ${position} ${aEventData} ")

    }





    //새로고침
    override fun onRestart() {
        super.onRestart()

        //setValueInResource()
        Log.d("로그","MainEventActivity onRestart.. position :${m_nPosition}")
        recyclerview_event_main.apply {
            //리사이클러뷰 설정
            layoutManager = LinearLayoutManager(this@ActivityEventMain, LinearLayoutManager.HORIZONTAL, false)
            // LinearLayoutManager(this@MainEventActivity, LinearLayoutManager.HORIZONTAL, false)
            //어답터 장착
            adapter = m_RecyclerEventAdt
            var tmp = layoutManager as LinearLayoutManager
            tmp.scrollToPosition(m_nPosition)
            //tmp.scrollToPositionWithOffset(m_nPosition,0)
        }

        setStatEventList()
        //var RecyclerStateEventAdt = RecyclerStateAdapter(this, m_listStatEventDate, this@ActivityEventMain)
        m_RecyclerStateEventAdt =  RecyclerStateAdapter(this, m_listStatEventDate, this@ActivityEventMain)
        recyclerview_state_event.apply {
            //리사이클러뷰 설정
            layoutManager = LinearLayoutManager(this@ActivityEventMain, LinearLayoutManager.HORIZONTAL, false)
            //어답터 장착
            adapter = m_RecyclerStateEventAdt

            var tmp = layoutManager as LinearLayoutManager
            tmp.scrollToPosition(m_nStatePosition)
        }

}


    //페이지 처음에 열었을때 이벤트 정보값을 가져온다.
    fun getEventList()
    {
        //val url = "http://mnk082.ga:5000/hans/events"
        val url = AWS_DOMAIN+"/hans/events"

        val client = OkHttpClient()
        val request = Request.Builder().url(url).build()

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
                    event.num = i+1
                    list_event.add(event)
                }
                //Update Main UI
                runOnUiThread {
                    getDeliveryList() //배달기사 가져오고
                }
            }
        })

    }


    fun getDeliveryList()
    {
        val url = AWS_DOMAIN+"/hans/delivery_list"
        val client = OkHttpClient()
        val request = Request.Builder().url(url).build()
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
                runOnUiThread {
                    getCarsInfoList() //차량 정보를 가져온다

                }
            }
        })

    }


    fun getCarsInfoList()
    {
        val url = AWS_DOMAIN+"/hans/carsinfo"
        val client = OkHttpClient()
        val request = Request.Builder().url(url).build()
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
                    var carsInfo: D_CarsInfo = D_CarsInfo(
                        json_objdetail.getString("id"),
                        json_objdetail.getString("num")
                    )
                    CListCollector.list_cars_info.add(carsInfo)
                }
                //Update Main UI
                runOnUiThread {
                    getMenuList() //메뉴 정보를 가져온다

                }
            }
        })

    }





    fun getMenuList()
    {
        val url = AWS_DOMAIN+"/hans/menu"
        val client = OkHttpClient()
        val request = Request.Builder().url(url).build()
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
                    var menu: D_PMenu = D_PMenu(
                        json_objdetail.getString("id"),
                        json_objdetail.getString("kind"),
                        (""),
                        json_objdetail.getString("name"),
                        json_objdetail.getString("description")
                    )
                    menu.kind_str = GetKindStr(menu.kind.toInt())
                    CListCollector.list_food_menu.add(menu)
                }
                //Update Main UI
                runOnUiThread {
                    getMatchingMenu() //메뉴매칭정보 가져온다

                }
            }
        })

    }



    fun getMatchingMenu()
    {
        val url = AWS_DOMAIN+"/hans/matching_menu"
        val client = OkHttpClient()
        val request = Request.Builder().url(url).build()
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
                    var menu: D_MatchingMenuInfo = D_MatchingMenuInfo(
                        json_objdetail.getString("id"),
                        json_objdetail.getString("event_id"),
                        json_objdetail.getString("menu_id")

                    )
                    CListCollector.list_MatchingMenu.add(menu)
                }
                //Update Main UI
                runOnUiThread {
                    getMatchingList() //매칭정보 가져온다

                }
            }
        })

    }





    fun getMatchingList()
    {
        val url = AWS_DOMAIN+"/hans/matching"

        val client = OkHttpClient()
        val request = Request.Builder().url(url).build()

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
                    list_Matching.add(matching_info)
                }
                //Update Main UI
                runOnUiThread {
                    DeliveryManListSync()
                    EventListSync()
                    EventMenuListSync()
                    //setValueInResource()
                    //어답터 인스턴스 생성
                    //var RecyclerAdt: RecyclerAdapter = RecyclerAdapter(list_event, this@MainEventActivity)
                    m_RecyclerEventAdt= RecyclerEventAdapter(list_event, this@ActivityEventMain )
                    recyclerview_event_main.apply {
                        //리사이클러뷰 설정
                        layoutManager = LinearLayoutManager(this@ActivityEventMain, LinearLayoutManager.HORIZONTAL, false)
                        //어답터 장착
                        adapter = m_RecyclerEventAdt
                    }

                    setStatEventList()
                    m_RecyclerStateEventAdt = RecyclerStateAdapter(this@ActivityEventMain, m_listStatEventDate, this@ActivityEventMain)
                    recyclerview_state_event.apply {
                        //리사이클러뷰 설정
                        layoutManager = LinearLayoutManager(this@ActivityEventMain, LinearLayoutManager.HORIZONTAL, false)
                        //어답터 장착
                        adapter = m_RecyclerStateEventAdt
                    }

                }
            }
        })
    }





    fun EventMenuListSync()
    {
        for(i in 0 until list_event.size)
        {
            for(inx in 0 until  list_MatchingMenu.size)
            {
                if(list_event[i].id == list_MatchingMenu[inx].event_id)
                {
                    for(index in 0 until list_food_menu.size)
                    {
                        if(list_MatchingMenu[inx].menu_id == list_food_menu[index].id)
                        {
                            list_event[i].List_Menu.add(D_SMenu(list_food_menu[index].id,list_food_menu[index].kind,list_food_menu[index].kind_str,list_food_menu[index].name) )
                        }

                    }
                }
            }
        }
    }


    fun EventListSync()
    {
        for(i in 0 until  list_event.size)
        {
            for(inx in 0 until list_Matching.size)
            {
                if(list_event[i].id == list_Matching[inx].event_id)
                {
                    for(index in 0 until list_delivery_man.size )
                    {
                        if(list_Matching[inx].delivery_id == list_delivery_man[index].id)
                        {
                            var car_id = "0"
                            var car_num ="미정"

                            for(j in 0 until list_delivery_man[index].List_Event.size)
                            {
                                if(list_event[i].id == list_delivery_man[index].List_Event[j].id)
                                {
                                    car_id = list_delivery_man[index].List_Event[j].car_id
                                    car_num = list_delivery_man[index].List_Event[j].car_num
                                    break
                                }
                            }

                            list_event[i].List_delivery_man.add(D_DeliveryPerson(list_delivery_man[index].id,list_delivery_man[index].name,list_delivery_man[index].mdn,car_id,car_num) )
                        }
                    }
                }
            }
        }
    }


    fun DeliveryManListSync()
    {
        for(i in 0 until  list_delivery_man.size)
        {
            for(inx in 0 until list_Matching.size)
            {
                if(list_delivery_man[i].id == list_Matching[inx].delivery_id)
                {
                    var car_id = "0"
                    var car_num = "미정"
                    //carsinfo

                    for(j in 0 until list_cars_info.size)
                    {
                        if(list_Matching[inx].car_id == list_cars_info[j].id)
                        {
                            car_id = list_cars_info[j].id
                            car_num = list_cars_info[j].num_str
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







    override fun onResume() {
        super.onResume()

//        var myDialog = CListDialogDelivery(this)
//        myDialog.show()
//        val params: ViewGroup.LayoutParams? = myDialog?.window?.attributes
//        val deviceWidth = size.x
//        params?.width = (deviceWidth * 0.5).toInt()
//        params?.height = (deviceWidth * 0.5).toInt()
//        myDialog?.window?.attributes = params as WindowManager.LayoutParams
    }


//        UserApiClient.instance.me { user, error ->
//            id.text = "회원번호: ${user?.id}"
//            nickname.text = "닉네임: ${user?.kakaoAccount?.profile?.nickname}"
//            profileimage_url.text = "프로필 링크: ${user?.kakaoAccount?.profile?.profileImageUrl}"
//            thumbnailimage_url.text = "썸네일 링크: ${user?.kakaoAccount?.profile?.thumbnailImageUrl}"
//        }

    //kakao_logout_button.setOnClickListener {
    fun funkakao_logout (v: View) {
        UserApiClient.instance.logout { error ->
            if (error != null) {
                Toast.makeText(this, "로그아웃 실패 $error", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "로그아웃 성공", Toast.LENGTH_SHORT).show()
            }
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
        }
    }
    //}

    //        kakao_unlink_button.setOnClickListener {
    fun kakao_unlink_button(v: View) {
        UserApiClient.instance.unlink { error ->
            if (error != null) {
                Toast.makeText(this, "회원 탈퇴 실패 $error", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "회원 탈퇴 성공", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
            }
        }
    }
    //}

    //}
//
//
//    fun SnedMeMsg(v: View) {
//        // 피드 메시지 보내기
////        LinkClient.instance.defaultTemplate(this, defaultText) { linkResult, error ->
////            if (error != null) {
////                Log.e(TAG, "카카오링크 보내기 실패", error)
////            }
////            else if (linkResult != null) {
////                Log.d(TAG, "카카오링크 보내기 성공 ${linkResult.intent}")
////                startActivity(linkResult.intent)
////
////                // 카카오링크 보내기에 성공했지만 아래 경고 메시지가 존재할 경우 일부 컨텐츠가 정상 동작하지 않을 수 있습니다.
////                Log.w(TAG, "Warning Msg: ${linkResult.warningMsg}")
////                Log.w(TAG, "Argument Msg: ${linkResult.argumentMsg}")
////            }
////        }
//
//        val nextIntent = Intent(this, SendEventMsgView::class.java)
//        startActivity(nextIntent)
//    }
////
//
//    fun SnedMsg(v: View)
//    {
//        TalkApiClient.instance.friends { friends, error ->
//            if (error != null) {
//                Log.e(TAG, "카카오톡 친구 목록 가져오기 실패", error)
//            }
//            else {
//                Log.d(TAG, "카카오톡 친구 목록 가져오기 성공 \n${friends!!.elements.joinToString("\n")}")
//
//                if (friends.elements.isEmpty()) {
//                    Log.e(TAG, "메시지 보낼 친구가 하나도 없어요 ㅠㅠ")
//                }
//                else {
//
//                    // 서비스에 상황에 맞게 메시지 보낼 친구의 UUID를 가져오세요.
//                    // 이 샘플에서는 친구 목록을 화면에 보여주고 체크박스로 선택된 친구들의 UUID 를 수집하도록 구현했습니다.
////                    FriendsActivity.startForResult(
////                        context,
////                        friends.elements.map { PickerItem(it.uuid, it.profileNickname, it.profileThumbnailImage) }
////                    ) { selectedItems ->
////                        if (selectedItems.isEmpty()) return@startForResult
////                        Log.d(TAG, "선택된 친구:\n${selectedItems.joinToString("\n")}")
//
//
//                    // 메시지 보낼 친구의 UUID 목록
//                    //val receiverUuids = selectedItems
//                    var receiverUuids = mutableListOf<String>()
//
//
//                    for( i in 0 until friends.elements.size)
//                    {
//                        friends.elements.map {
//
//                        }
//                        var friend1 =  friends.elements[i];
//
//                        receiverUuids.add(friend1.uuid)
//                    }
//
//                    // Feed 메시지
//                    val template = defaultFeed
//
//                    // 메시지 보내기
//                    TalkApiClient.instance.sendDefaultMessage(receiverUuids, template) { result, error ->
//                        if (error != null) {
//                            Log.e(TAG, "메시지 보내기 실패", error)
//                        }
//                        else if (result != null) {
//                            Log.i(TAG, "메시지 보내기 성공 ${result.successfulReceiverUuids}")
//
//                            if (result.failureInfos != null) {
//                                Log.d(TAG, "메시지 보내기에 일부 성공했으나, 일부 대상에게는 실패 \n${result.failureInfos}")
//                            }
//                        }
//                    }
//                }
//            }
//        }
//    }



//    fun InsertPushData()
//    {
//        val url = AWS_DOMAIN+"/hans/insert_push_user"
//        val client : OkHttpClient = OkHttpClient()
//
//        val body: RequestBody = FormBody.Builder().add("kakao_id",MY_kakaoId).add("token",m_token).add("nick_name",MY_nickName).build()
//        val request = Request.Builder().url(url).post(body).build()
//        client.newCall(request).enqueue(object : Callback {
//            override fun onFailure(call: Call, e: IOException) {
//                //에러 메세지 출력
//                Log.d("로그", e.toString())
//            }
//            override fun onResponse(call: Call, response: Response) {
//                Log.d("로그", "${response.body.toString()}")
//                //Update Main UI
//                runOnUiThread {
//                    //Toast.makeText(applicationContext, "푸시 보냈습니다.", Toast.LENGTH_SHORT).show()
//                }
//            }
//        })
//    }



    fun testPush(v: View)
    {
        val url = AWS_DOMAIN+"/hans/event/send_push_insert"
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
                runOnUiThread {
                    Toast.makeText(applicationContext, "푸시 보냈습니다.", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    fun testPush1(v: View)
    {

        val url = CListCollector.AWS_DOMAIN +"/hans/update_push_info"
        val client : OkHttpClient = OkHttpClient()

        if(MY_kakaoId == "" || MY_nickName == "" || MY_kakaoId == "null"  || MY_nickName == "null") { return }

        val body: RequestBody = FormBody.Builder().add("kakao_id", CListCollector.MY_kakaoId).add("token",MY_token).build()
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