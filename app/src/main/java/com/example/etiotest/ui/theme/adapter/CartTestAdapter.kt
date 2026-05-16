package com.example.etiotest.ui.theme.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.etiotest.data.LabTests
import com.example.etiotest.databinding.ItemCartTestBinding

class CartTestAdapter(
    private val onRemoveClick: (LabTests) -> Unit
) : ListAdapter<LabTests, CartTestAdapter.CartViewHolder>(CartDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val binding = ItemCartTestBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return CartViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class CartViewHolder(private val binding: ItemCartTestBinding) :
        RecyclerView.ViewHolder(binding.root) {
        
        fun bind(test: LabTests) {
            binding.apply {
                tvTestName.text = test.name
                tvPrice.text = "Rs. ${test.displayPrice}"
                // Use a fallback description if your model doesn't have one yet
                tvDescription.text = "A routine blood test that helps monitor your overall health and detect potential issues early."
                
                btnRemove.setOnClickListener {
                    onRemoveClick(test)
                }
            }
        }
    }

    class CartDiffCallback : DiffUtil.ItemCallback<LabTests>() {
        override fun areItemsTheSame(oldItem: LabTests, newItem: LabTests): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: LabTests, newItem: LabTests): Boolean {
            return oldItem == newItem
        }
    }
}