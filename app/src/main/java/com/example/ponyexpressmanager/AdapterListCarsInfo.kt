package com.example.ponyexpressmanager

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.CheckBox
import android.widget.TextView

class AdapterListCarsInfo(context: Context, item : ArrayList<D_CarsInfo>) : BaseAdapter()
{
    private val m_Context = context
    private val m_list_cars = item
    private val m_Inflater = LayoutInflater.from(m_Context)

    lateinit  var m_viewHolder : AdapterListCarsInfo.ViewHolder

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        m_viewHolder = ViewHolder()
        var view = convertView

        if(view == null){
            view = m_Inflater.inflate(R.layout.list_item_cars_info ,parent, false)
            m_viewHolder.text_num = view.findViewById(R.id.txt_num)
            m_viewHolder.checkBox = view.findViewById(R.id.checkBox)
            m_viewHolder.text_num.text = m_list_cars[position].num_str

        }
        else
        {
            view = m_Inflater.inflate(R.layout.list_item_cars_info ,parent, false)
            m_viewHolder.text_num = view.findViewById(R.id.txt_num)
            m_viewHolder.checkBox = view.findViewById(R.id.checkBox)
            m_viewHolder.text_num.text = m_list_cars[position].num_str

        }

        return view

    }

    override fun getItem(position: Int) = m_list_cars[position]

    override fun getItemId(position: Int) = position.toLong()

    override fun getCount() = m_list_cars.size


    //내부클래스로 뷰홀더 생성
    inner class ViewHolder {
        lateinit var text_num: TextView
        lateinit var checkBox: CheckBox
    }
}