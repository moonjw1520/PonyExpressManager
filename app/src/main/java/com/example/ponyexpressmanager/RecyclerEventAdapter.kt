package com.example.ponyexpressmanager

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.ponyexpressmanager.CListCollector.TAG

class RecyclerEventAdapter(val m_modelList:ArrayList<CEvent>, var m_EventRecyclerviewInterface : EventRecyclerviewInterface) :
    RecyclerView.Adapter<BranchEventViewHolder>()
{

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BranchEventViewHolder {
        return BranchEventViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.recycler_item_event,parent,false),this.m_EventRecyclerviewInterface!!)
    }

    override fun getItemCount(): Int {
        return m_modelList.size
    }

    override fun onBindViewHolder(holder: BranchEventViewHolder, position: Int) {
        Log.d(TAG,"RecyclerAdapter - onBindViewHolder() called / position : $position")
        holder.bind(this.m_modelList[position])
    }


}