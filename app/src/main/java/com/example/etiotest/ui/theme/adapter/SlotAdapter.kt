package com.example.etiotest.ui.theme.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.etiotest.R

class SlotAdapter(
    private val onClick: (String) -> Unit
) : RecyclerView.Adapter<SlotAdapter.ViewHolder>() {

    private var list = listOf<String>()
    private var selected = -1

    fun setData(data: List<String>) {
        list = data
        notifyDataSetChanged()
    }

    inner class ViewHolder(val tv: TextView) : RecyclerView.ViewHolder(tv)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val tv = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_slot, parent, false) as TextView
        return ViewHolder(tv)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val slot = list[position]
        holder.tv.text = slot

        holder.tv.setBackgroundResource(
            if (selected == position) R.drawable.bg_selected_slot
            else R.drawable.bg_outline_gray_box
        )

        holder.tv.setOnClickListener {
            selected = position
            notifyDataSetChanged()
            onClick(slot)
        }
    }

    override fun getItemCount() = list.size
}