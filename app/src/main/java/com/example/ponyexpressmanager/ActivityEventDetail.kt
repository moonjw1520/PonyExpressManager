package com.example.ponyexpressmanager

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.telephony.SmsManager
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.ponyexpressmanager.CListCollector.AWS_DOMAIN
import com.example.ponyexpressmanager.CListCollector.MY_nickName
import com.example.ponyexpressmanager.CListCollector.TAG
import com.example.ponyexpressmanager.CListCollector.list_event
import com.example.ponyexpressmanager.CListCollector.list_food_menu
import com.example.ponyexpressmanager.CRefreshDatabase.DeleteEventInDeliveryMan
import com.example.ponyexpressmanager.CRefreshDatabase.DeleteEventInEvent
import com.example.ponyexpressmanager.CRefreshDatabase.SynEventInDeliveryMan
import com.example.ponyexpressmanager.CRefreshDatabase.getNow
import com.example.ponyexpressmanager.CRefreshDatabase.synEventJustNum
import com.kakao.sdk.auth.AuthApiClient.Companion.instance
import kotlinx.android.synthetic.main.activity_event_detail.*
import okhttp3.*
import okio.IOException
import java.util.*

class ActivityEventDetail : AppCompatActivity() {

    var m_modifyCotent: String? = null
    var m_nIndex_event: Int = 0
    lateinit var m_strid: String
    lateinit var m_event_date: String
    lateinit var m_SendNameList: String
    lateinit var m_SendMsg: String

    var m_list_view: ArrayList<LinearLayout> = ArrayList()
    var m_list_delivery_view: ArrayList<LinearLayout> = ArrayList()

    var m_list_txt_delivery_man = arrayListOf<TextView>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event_detail)

        setTextView()
        InitTextLinearLayout()
        InitTextDeliveryLinearLayout()
        findIndex()
        InitTextVew()
        printDeliveryMan()
        printMenu()


        //행사 내용 저장
        et_event_detail.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                Log.d("로그", p0.toString())
                //m_modifyCotent = p0.toString()
                list_event[m_nIndex_event].event = p0.toString()
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        })
        //행사 제목 저장
        et_event_title.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                Log.d("로그", p0.toString())
                list_event[m_nIndex_event].title = p0.toString()
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        })
        //행사 장소 저장
        et_event_place.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                Log.d("로그", p0.toString())
                list_event[m_nIndex_event].place = p0.toString()
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        })

        //행사 인원 저장
        et_user_count.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                Log.d("로그", p0.toString())
                list_event[m_nIndex_event].user_count = p0.toString()
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        })
    }

    //LMS 보낼지 확인 다이얼로그
    fun ShowDlgSendSms(v: View) {
        var builder = AlertDialog.Builder(this)
        makeSendNameList()
        builder.setTitle("HansFamily 배차 문자")
        builder.setMessage("${m_SendNameList} 에게 문자를 보내시겠습니까?")
        builder.setIcon(R.drawable.ponylogo)

        // 버튼 클릭시에 무슨 작업을 할 것인가!
        var listener = object : DialogInterface.OnClickListener {
            override fun onClick(p0: DialogInterface?, p1: Int) {
                when (p1) {
                    DialogInterface.BUTTON_POSITIVE -> {
                        SendLms()
                    }
                    // tv1.text = "BUTTON_POSITIVE"
                    DialogInterface.BUTTON_NEGATIVE -> {
                    }
                    // tv1.text = "BUTTON_NEGATIVE"
                }
            }
        }

        builder.setPositiveButton("문자 보내기", listener)
        builder.setNegativeButton("취소", listener)

        builder.show()
    }

    //LMS 확인 다이얼로그에 표시할 배달기사 분들 리스트를 임의로 만든다.
    fun makeSendNameList() {
        m_SendNameList = ""
        for (i in 0 until list_event[m_nIndex_event].List_delivery_man.size) {
            if (m_SendNameList == "") {
                m_SendNameList =
                    m_SendNameList + list_event[m_nIndex_event].List_delivery_man[i].name
            } else {
                m_SendNameList =
                    m_SendNameList + ", " + list_event[m_nIndex_event].List_delivery_man[i].name
            }

        }
        m_SendNameList = m_SendNameList + " ${list_event[m_nIndex_event].List_delivery_man.size}명 "
    }


    //그전 MainEventView 에서 받은 값을 세팅한다.
    fun setTextView() {
        val intent = getIntent()

        txt_signature.setText("@" + intent.getStringExtra("signature"))
        txt_signature_datetime.setText(intent.getStringExtra("signature_datetime"))

        m_strid = intent.getStringExtra("id") as String
        txt_name.setText(intent.getStringExtra("user_name"))
        txt_mdn.setText(intent.getStringExtra("mdn"))
        et_event_title.setText(intent.getStringExtra("title"))
        et_event_detail.setText(intent.getStringExtra("event"))
        m_event_date = intent.getStringExtra("event_date") as String
        txt_event_date.setText(m_event_date)
        txt_event_time.setText(intent.getStringExtra("event_time"))
        et_event_place.setText(intent.getStringExtra("place"))
        et_user_count.setText(intent.getStringExtra("user_count"))

        //sms 관련
        var sms_state = intent.getStringExtra("send_sms")


        var sms_sgin_datetime: String
        if (sms_state == "1") {
            txt_send_sms.setTextColor(
                ContextCompat.getColor(
                    applicationContext,
                    R.color.color_blue_sms
                )
            )
            txt_send_sms.setText(R.string.sms_sent)
            sms_sgin_datetime = "@" + intent.getStringExtra("signature_send_sms")
                .toString() + " " + intent.getStringExtra("send_sms_datetime").toString()
        } else {
            txt_send_sms.setTextColor(
                ContextCompat.getColor(
                    applicationContext,
                    R.color.color_red_default
                )
            )
            txt_send_sms.setText(R.string.sms_dont_send)
            sms_sgin_datetime = ""
        }


        txt_send_sms_sign_datetime.setText(sms_sgin_datetime)


        //사진 띄워주기
        SetFile(intent.getStringExtra("file1") as String)
    }



    //파일은 이미지로 띄워준다.
    fun SetFile(a_file_path: String) {
        //val url = "http://mnk082.ga:5000${a_file_path}"
        val url = AWS_DOMAIN + "${a_file_path}"
        if (a_file_path != null && a_file_path != "" && a_file_path != "null") {
            Log.d("로그", "--file1 내용 있음--")
            Glide
                .with(applicationContext)
                .load(url)
                .centerCrop()
                .placeholder(R.drawable.ponylogo2)
                .into(imageView1);
        } else {
            Log.d("로그", "file1 내용 없음")
        }
    }


    //---Text View 초기화 -----
    fun celearTextView() {
        for (i in 0 until m_list_txt_delivery_man.size) {
            m_list_txt_delivery_man[i].text = ""
        }
    }

    fun InitTextVew() {
//        m_list_txt_delivery_man.add(findViewById(R.id.txt_fix_delivery1))
//        m_list_txt_delivery_man.add(findViewById(R.id.txt_fix_delivery2))
//        m_list_txt_delivery_man.add(findViewById(R.id.txt_fix_delivery3))
//        m_list_txt_delivery_man.add(findViewById(R.id.txt_fix_delivery4))
//        m_list_txt_delivery_man.add(findViewById(R.id.txt_fix_delivery5))
    }

    fun InitTextDeliveryLinearLayout() {
        m_list_delivery_view.clear()
        m_list_delivery_view.add(this.findViewById<LinearLayout>(R.id.text_view_delivery_area0))
        m_list_delivery_view.add(this.findViewById<LinearLayout>(R.id.text_view_delivery_area1))
        m_list_delivery_view.add(this.findViewById<LinearLayout>(R.id.text_view_delivery_area2))
        m_list_delivery_view.add(this.findViewById<LinearLayout>(R.id.text_view_delivery_area3))
        m_list_delivery_view.add(this.findViewById<LinearLayout>(R.id.text_view_delivery_area4))
        m_list_delivery_view.add(this.findViewById<LinearLayout>(R.id.text_view_delivery_area5))
        m_list_delivery_view.add(this.findViewById<LinearLayout>(R.id.text_view_delivery_area6))
        m_list_delivery_view.add(this.findViewById<LinearLayout>(R.id.text_view_delivery_area7))
        m_list_delivery_view.add(this.findViewById<LinearLayout>(R.id.text_view_delivery_area8))
        m_list_delivery_view.add(this.findViewById<LinearLayout>(R.id.text_view_delivery_area9))
        m_list_delivery_view.add(this.findViewById<LinearLayout>(R.id.text_view_delivery_area10))

        //m_list_view.add(itemView.findViewById<LinearLayout>())
    }

    // 가장 최신화 된 배달기사로 화면에 나타내줌
    fun printDeliveryMan()
    {
        txt_fix_delivery1.text =""
        txt_fix_delivery_car_num.text = ""

        clearDelivery()
        for(i in 0 until  list_event[m_nIndex_event].List_delivery_man.size)
        {
            if(i == 0) {
                val vivew1 = this.findViewById<LinearLayout>(R.id.text_view_delivery_area0)
                vivew1.removeAllViews()

                txt_fix_delivery1.text = "${i+1}. " + list_event[m_nIndex_event].List_delivery_man[i].name
                txt_fix_delivery_car_num.text =  " " +list_event[m_nIndex_event].List_delivery_man[i].car_num


                m_list_delivery_view[i].addView(createButton(i))
            }
            else
            {

                if(i >= m_list_delivery_view.size) { return }

                var txtViewDelivery1 = TextView(applicationContext)
                setTextColor(txtViewDelivery1,0)
                txtViewDelivery1.setTextSize(15.0f)
                txtViewDelivery1.text =  "${i+1}. " + list_event[m_nIndex_event].List_delivery_man[i].name
                m_list_delivery_view[i].addView(txtViewDelivery1)

                var txtViewDelivery2 = TextView(applicationContext)
                setTextColor(txtViewDelivery2,1)
                txtViewDelivery2.setTextSize(15.0f)
                txtViewDelivery2.text =  "  " + list_event[m_nIndex_event].List_delivery_man[i].car_num
                m_list_delivery_view[i].addView(txtViewDelivery2)

                m_list_delivery_view[i].addView(createButton(i))

            }
        }
    }

    fun createButton(a_nIndex : Int): View {
        val lp = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        val button = Button(this)
        button.text = "차량선택"
        lp.setMargins(getDP(0),0,0,0)
        button.layoutParams = lp
        button.setOnClickListener {
//            Log.d(TAG, "i 값 : ${i}")

            val nextIntent = Intent(this, ActivityListCarsInfo::class.java)
            nextIntent.putExtra("id", m_strid)
            nextIntent.putExtra("delivery_index", a_nIndex)
            nextIntent.putExtra("event_index", m_nIndex_event)
            startActivity(nextIntent)
        }
        return button
    }



    private fun getDP(value : Int) : Int{
        var displayMetrics = resources.displayMetrics
        var dp = Math.round(value * displayMetrics.density)
        return dp
    }

    fun InitTextLinearLayout()
    {
        m_list_view.clear()
        m_list_view.add(this.findViewById<LinearLayout>(R.id.text_view_area1))
        m_list_view.add(this.findViewById<LinearLayout>(R.id.text_view_area2))
        m_list_view.add(this.findViewById<LinearLayout>(R.id.text_view_area3))
        m_list_view.add(this.findViewById<LinearLayout>(R.id.text_view_area4))
        m_list_view.add(this.findViewById<LinearLayout>(R.id.text_view_area5))
        m_list_view.add(this.findViewById<LinearLayout>(R.id.text_view_area6))
        m_list_view.add(this.findViewById<LinearLayout>(R.id.text_view_area7))
        m_list_view.add(this.findViewById<LinearLayout>(R.id.text_view_area8))
        m_list_view.add(this.findViewById<LinearLayout>(R.id.text_view_area9))
        m_list_view.add(this.findViewById<LinearLayout>(R.id.text_view_area10))

        //m_list_view.add(itemView.findViewById<LinearLayout>())
    }


    fun clearDelivery()
    {
        for (i in 0 until m_list_delivery_view.size) {
            m_list_delivery_view[i].removeAllViews()
        }
    }

    fun clearMenu()
    {
        for (i in 0 until m_list_view.size)
        {
            m_list_view[i].removeAllViews()
        }

    }

    fun printMenu()
    {
        txt_fix_menu1.text = ""
        txt_fix_menu2.text = ""

        clearMenu()
        for(i in 0 until  list_event[m_nIndex_event].List_Menu.size)
        {
            if(0==i)
            {
                setTextColor(txt_fix_menu1,list_event[m_nIndex_event].List_Menu[i].kind.toInt())
                txt_fix_menu1.text = "${i+1}. " +list_event[m_nIndex_event].List_Menu[i].kind_str
                txt_fix_menu2.text = list_event[m_nIndex_event].List_Menu[i].name
            }
            else
            {
                if(i-1 >= m_list_view.size) { return }

                var txtViewMenu1 = TextView(applicationContext)
                setTextColor(txtViewMenu1,list_event[m_nIndex_event].List_Menu[i].kind.toInt())
                txtViewMenu1.text = "${i+1}. " +list_event[m_nIndex_event].List_Menu[i].kind_str
                txtViewMenu1.setTextSize(15.0f)
                m_list_view[i-1].addView(txtViewMenu1)

                var txtViewMenu2 = TextView(applicationContext)
                setTextColor(txtViewMenu2,1)
                txtViewMenu2.text = list_event[m_nIndex_event].List_Menu[i].name
                txtViewMenu2.setTextSize(15.0f)
                m_list_view[i-1].addView(txtViewMenu2)
            }

        }
    }

    fun setTextColor(a_txtView : TextView, a_nFlag : Int)
    {
        when(a_nFlag)
        {
            0-> a_txtView.setTextColor(application.getColor(R.color.color_green_default2))
            1-> a_txtView.setTextColor(application.getColor(R.color.black))
            2-> a_txtView.setTextColor(application.getColor(R.color.color_purple_place))
            3-> a_txtView.setTextColor(application.getColor(R.color.color_orange_default))
            4-> a_txtView.setTextColor(application.getColor(R.color.color_green_default3))
            else -> a_txtView.setTextColor(application.getColor(R.color.black))
        }

    }



    //메뉴 띄워주기
    fun showListMenu(v: View)
    {
        val nextIntent = Intent(this, ActivityListFoodMenu::class.java)
        nextIntent.putExtra("id", m_strid)
        nextIntent.putExtra("event_date", m_event_date)
        nextIntent.putExtra("event_index", m_nIndex_event)
        startActivity(nextIntent)
    }


    //배달기사 리스트 띄워주기
    fun showListDelivery(v: View)
    {
        val nextIntent = Intent(this, ActivityListDeliveryMan::class.java)
        nextIntent.putExtra("id", m_strid)
        nextIntent.putExtra("event_date", m_event_date)
        nextIntent.putExtra("event_index", m_nIndex_event)
        startActivity(nextIntent)
    }



    //전체 이벤트 리스트에서 자신의 index 찾아서 가지고 있음
    fun findIndex()
    {
        for(i in 0 until  CListCollector.list_event.size )
        {
            Log.d("로그", "printDeliveryMan2")
            if(CListCollector.list_event[i].id == m_strid)
            {
                m_nIndex_event=i
            }
        }
    }


    fun just_OK(v: View)
    {
        finish()
    }

//-------------------------------------이벤트 관련수정 2020-12-17-------------------------------------

    //이벤트 리스트에서 해당 이벤트 가져와서 바뀐 이벤트 내용 적용시킨다.
    fun setModifyEvent()
    {
        list_event[m_nIndex_event].event = m_modifyCotent.toString()
    }

    fun setSignature()
    {

        Log.d("로그", "현재 시간 ${getNow(1)} ")

        list_event[m_nIndex_event].signature = MY_nickName
        list_event[m_nIndex_event].signature_datetime = getNow(1)

        SynEventInDeliveryMan(m_nIndex_event, 3)
    }


    fun SaveEvent(v: View) {
        val url = AWS_DOMAIN+"/hans/event/update_event"

        val client : OkHttpClient = OkHttpClient()
        //m_modifyCotent?: et_event_detail.toString()
        //if(m_modifyCotent == null ) { m_modifyCotent = et_event_detail.text.toString()}
        //setModifyEvent()
        //val body: RequestBody = FormBody.Builder().add("event", m_modifyCotent).add("id",m_id.toString()).build()

        if(m_strid == "-1" ) {
            Log.d(TAG,"이벤트가 삭제되어 그냥 finish")
            finish()
        }

        val body: RequestBody = FormBody.Builder()
            .add("id", m_strid)
            .add("title", list_event[m_nIndex_event].title!!)
            .add("place", list_event[m_nIndex_event].place!!)
            .add("event_date", list_event[m_nIndex_event].event_date!!)
            .add("event_time", list_event[m_nIndex_event].event_time!!)
            .add("event", list_event[m_nIndex_event].event!!)
            .add("user_count", list_event[m_nIndex_event].user_count!!)
            .add("signature", MY_nickName!!)
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
                    setSignature()//시그니처 관련 갱신
                    SortEventDateTime()
                    Toast.makeText(v.context, "저장 되었습니다.", Toast.LENGTH_SHORT).show()
                }

            }
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
            //txt_event_date.text = "${i}-${i2 + 1}-${i3}"
            txt_event_date.text = "${i}-${makeZeroPlus(i2 + 1)}-${makeZeroPlus(i3)}"
            list_event[m_nIndex_event].event_date  = "${i}-${makeZeroPlus(i2 + 1)}-${makeZeroPlus(i3)}"
            //list_event[m_nIndex_event].event_date = "${i}-${i2 + 1}월-${i3}일"
            SynEventInDeliveryMan(m_nIndex_event, 1) //1: 날짜 갱신
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
            list_event[m_nIndex_event].event_time = "${makeZeroPlus(i)}:${makeZeroPlus(i2)}"
            SynEventInDeliveryMan(m_nIndex_event, 2) //2 : 시간 갱신
        }

        var picker = TimePickerDialog(this, listener, hour, minute, false) // true하면 24시간 제
        picker.show()
    }

    fun SendLms()
    {
        val RECORD_REQUEST_CODE = 1000

        //권한이 부여되어 있는지 확인
        var permissonCheck= ContextCompat.checkSelfPermission(this, android.Manifest.permission.SEND_SMS);

        if(permissonCheck == PackageManager.PERMISSION_GRANTED){
            Toast.makeText(getApplicationContext(), "SMS 수신권한 있음", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(getApplicationContext(), "SMS 수신권한 없음", Toast.LENGTH_SHORT).show();

            //권한설정 dialog에서 거부를 누르면
            //ActivityCompat.shouldShowRequestPermissionRationale 메소드의 반환값이 true가 된다.
            //단, 사용자가 "Don't ask again"을 체크한 경우
            //거부하더라도 false를 반환하여, 직접 사용자가 권한을 부여하지 않는 이상, 권한을 요청할 수 없게 된다.
            if(ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.SEND_SMS)){
                //이곳에 권한이 왜 필요한지 설명하는 Toast나 dialog를 띄워준 후, 다시 권한을 요청한다.
                Toast.makeText(getApplicationContext(), "SMS권한이 필요합니다", Toast.LENGTH_SHORT).show();
                ActivityCompat.requestPermissions(this@ActivityEventDetail, arrayOf(android.Manifest.permission.SEND_SMS), RECORD_REQUEST_CODE);
            }else{
                ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.SEND_SMS), RECORD_REQUEST_CODE);
            }
        }



        /*SMS보내기 방법*/

//String senddate = "01012345678"; //전화번호
//String SMSText = "보낼 문자열";
//        val SMSSend = SmsManager.getDefault()
//        SMSSend.sendTextMessage(senddate, null, SMSText, null, null)
//        /*LMS 보내기 방법*/
//        val SMSSend = SmsManager.getDefault()
//        val partMessage = SMSSend.divideMessage(SMSText)
//        SMSSend.sendMultipartTextMessage(senddate, null, partMessage, null, null)

        //입력한 값을 가져와 변수에 담는다
        //val phoneNo= "010-2039-5791"
        //val sms = "진짜 잘가나 확인해보자"
        for(i in 0 until list_event[m_nIndex_event].List_delivery_man.size)
        {
            val  sms= MakeSmsMsg(list_event[m_nIndex_event].List_delivery_man[i].name, list_event[m_nIndex_event].List_delivery_man[i].car_num)

            Log.d("로그", "sms length:${sms.length} ")

            try {
                //전송
                val smsManager: SmsManager = SmsManager.getDefault()
                val partMessage = smsManager.divideMessage(sms)
                //smsManager.sendTextMessage(list_event[m_nIndex_event].List_delivery_man[i].mdn, null, sms, null, null)
                //smsManager.sendTextMessage(, null, sms, null, null)
                //smsManager.sendTextMessage(phoneNo, null, sms, null, null)
                //smsManager.sendMultipartTextMessage(phoneNo, null, partMessage, null, null)
                smsManager.sendMultipartTextMessage(list_event[m_nIndex_event].List_delivery_man[i].mdn, null, partMessage, null, null)
                Toast.makeText(applicationContext, "전송 완료!", Toast.LENGTH_LONG).show()
                UpdateSmsState()
            } catch (e: Exception) {
                Toast.makeText(applicationContext, "SMS faild, please try again later!", Toast.LENGTH_LONG).show()
                Toast.makeText(applicationContext, "err: ${e.toString()}", Toast.LENGTH_LONG).show()
                e.printStackTrace()
            }
        }
    }

    fun MakeSmsMsg(a_name: String, a_car_num: String) : String
    {
        var SendMsg : String
        SendMsg = "[${a_name}님 PonyExpress 배차 안내]\n"
        SendMsg += "차량번호 [${a_car_num}]\n"
        SendMsg += "행사 제목: ${list_event[m_nIndex_event].title}\n"
        SendMsg += "장소: ${list_event[m_nIndex_event].place}\n"
        SendMsg += "인원: ${list_event[m_nIndex_event].user_count}\n"
        SendMsg += "고객: ${list_event[m_nIndex_event].user_name}\n"
        SendMsg += "연락처: ${list_event[m_nIndex_event].mdn}\n"
        SendMsg += "상세내용: ${list_event[m_nIndex_event].event}\n\n"
        SendMsg += "총 패키지 : ${list_event[m_nIndex_event].List_Menu.size} 개\n"

        for(i in 0 until list_event[m_nIndex_event].List_Menu.size)
        {

            SendMsg = SendMsg + "${i+1}. ${list_event[m_nIndex_event].List_Menu[i].kind_str}"+" ${list_event[m_nIndex_event].List_Menu[i].name} \n"
            SendMsg += list_food_menu[getMenuId(i)].desc
            Log.d(TAG,"menu$i : ${list_food_menu[getMenuId(i)].desc}")

            SendMsg += "\n"
            SendMsg += "\n"
        }

        Log.d(TAG," SendMsg : ${SendMsg}")
        return SendMsg
    }

    fun getMenuId(a_nInx : Int) : Int
    {
        var ret = 0
        for( i in 0 until  list_food_menu.size)
        {
            if (list_event[m_nIndex_event].List_Menu[a_nInx].id == list_food_menu[i].id)
            {
                ret =i
            }

        }
        return ret
    }


    //-----------------------DB SmsStat update LMS보냈을때---------------------------------
    fun UpdateSmsState() {
        val url = AWS_DOMAIN+"/hans/sms_state_modify"
        val client : OkHttpClient = OkHttpClient()

        val body: RequestBody = FormBody.Builder().add("event_id",m_strid).add("signature_send_sms", MY_nickName).build()
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
                    list_event[m_nIndex_event].send_sms = "1"
                    list_event[m_nIndex_event].send_sms_datetime = getNow(1)
                    list_event[m_nIndex_event].signature_send_sms = MY_nickName
                    SynEventInDeliveryMan(m_nIndex_event, 4)

                    Toast.makeText(applicationContext, "update 되었습니다.", Toast.LENGTH_SHORT).show()
                }

            }
        })
    }

    //-----------------------행사 삭제 2020-12-19---------------------------------


    fun ShowDlgDeleteEvent(v: View)
    {
        var builder = AlertDialog.Builder(this)
        makeSendNameList()
        builder.setTitle("HansFamily 행사 삭제")
        builder.setMessage("${list_event[m_nIndex_event].title} 행사를 삭제 하시겠습니까?")
        builder.setIcon(R.drawable.ponylogo2)

        // 버튼 클릭시에 무슨 작업을 할 것인가!
        var listener = object : DialogInterface.OnClickListener {
            override fun onClick(p0: DialogInterface?, p1: Int) {
                when (p1) {
                    DialogInterface.BUTTON_POSITIVE -> {
                        DeleteEvent()
                    }

                    DialogInterface.BUTTON_NEGATIVE -> {
                    }

                }
            }
        }

        builder.setPositiveButton("삭제", listener)
        builder.setNegativeButton("취소", listener)

        builder.show()
    }


    fun DeleteEvent() {

        val url = AWS_DOMAIN+"/hans/event_delete"
        val client : OkHttpClient = OkHttpClient()

        val body: RequestBody = FormBody.Builder().add("event_id",m_strid).build()
        m_strid="-1"
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

                    DeleteEventInDeliveryMan(m_nIndex_event)
                    DeleteEventInEvent(m_nIndex_event)
                    synEventJustNum(m_nIndex_event)
                    sortEventNUm()
                    Toast.makeText(applicationContext, "삭제 되었습니다.", Toast.LENGTH_SHORT).show()
                }

            }
        })
    }

    fun SortEventDateTime()
    {
        var lastFlag = 0
        if( list_event.size == 1 ) { return }

        var tmpCurEvent =  list_event[m_nIndex_event]

        list_event.removeAt(m_nIndex_event)
        for(i in 0 until list_event.size)
        {
            if( (list_event[i].event_date + list_event[i].event_time)  >  (tmpCurEvent.event_date + tmpCurEvent.event_time) )
            {
                lastFlag = 1
                if(i == 0)
                {
                    tmpCurEvent.num = i+1
                    list_event.add(0,tmpCurEvent)
                    m_nIndex_event = 0
                }
                else
                {
                    tmpCurEvent.num = i+1
                    list_event.add(i,tmpCurEvent)
                    m_nIndex_event = i
                }

                Log.d("로그","현재 이벤트보다 날짜가 더크다 이벤트 num : ${list_event[i].num}  ${list_event[i].event_date}, 현재 이벤트: ${list_event[m_nIndex_event].event_date}")
                break
            }

        }
        if(lastFlag == 0)
        {
            m_nIndex_event = list_event.size
            list_event.add(tmpCurEvent)
        }

        sortEventNUm()
    }

    fun sortEventNUm()
    {
        for(i in 0 until list_event.size)
        {
            list_event[i].num = i+1
        }
    }


    fun CallUser(v: View)
    {
        var intent = Intent(Intent.ACTION_DIAL)
        //intent.data = Uri.parse("tel:0537207900")
        intent.data = Uri.parse("tel:"+list_event[m_nIndex_event].mdn)
        if(intent.resolveActivity(packageManager) != null){
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        Log.d("로그", "onResume....")

    }

    override fun onPause() {
        super.onPause()
        Log.d("로그", "onPause........")
    }

    override fun onRestart() {
        super.onRestart()
        Log.d("로그", "onRestart........")
        //   printDeliveryMan()
        printDeliveryMan()
        printMenu()

    }

}