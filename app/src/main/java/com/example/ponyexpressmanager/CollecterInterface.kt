package com.example.ponyexpressmanager

interface EventRecyclerviewInterface
{
    fun onItemClicked(position : Int, a_evetData : CEvent)
}

interface StateRecyclerviewInterface
{
    fun onStateEventClicked(position : Int, aEventData : String)
}

interface CheckInterface
{
    fun onCheckClicked(position : Int)
}

