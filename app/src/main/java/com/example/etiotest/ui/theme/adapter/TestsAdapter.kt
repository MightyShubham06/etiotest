package com.example.etiotest.ui.theme.adapter

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
// Import the correct consolidated model
import com.example.etiotest.data.LabTests
import com.example.etiotest.databinding.ItemTestBinding

class TestsAdapter(
    private var list: List<LabTests>, // Changed to LabTests
    private val onBookNow: (LabTests) -> Unit // Changed to LabTests
) : RecyclerView.Adapter<TestsAdapter.TestViewHolder>()
{

    inner class TestViewHolder(val binding: ItemTestBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(test: LabTests) {
            binding.tvTestName.text = test.name
            binding.tvDescription.text = test.description

            // Pricing Logic
            binding.tvDiscountedPrice.text = "₹${test.displayPrice}"
            binding.tvOriginalPrice.text = "₹${test.originalPrice}"

            // Apply Strikethrough
            binding.tvOriginalPrice.paintFlags = binding.tvOriginalPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG

            // Load Image
            Glide.with(binding.imgTest.context)
                .load(test.image)
                .placeholder(android.R.color.darker_gray)
                .into(binding.imgTest)

            binding.btnBookNow.setOnClickListener {
                onBookNow(test)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TestViewHolder {
        val binding = ItemTestBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TestViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TestViewHolder, position: Int) = holder.bind(list[position])

    override fun getItemCount(): Int = list.size

    fun updateList(newList: List<LabTests>) { // Changed to LabTests
        list = newList
        notifyDataSetChanged()
    }
}