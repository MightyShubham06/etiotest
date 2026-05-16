package com.example.etiotest.ui.theme.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.etiotest.data.LabTests
import com.example.etiotest.databinding.ItemRecommendedTestBinding

class RecommendedAdapter(
    private val onAddClick: (LabTests) -> Unit
) : ListAdapter<LabTests, RecommendedAdapter.RecommendedViewHolder>(RecommendedDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecommendedViewHolder {
        val binding = ItemRecommendedTestBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return RecommendedViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecommendedViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class RecommendedViewHolder(private val binding: ItemRecommendedTestBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(test: LabTests) {
            binding.tvRecTestName.text = test.name
            binding.tvRecPrice.text = "Rs. ${test.displayPrice}"

            binding.btnAddTest.setOnClickListener {
                onAddClick(test)
            }
        }
    }

    class RecommendedDiffCallback : DiffUtil.ItemCallback<LabTests>() {
        override fun areItemsTheSame(oldItem: LabTests, newItem: LabTests): Boolean = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: LabTests, newItem: LabTests): Boolean = oldItem == newItem
    }
}