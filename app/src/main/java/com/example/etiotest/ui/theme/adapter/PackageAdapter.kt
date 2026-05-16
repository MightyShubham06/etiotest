package com.example.etiotest.ui.theme.adapter

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.recyclerview.widget.RecyclerView
import com.example.etiotest.R
import com.example.etiotest.data.model.HealthPackage

class PackageAdapter(private val list: List<HealthPackage>,
                     private val onBookClick: (HealthPackage) -> Unit) :
    RecyclerView.Adapter<PackageAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.tvTitle)
        val oldPrice: TextView = view.findViewById(R.id.tvOldPrice)
        val newPrice: TextView = view.findViewById(R.id.tvNewPrice)
        val desc: TextView = view.findViewById(R.id.tvDesc)
        val btnBook: AppCompatButton = view.findViewById(R.id.btnBook)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_health_package, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        holder.title.text = item.title
        holder.oldPrice.text = item.oldPrice
        holder.newPrice.text = item.newPrice
        holder.desc.text = item.desc

        holder.btnBook.setOnClickListener {
            onBookClick(item)
        }

        holder.oldPrice.paintFlags = holder.oldPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
    }

    override fun getItemCount() = list.size
}