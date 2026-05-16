package com.example.etiotest.ui.theme.adapter

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.etiotest.R
import com.example.etiotest.data.LabTests
import com.example.etiotest.databinding.ItemHealthPackageBinding

class CartPackageAdapter(
    private val onRemoveClick: (LabTests) -> Unit,
    private val onBookClick: (LabTests) -> Unit
) : ListAdapter<LabTests, CartPackageAdapter.PackageViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PackageViewHolder {
        val binding = ItemHealthPackageBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return PackageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PackageViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class PackageViewHolder(private val binding: ItemHealthPackageBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(test: LabTests) {
            binding.apply {
                tvTitle.text = test.name
                tvNewPrice.text = "Rs. ${test.displayPrice}"
                tvOldPrice.text = "Rs. ${test.originalPrice}"

                tvOldPrice.paintFlags = tvOldPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG

                tvDesc.text = test.description

                Glide.with(itemView.context)
                    .load(test.image)
                    .placeholder(R.drawable.ic_launcher_background) // placeholder image
                    .into(packageImage)

                // Buttons setup
                btnRemove.setOnClickListener { onRemoveClick(test) }
                btnBook.setOnClickListener { onBookClick(test) }
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<LabTests>() {
        override fun areItemsTheSame(oldItem: LabTests, newItem: LabTests) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: LabTests, newItem: LabTests) = oldItem == newItem
    }
}