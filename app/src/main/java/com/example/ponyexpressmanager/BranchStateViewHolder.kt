package com.example.ponyexpressmanager

import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.ponyexpressmanager.CListCollector.TAG
import com.example.ponyexpressmanager.CListCollector.list_event
import kotlinx.android.synthetic.main.recycler_item_state_event.view.*

class BranchStateViewHolder(itemView : View, aStateRecyclerviewInterface : StateRecyclerviewInterface ) :
    RecyclerView.ViewHolder(itemView),
    View.OnClickListener
{

    var m_list_view : ArrayList<LinearLayout> = ArrayList()
    lateinit var mEventDate : String

    var mStateRecyclerviewInterface : StateRecyclerviewInterface? = null
    private val m_txt_event_date = itemView.txt_event_date
    private val m_txt_event_count1 = itemView.txt_event_count1
    private val m_txt_event_count2 = itemView.txt_event_count2
    private val m_txt_send_sms_count = itemView.txt_send_sms_count
    init {
        mStateRecyclerviewInterface = aStateRecyclerviewInterface
    }


    fun bind(a_event_date : String) {

        mEventDate = a_event_date
        SetTextLinearLayout()

        var eventcount =0
        var smscount=0
        var countview = 0


        for(i in 0 until list_event.size)
        {
            if(a_event_date == list_event[i].event_date)
            {
                eventcount++
                if(list_event[i].send_sms == "1")       {  smscount++ }

                if(countview >= m_list_view.size)    { continue }
                var txtViewnum = TextView(itemView.context)
                txtViewnum.setText("")
                txtViewnum.setText(list_event[i].num.toString() + ". ")
                txtViewnum.setTextColor(ContextCompat.getColor(itemView.context,R.color.black))
                m_list_view[countview].addView(txtViewnum)
                var view = m_list_view[countview]
                view.removeAllViews()
                view.addView(txtViewnum)


                var txtViewtime = TextView(itemView.context)
                txtViewtime.setText("")
                txtViewtime.setText(list_event[i].event_time + " ")
                txtViewtime.setTextColor(ContextCompat.getColor(itemView.context,R.color.color_date_time))
                view.addView(txtViewtime)


                var txtViewplace = TextView(itemView.context)
                txtViewplace.setText("")
                txtViewplace.setText(list_event[i].place + " ")
                txtViewplace.setTextColor(ContextCompat.getColor(itemView.context,R.color.color_purple_place))

                view.addView(txtViewplace)


                var txtViewusercount = TextView(itemView.context)
                txtViewusercount.setText("")
                txtViewusercount.setText(list_event[i].user_count + " ")
                txtViewusercount.setTextColor(ContextCompat.getColor(itemView.context,R.color.black))

                view.addView(txtViewusercount)


                var txtView_sms = TextView(itemView.context)
                txtView_sms.setText("")
                txtView_sms.setText("배차 SMS ")
                txtView_sms.setTextColor(ContextCompat.getColor(itemView.context,R.color.color_green_sms))

                view.addView(txtView_sms)


                if(list_event[i].send_sms == "1")
                {
                    var txtView_sms_state = TextView(itemView.context)
                    txtView_sms_state.setText("")
                    txtView_sms_state.setText(" 발송완료!!" + " ")
                    txtView_sms_state.setTextColor(ContextCompat.getColor(itemView.context,R.color.color_blue_sms))
                    view.addView(txtView_sms_state)

                }
                else
                {
                    var txtView_sms_state = TextView(itemView.context)
                    txtView_sms_state.setText("")
                    txtView_sms_state.setText(" 미발송!!" + " ")
                    txtView_sms_state.setTextColor(ContextCompat.getColor(itemView.context,R.color.color_red_default))
                    m_list_view[countview].addView(txtView_sms_state)
                }

                //배차
                var txtView_delivery = TextView(itemView.context)
                txtView_delivery.setText("")
                txtView_delivery.setText("배차: ")
                txtView_delivery.setTextColor(ContextCompat.getColor(itemView.context,R.color.color_text_default))
                var view2 = m_list_view[countview+1]
                view2.removeAllViews()
                view2.addView(txtView_delivery)


                var txtView_deliveryman_list = TextView(itemView.context)

                txtView_deliveryman_list.setText("")

                //배달기사 리스트
                for(inx in 0 until list_event[i].List_delivery_man.size)
                {
                    if (inx == 0 )
                    {
                        txtView_deliveryman_list.setText(list_event[i].List_delivery_man[inx].name)
                    }
                    else
                    {
                        txtView_deliveryman_list.setText(txtView_deliveryman_list.text.toString()+ ", " + list_event[i].List_delivery_man[inx].name)
                    }

                }
                txtView_deliveryman_list.setText(txtView_deliveryman_list.text.toString()+ " ${list_event[i].List_delivery_man.size}명")
                txtView_deliveryman_list.setTextColor(ContextCompat.getColor(itemView.context,R.color.black))
                view2.addView(txtView_deliveryman_list)

                //배달기사 총계
//                var txtView_deliveryman_count = TextView(itemView.context)
//                txtView_deliveryman_count.setText("")
//                txtView_deliveryman_count.setText(" ${list_event[i].List_delivery_man.size}명")
//                txtView_deliveryman_count.setTextColor(ContextCompat.getColor(itemView.context,R.color.black))
//                //m_list_view[countview+1].addView(txtView_deliveryman_count)
//                view2.addView(txtView_deliveryman_count)
                countview += 2
            }

        }// list_event

        m_txt_event_date.setText("")
        m_txt_event_date.setText(a_event_date + " 행사 현황")
        m_txt_event_date.setTextColor(ContextCompat.getColor(itemView.context,R.color.black))

        m_txt_event_count1.setText("")
        m_txt_event_count1.setText(eventcount.toString())
        m_txt_event_count1.setTextColor(ContextCompat.getColor(itemView.context,R.color.black))

        m_txt_event_count2.setText("")
        m_txt_event_count2.setText("/" + eventcount.toString())
        m_txt_event_count2.setTextColor(ContextCompat.getColor(itemView.context,R.color.black))

        if(smscount == eventcount)
        {
            m_txt_send_sms_count.setText("")
            m_txt_send_sms_count.setText(smscount.toString())
            m_txt_send_sms_count.setTextColor(ContextCompat.getColor(itemView.context,R.color.black))
            if(smscount != 0 )
            {
                m_txt_event_count2.setText(m_txt_event_count2.text.toString() + " Good!!")
            }
        }
        else
        {
            m_txt_send_sms_count.setText("")
            m_txt_send_sms_count.setText(smscount.toString())
            m_txt_send_sms_count.setTextColor(ContextCompat.getColor(itemView.context,R.color.color_red_default))
        }

    } // bind()


    fun SetTextLinearLayout()
    {
        m_list_view.clear()
        m_list_view.add(itemView.findViewById<LinearLayout>(R.id.text_view_area1_1))
        m_list_view.add(itemView.findViewById<LinearLayout>(R.id.text_view_area1_2))
        m_list_view.add(itemView.findViewById<LinearLayout>(R.id.text_view_area2_1))
        m_list_view.add(itemView.findViewById<LinearLayout>(R.id.text_view_area2_2))
        m_list_view.add(itemView.findViewById<LinearLayout>(R.id.text_view_area3_1))
        m_list_view.add(itemView.findViewById<LinearLayout>(R.id.text_view_area3_2))
        m_list_view.add(itemView.findViewById<LinearLayout>(R.id.text_view_area4_1))
        m_list_view.add(itemView.findViewById<LinearLayout>(R.id.text_view_area4_2))

        //m_list_view.add(itemView.findViewById<LinearLayout>())
    }


    override fun onClick(v: View?) {
        Log.d(TAG, "BranchViewHolder - OnClick() called")
        //this.mStateRecyclerviewInterface?.onStateEventClicked(adapterPosition, mEventDate)
    }
}