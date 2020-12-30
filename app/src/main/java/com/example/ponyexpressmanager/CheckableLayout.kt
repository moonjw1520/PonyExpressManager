package com.example.ponyexpressmanager

import android.content.Context
import android.util.AttributeSet
import android.widget.Checkable
import android.widget.LinearLayout
import kotlinx.android.synthetic.main.list_item_deliveryman.view.*

class CheckableLayout(context: Context, attributeSet: AttributeSet) : LinearLayout(context,attributeSet), Checkable
{
    override fun isChecked(): Boolean {

        return checkBox.isChecked
    }

    override fun toggle() {
        isChecked = !checkBox.isChecked
    }

    override fun setChecked(checked: Boolean) {
        if (checkBox.isChecked != checked) {
            checkBox.isChecked = checked
        }
    }
}