package com.example.ponyexpressmanager

import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.ponyexpressmanager.CListCollector.list_event
import kotlinx.android.synthetic.main.recycler_item_event.view.*

class BranchEventViewHolder(itemView : View, var m_EventRecyclerviewInterface : EventRecyclerviewInterface ) :
    RecyclerView.ViewHolder(itemView),
    View.OnClickListener
{
    val TAG ="로그"
    private val m_txt_title = itemView.text_title
    private val m_txt_user_name = itemView.text_user_name
    private val m_txt_mdn = itemView.text_mdn
    private val m_txt_place =itemView.text_place
    private val m_txt_evnet_date =itemView.text_evnet_date
    private val m_txt_evnet_time =itemView.text_evnet_time
    private val m_txt_user_count =itemView.text_user_count
    private val m_txt_send_sms_sign_datetime1 =itemView.txt_send_sms_sign_datetime1
    private val m_txt_send_sms1 =itemView.txt_send_sms1


    //private var m_MyRecyclerviewInterface : MyRecyclerviewInterface? = null
    lateinit var m_eventData : CEvent
    lateinit var m_strid : String

    var m_list_txt_delivery_man = arrayListOf<TextView>()

    init{
        itemView.setOnClickListener(this)
        //
        InitTextVew()
    }

    fun bind(a_EventData : CEvent) {
        Log.d(TAG, "BranchViewHolder - bind() called")

        //var num =adapterPosition+1
        m_txt_title.text = a_EventData.num.toString()+"."+a_EventData.title
        m_txt_user_name.text = a_EventData.user_name
        m_txt_mdn.text = a_EventData.mdn
        m_txt_place.text = a_EventData.place
        m_txt_evnet_date.text = a_EventData.event_date
        m_txt_evnet_time.text = a_EventData.event_time
        m_txt_user_count.text = a_EventData.user_count

        //sms 관련


        var tmp_sms_date = a_EventData.send_sms_datetime
        var tmp_sms_signature = a_EventData.signature_send_sms
        if(a_EventData.send_sms == "1")
        {
            m_txt_send_sms1.setTextColor(ContextCompat.getColor(itemView.context,R.color.color_blue_sms))
            m_txt_send_sms1.setText(R.string.sms_sent)
            if(tmp_sms_date != "null")        {            tmp_sms_date =tmp_sms_date.subSequence(5,tmp_sms_date.length).toString()        }
            else                              {            tmp_sms_date = ""        }
            if(tmp_sms_signature != "null")   {            tmp_sms_signature = "@" + tmp_sms_signature     }
            else                              {            tmp_sms_signature = ""       }
        }
        else
        {
            m_txt_send_sms1.setTextColor(ContextCompat.getColor(itemView.context,R.color.color_red_default))
            m_txt_send_sms1.setText(R.string.sms_dont_send)
            tmp_sms_date=""
            tmp_sms_signature=""
        }


        var sms_sgin_datetime = tmp_sms_signature+ " " + tmp_sms_date

        m_txt_send_sms_sign_datetime1.setText(sms_sgin_datetime)

        m_strid = a_EventData.id
        m_eventData = a_EventData

        printDeliveryMan()
//        if (a_EventData != null && a_EventData.file1 != "" && a_EventData.file1 != "null") {
//            Log.d(TAG,"--file1 내용 있음--")
//            Glide
//                .with(GlobalApplication.instance)
//                .load(a_EventData.file1)
//                .centerCrop()
//                .placeholder(R.mipmap.ic_launcher)
//                .into(m_img_evnet_time);
//
//        }
//        else
//        {
//            Log.d(TAG,"file1 내용 없음")
//        }
    }

    fun InitTextVew()
    {
        m_list_txt_delivery_man.add(itemView.txt_recycle_fix_delivery1)
        m_list_txt_delivery_man.add(itemView.txt_recycle_fix_delivery2)
        m_list_txt_delivery_man.add(itemView.txt_recycle_fix_delivery3)
        m_list_txt_delivery_man.add(itemView.txt_recycle_fix_delivery4)
        m_list_txt_delivery_man.add(itemView.txt_recycle_fix_delivery5)
    }

    fun celeanTextView()
    {
        for(i in 0 until  m_list_txt_delivery_man.size)
        {
            m_list_txt_delivery_man[i].text=""
        }
    }

    fun printDeliveryMan()
    {
        celeanTextView()
        for(i in 0 until  CListCollector.list_event.size )
        {
            if(CListCollector.list_event[i].id == m_strid)
            {
                list_event[i].num = adapterPosition+1
                itemView.txt_recycle_total_delivery.text = "총: ${CListCollector.list_event[i].List_delivery_man.size}명"

                if(CListCollector.list_event[i].List_delivery_man.isEmpty()){
                    Log.d("로그","list_delivery_man is empty")
                    return
                }
                for(inx in 0 until  CListCollector.list_event[i].List_delivery_man.size)
                {
                    if(inx>=m_list_txt_delivery_man.size) { return }
                    m_list_txt_delivery_man[inx].text = CListCollector.list_event[i].List_delivery_man[inx].name
                }
                break
            }
        }
    }


    override fun onClick(p0: View?) {
        Log.d(TAG, "BranchViewHolder - OnClick() called")
        this.m_EventRecyclerviewInterface?.onItemClicked(adapterPosition, m_eventData)
    }
}