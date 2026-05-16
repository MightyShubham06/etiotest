package com.example.etiotest.ui.theme.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.etiotest.data.model.OrderItem
import com.example.etiotest.databinding.ItemOrderBinding

class OrdersAdapter(private val onItemClick: (OrderItem) -> Unit) :
    ListAdapter<OrderItem, OrdersAdapter.ViewHolder>(DIFF) {

    inner class ViewHolder(val binding: ItemOrderBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemOrderBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)

        holder.itemView.setOnClickListener { onItemClick(item) }

        with(holder.binding) {
            tvOrderId.text = item.orderNumber
            tvPatient.text = item.patient.name
            tvAddress.text = "${item.address.line1}, ${item.address.city}"
            tvPrice.text = "₹${item.pricing.total}"
            tvStatus.text = item.status
            tvDate.text = item.bookingDate.substring(0, 10)
            tvTestName.text = item.tests.firstOrNull()?.name ?: "-"
        }
    }

    companion object {
        val DIFF = object : DiffUtil.ItemCallback<OrderItem>() {
            override fun areItemsTheSame(o: OrderItem, n: OrderItem) = o._id == n._id
            override fun areContentsTheSame(o: OrderItem, n: OrderItem) = o == n
        }
    }
}