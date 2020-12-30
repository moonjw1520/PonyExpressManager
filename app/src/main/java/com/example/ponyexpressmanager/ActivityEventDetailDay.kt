package com.example.ponyexpressmanager

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_event_detail.*
import kotlinx.android.synthetic.main.activity_event_detail.txt_event_date
import kotlinx.android.synthetic.main.recycler_item_state_event.*

class ActivityEventDetailDay : AppCompatActivity() {

    lateinit  var mEvenDate : String
    var mListView : ArrayList<LinearLayout> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event_detail_day)

        mEvenDate = intent.getStringExtra("eventDate") as String

        SetTextLinearLayout()

        PrintEventDay()

        btn_OK.setOnClickListener{
            finish()
        }
    }


    fun SetTextLinearLayout()
    {
        mListView.clear()
        mListView.add(this.findViewById<LinearLayout>(R.id.text_view_area1_1))
        mListView.add(this.findViewById<LinearLayout>(R.id.text_view_area1_2))
        mListView.add(this.findViewById<LinearLayout>(R.id.text_view_area2_1))
        mListView.add(this.findViewById<LinearLayout>(R.id.text_view_area2_2))
        mListView.add(this.findViewById<LinearLayout>(R.id.text_view_area3_1))
        mListView.add(this.findViewById<LinearLayout>(R.id.text_view_area3_2))
        mListView.add(this.findViewById<LinearLayout>(R.id.text_view_area4_1))
        mListView.add(this.findViewById<LinearLayout>(R.id.text_view_area4_2))


        mListView.add(this.findViewById<LinearLayout>(R.id.text_view_area5_1))
        mListView.add(this.findViewById<LinearLayout>(R.id.text_view_area5_2))
        mListView.add(this.findViewById<LinearLayout>(R.id.text_view_area6_1))
        mListView.add(this.findViewById<LinearLayout>(R.id.text_view_area6_2))
        mListView.add(this.findViewById<LinearLayout>(R.id.text_view_area7_1))
        mListView.add(this.findViewById<LinearLayout>(R.id.text_view_area7_2))
        mListView.add(this.findViewById<LinearLayout>(R.id.text_view_area8_1))
        mListView.add(this.findViewById<LinearLayout>(R.id.text_view_area8_2))

        mListView.add(this.findViewById<LinearLayout>(R.id.text_view_area9_1))
        mListView.add(this.findViewById<LinearLayout>(R.id.text_view_area9_2))
        mListView.add(this.findViewById<LinearLayout>(R.id.text_view_area10_1))
        mListView.add(this.findViewById<LinearLayout>(R.id.text_view_area10_2))
        mListView.add(this.findViewById<LinearLayout>(R.id.text_view_area11_1))
        mListView.add(this.findViewById<LinearLayout>(R.id.text_view_area11_2))
        mListView.add(this.findViewById<LinearLayout>(R.id.text_view_area12_1))
        mListView.add(this.findViewById<LinearLayout>(R.id.text_view_area12_2))

        mListView.add(this.findViewById<LinearLayout>(R.id.text_view_area13_1))
        mListView.add(this.findViewById<LinearLayout>(R.id.text_view_area13_2))
        mListView.add(this.findViewById<LinearLayout>(R.id.text_view_area14_1))
        mListView.add(this.findViewById<LinearLayout>(R.id.text_view_area14_2))
        mListView.add(this.findViewById<LinearLayout>(R.id.text_view_area15_1))
        mListView.add(this.findViewById<LinearLayout>(R.id.text_view_area15_2))
        mListView.add(this.findViewById<LinearLayout>(R.id.text_view_area16_1))
        mListView.add(this.findViewById<LinearLayout>(R.id.text_view_area16_2))

        mListView.add(this.findViewById<LinearLayout>(R.id.text_view_area17_1))
        mListView.add(this.findViewById<LinearLayout>(R.id.text_view_area17_2))
        mListView.add(this.findViewById<LinearLayout>(R.id.text_view_area18_1))
        mListView.add(this.findViewById<LinearLayout>(R.id.text_view_area18_2))
        mListView.add(this.findViewById<LinearLayout>(R.id.text_view_area19_1))
        mListView.add(this.findViewById<LinearLayout>(R.id.text_view_area19_2))
        mListView.add(this.findViewById<LinearLayout>(R.id.text_view_area20_1))
        mListView.add(this.findViewById<LinearLayout>(R.id.text_view_area20_2))


    }


    fun PrintEventDay()
    {
        var eventcount =0
        var smscount=0
        var countview = 0


        for(i in 0 until CListCollector.list_event.size)
        {
            if(mEvenDate == CListCollector.list_event[i].event_date)
            {
                eventcount++
                if(countview >= mListView.size)    { break }
                var txtViewnum = TextView(applicationContext)
                txtViewnum.setTextSize(15.0f)
                txtViewnum.setText("")
                txtViewnum.setText(CListCollector.list_event[i].num.toString() + ". ")
                txtViewnum.setTextColor(ContextCompat.getColor(applicationContext,R.color.black))
                mListView[countview].addView(txtViewnum)
                var view = mListView[countview]
                view.removeAllViews()

                view.addView(txtViewnum)


                var txtViewtime = TextView(applicationContext)
                txtViewtime.setTextSize(15.0f)
                txtViewtime.setText("")
                txtViewtime.setText(CListCollector.list_event[i].event_time + " ")
                txtViewtime.setTextColor(ContextCompat.getColor(applicationContext,R.color.color_date_time))
                view.addView(txtViewtime)


                var txtViewplace = TextView(applicationContext)
                txtViewplace.setTextSize(15.0f)
                txtViewplace.setText("")
                txtViewplace.setText(CListCollector.list_event[i].place + " ")
                txtViewplace.setTextColor(ContextCompat.getColor(applicationContext,R.color.color_purple_place))
                view.addView(txtViewplace)


                var txtViewusercount = TextView(applicationContext)
                txtViewusercount.setTextSize(15.0f)
                txtViewusercount.setText("")
                txtViewusercount.setText(CListCollector.list_event[i].user_count + " ")
                txtViewusercount.setTextColor(ContextCompat.getColor(applicationContext,R.color.black))
                view.addView(txtViewusercount)


                var txtView_sms = TextView(applicationContext)
                txtView_sms.setTextSize(15.0f)
                txtView_sms.setText("")
                txtView_sms.setText(" SMS ")
                txtView_sms.setTextColor(ContextCompat.getColor(applicationContext,R.color.color_green_sms))
                view.addView(txtView_sms)


                if(CListCollector.list_event[i].send_sms == "1")
                {
                    var txtView_sms_state = TextView(applicationContext)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        txtView_sms_state.setAutoSizeTextTypeWithDefaults(TextView.AUTO_SIZE_TEXT_TYPE_UNIFORM)
                    }
                    txtView_sms_state.setText("")
                    txtView_sms_state.setText(" 발송완료!!" + " ")
                    txtView_sms_state.setTextColor(ContextCompat.getColor(applicationContext,R.color.color_blue_sms))
                    view.addView(txtView_sms_state)
                    smscount++ //화면에 뿌려주기 위해
                }
                else
                {
                    var txtView_sms_state = TextView(applicationContext)
                    txtView_sms_state.setText("")
                    txtView_sms_state.setText(" 미발송!!" + " ")
                    txtView_sms_state.setTextColor(ContextCompat.getColor(applicationContext,R.color.color_red_default))
                    view.addView(txtView_sms_state)
                }

                //배차
                var txtView_delivery = TextView(applicationContext)
                txtView_delivery.setTextSize(15.0f)
                txtView_delivery.setText("")
                txtView_delivery.setText("배차: ")
                txtView_delivery.setTextColor(ContextCompat.getColor(applicationContext,R.color.color_text_default))

                var view2 = mListView[countview+1]
                view2.removeAllViews()
                view2.addView(txtView_delivery)

                var txtView_deliveryman_list = TextView(applicationContext)
                txtView_deliveryman_list.setTextSize(15.0f)

                txtView_deliveryman_list.setText("")

                //배달기사 리스트
                for(inx in 0 until CListCollector.list_event[i].List_delivery_man.size)
                {
                    if (inx == 0 )
                    {
                        txtView_deliveryman_list.setText(CListCollector.list_event[i].List_delivery_man[inx].name)
                    }
                    else
                    {
                        txtView_deliveryman_list.setText(txtView_deliveryman_list.text.toString()+ ", " + CListCollector.list_event[i].List_delivery_man[inx].name)
                    }

                }

                txtView_deliveryman_list.text = txtView_deliveryman_list.text.toString() +" ${CListCollector.list_event[i].List_delivery_man.size}명"

                txtView_deliveryman_list.setTextColor(ContextCompat.getColor(applicationContext,R.color.black))
                view2.addView(txtView_deliveryman_list)

                countview += 2
            }

        }// list_event

        txt_event_date.setText("")
        txt_event_date.setText(mEvenDate + " 행사 현황")
        txt_event_date.setTextColor(ContextCompat.getColor(applicationContext,R.color.black))

        txt_event_count1.setText("")
        txt_event_count1.setText(eventcount.toString())
        txt_event_count1.setTextColor(ContextCompat.getColor(applicationContext,R.color.black))

        txt_event_count2.setText("")
        txt_event_count2.setText("/" + eventcount.toString())
        txt_event_count2.setTextColor(ContextCompat.getColor(applicationContext,R.color.black))

        if(smscount == eventcount)
        {
            txt_send_sms_count.setText("")
            txt_send_sms_count.setText(smscount.toString())
            txt_send_sms_count.setTextColor(ContextCompat.getColor(applicationContext,R.color.black))
            if(smscount != 0 )
            {
                txt_event_count2.setText(txt_event_count2.text.toString() + " Good!!")
            }
        }
        else
        {
            txt_send_sms_count.setText("")
            txt_send_sms_count.setText(smscount.toString())
            txt_send_sms_count.setTextColor(ContextCompat.getColor(applicationContext,R.color.color_red_default))
        }
    }

}