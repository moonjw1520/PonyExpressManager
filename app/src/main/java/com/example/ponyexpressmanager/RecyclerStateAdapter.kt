package com.example.ponyexpressmanager

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class RecyclerStateAdapter(val context : Context, val modelist:ArrayList<String>, a_StateRecyclerviewInterface : StateRecyclerviewInterface) :
    RecyclerView.Adapter<BranchStateViewHolder>()
{
    val TAG: String = "로그"
    private  var m_modelList = ArrayList<String>()
    var mStateRecyclerviewInterface : StateRecyclerviewInterface? = null
    init {
        mStateRecyclerviewInterface = a_StateRecyclerviewInterface
        m_modelList  = modelist
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BranchStateViewHolder {
        return BranchStateViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.recycler_item_state_event,parent,false),this.mStateRecyclerviewInterface!!)

    }

    override fun onBindViewHolder(holder: BranchStateViewHolder, position: Int) {
        Log.d(TAG,"RecyclerAdapter - BranchStateViewHolder() called / position : $position")
        holder.bind(this.m_modelList[position])

        holder.itemView.setOnClickListener {

            (context as ActivityEventMain).StateEventClicked(position, this.m_modelList[position])
            //Toast.makeText(it.context, "Clicked: ${m_modelList.get(position)}", Toast.LENGTH_SHORT).show()

            //새 액티비티를 열고 웹뷰를 이용해서 상세보기 페이지를 보여 준다.
        }
    }

    override fun getItemCount(): Int {
        return m_modelList.size
    }
}