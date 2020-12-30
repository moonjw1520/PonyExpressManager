package com.example.ponyexpressmanager

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.CheckBox
import android.widget.TextView
import com.example.ponyexpressmanager.CListCollector.list_event

class AdapterListDelivery(context: Context, item : ArrayList<CDeliveryPerson>, a_nIndex : Int, a_event_id : String) : BaseAdapter()
{

    private val m_Context = context
    private val m_list_Delvery_man = item
    private val m_Inflater = LayoutInflater.from(m_Context)
    private val m_nIndex = a_nIndex
    private val a_event_id = a_event_id


    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var view = convertView
        lateinit var viewHolder : ViewHolder1

        //뷰가 캐싱되지 않았을 경우 뷰를 만들고 뷰 홀더에 등록.
        if(view == null){
            viewHolder = ViewHolder1()
            view = m_Inflater.inflate(R.layout.list_item_deliveryman,parent, false)
            viewHolder.text_name = view.findViewById(R.id.txt_name)
            viewHolder.txt_wait = view.findViewById(R.id.txt_wait)
            //viewHolder.txt_complete = view.findViewById(R.id.txt_complete)
            viewHolder.checkBox = view.findViewById(R.id.checkBox)
            viewHolder.txt_event_date1 = view.findViewById(R.id.txt_event_date1)
            viewHolder.txt_event_title1 = view.findViewById(R.id.txt_event_title1)
            viewHolder.txt_event_date2 = view.findViewById(R.id.txt_event_date2)
            viewHolder.txt_event_title2 = view.findViewById(R.id.txt_event_title2)
            viewHolder.txt_event_date3 = view.findViewById(R.id.txt_event_date3)
            viewHolder.txt_event_title3 = view.findViewById(R.id.txt_event_title3)

            var list_date  = arrayListOf<TextView>()
            var list_title  = arrayListOf<TextView>()

            list_date.add(viewHolder.txt_event_date1)
            list_date.add(viewHolder.txt_event_date2)
            list_date.add(viewHolder.txt_event_date3)

            list_title.add(viewHolder.txt_event_title1)
            list_title.add(viewHolder.txt_event_title2)
            list_title.add(viewHolder.txt_event_title3)


            for(i in 0 until  m_list_Delvery_man[position].List_Event.size)
            {
                if(i >= list_title.size)   {       break;       }


                if(list_event[m_nIndex].event_date == m_list_Delvery_man[position].List_Event[i].event_date)
                {
                    if(a_event_id != m_list_Delvery_man[position].List_Event[i].id)
                    {
                        list_title[i].setTextColor(m_Context.getColor(R.color.color_red_default))
                        list_title[i].text = m_list_Delvery_man[position].List_Event[i].title
                        list_date[i].setTextColor(m_Context.getColor(R.color.color_red_default))
                        list_date[i].text = m_list_Delvery_man[position].List_Event[i].event_date
                        viewHolder.txt_wait.setTextColor(m_Context.getColor(R.color.color_red_default))
                        viewHolder.txt_wait.text = "중복확인"
                    }
                    Log.d("로그", "111 position : ${position} 이름 : ${m_list_Delvery_man[position].name}")
                }
                else
                {
                    list_title[i].text = m_list_Delvery_man[position].List_Event[i].title
                    list_date[i].text = m_list_Delvery_man[position].List_Event[i].event_date
                }

            }
            viewHolder.text_name.text = m_list_Delvery_man[position].name

            view.tag = viewHolder
            return view
        }
        else{
            //viewHolder = view.tag as ViewHolder1
            //viewHolder = view.tag as ViewHolder1
        }
        viewHolder = ViewHolder1()
        view = m_Inflater.inflate(R.layout.list_item_deliveryman,parent, false)
        viewHolder.text_name = view.findViewById(R.id.txt_name)
        viewHolder.txt_wait = view.findViewById(R.id.txt_wait)
        //viewHolder.txt_complete = view.findViewById(R.id.txt_complete)
        viewHolder.checkBox = view.findViewById(R.id.checkBox)
        viewHolder.txt_event_date1 = view.findViewById(R.id.txt_event_date1)
        viewHolder.txt_event_title1 = view.findViewById(R.id.txt_event_title1)
        viewHolder.txt_event_date2 = view.findViewById(R.id.txt_event_date2)
        viewHolder.txt_event_title2 = view.findViewById(R.id.txt_event_title2)
        viewHolder.txt_event_date3 = view.findViewById(R.id.txt_event_date3)
        viewHolder.txt_event_title3 = view.findViewById(R.id.txt_event_title3)

        var list_date  = arrayListOf<TextView>()
        var list_title  = arrayListOf<TextView>()

        list_date.add(viewHolder.txt_event_date1)
        list_date.add(viewHolder.txt_event_date2)
        list_date.add(viewHolder.txt_event_date3)

        list_title.add(viewHolder.txt_event_title1)
        list_title.add(viewHolder.txt_event_title2)
        list_title.add(viewHolder.txt_event_title3)


        for(i in 0 until  m_list_Delvery_man[position].List_Event.size)
        {
            if(i >= list_title.size)   {       break;       }

            if(list_event[m_nIndex].event_date == m_list_Delvery_man[position].List_Event[i].event_date)
            {
                if(a_event_id != m_list_Delvery_man[position].List_Event[i].id) {
                    list_title[i].setTextColor(m_Context.getColor(R.color.color_red_default))
                    list_title[i].text = m_list_Delvery_man[position].List_Event[i].title
                    list_date[i].setTextColor(m_Context.getColor(R.color.color_red_default))
                    list_date[i].text = m_list_Delvery_man[position].List_Event[i].event_date
                    viewHolder.txt_wait.setTextColor(m_Context.getColor(R.color.color_red_default))
                    viewHolder.txt_wait.text = "중복확인"
                }
                Log.d("로그", "222 position : ${position} 이름 : ${m_list_Delvery_man[position].name}")

            }
            else
            {
                list_title[i].text = m_list_Delvery_man[position].List_Event[i].title
                list_date[i].text = m_list_Delvery_man[position].List_Event[i].event_date
            }
        }
        viewHolder.text_name.text = m_list_Delvery_man[position].name


        return view
    }
    override fun getItem(position: Int) = m_list_Delvery_man[position]

    override fun getItemId(position: Int) = position.toLong()

    override fun getCount() = m_list_Delvery_man.size

    //내부클래스로 뷰홀더 생성
    inner class ViewHolder1 {
        lateinit var text_name: TextView
        lateinit var txt_wait: TextView
        lateinit var checkBox: CheckBox
        lateinit var txt_event_date1: TextView
        lateinit var txt_event_title1: TextView
        lateinit var txt_event_date2: TextView
        lateinit var txt_event_title2: TextView
        lateinit var txt_event_date3: TextView
        lateinit var txt_event_title3: TextView
    }
}