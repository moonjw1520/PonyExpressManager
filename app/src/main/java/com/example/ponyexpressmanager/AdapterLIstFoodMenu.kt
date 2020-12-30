package com.example.ponyexpressmanager

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.CheckBox
import android.widget.TextView
import com.example.ponyexpressmanager.CListCollector.TAG
import com.example.ponyexpressmanager.CListCollector.list_food_menu

class AdapterLIstFoodMenu(context: Context, item : ArrayList<D_PMenu>, var m_checkInterface: CheckInterface) : BaseAdapter()
{
    private val m_Context = context
    private val m_list_menu = item
    private val m_Inflater = LayoutInflater.from(m_Context)

    lateinit  var m_viewHolder :  ViewHolder

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        m_viewHolder = ViewHolder()
        var view = convertView

        if(view == null){
            view = m_Inflater.inflate(R.layout.list_item_food_menu ,parent, false)

            m_viewHolder.text_kind = view.findViewById(R.id.txt_kind)
            m_viewHolder.text_name = view.findViewById(R.id.txt_name)
            m_viewHolder.checkBox = view.findViewById(R.id.checkBox)

            KindColorSelect(list_food_menu[position].kind.toInt())
            m_viewHolder.text_kind.text = list_food_menu[position].kind_str
            m_viewHolder.text_name.text = list_food_menu[position].name


            m_viewHolder.checkBox.setOnCheckedChangeListener { buttonView, isChecked ->
                Log.d(TAG, "checkBox Clicked1..")
                m_checkInterface.onCheckClicked(position)
            }


        }
        else
        {
            view = m_Inflater.inflate(R.layout.list_item_food_menu ,parent, false)
            m_viewHolder.text_kind = view.findViewById(R.id.txt_kind)
            m_viewHolder.text_name = view.findViewById(R.id.txt_name)
            m_viewHolder.checkBox = view.findViewById(R.id.checkBox)

            KindColorSelect(list_food_menu[position].kind.toInt())
            m_viewHolder.text_kind.text = list_food_menu[position].kind_str
            m_viewHolder.text_name.text = list_food_menu[position].name

            m_viewHolder.checkBox.setOnCheckedChangeListener { buttonView, isChecked ->
                Log.d(TAG, "checkBox Clicked2..")
                m_checkInterface.onCheckClicked(position)
            }

        }

        return view
    }


    override fun getItem(position: Int) = m_list_menu[position]

    override fun getItemId(position: Int) = position.toLong()

    override fun getCount() = m_list_menu.size


    //내부클래스로 뷰홀더 생성
    inner class ViewHolder {
        lateinit var text_kind: TextView
        lateinit var text_name: TextView
        lateinit var checkBox: CheckBox
    }


    fun KindColorSelect(a_nFlag : Int)
    {
        when(a_nFlag)
        {
            1-> m_viewHolder.text_kind.setTextColor(m_Context.getColor(R.color.black))
            2-> m_viewHolder.text_kind.setTextColor(m_Context.getColor(R.color.color_green_default))
            3-> m_viewHolder.text_kind.setTextColor(m_Context.getColor(R.color.color_blue_sms))
            4-> m_viewHolder.text_kind.setTextColor(m_Context.getColor(R.color.color_orange_default))
            else -> m_viewHolder.text_kind.setTextColor(m_Context.getColor(R.color.color_purple_place))

        }
    }

}