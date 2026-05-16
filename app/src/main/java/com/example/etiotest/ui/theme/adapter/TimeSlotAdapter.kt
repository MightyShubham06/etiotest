package com.example.etiotest.ui.theme.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.etiotest.databinding.ItemTimeSlotBinding

class TimeSlotAdapter(private val slots: List<String>) :
    RecyclerView.Adapter<TimeSlotAdapter.SlotViewHolder>() {

    class SlotViewHolder(val binding: ItemTimeSlotBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SlotViewHolder {
        val binding = ItemTimeSlotBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SlotViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SlotViewHolder, position: Int) {
        holder.binding.tvSlot.text = slots[position]
        holder.itemView.setOnClickListener { /* Handle slot selection */ }
    }

    override fun getItemCount() = slots.size
}