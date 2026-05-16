package com.example.etiotest.ui.theme.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.etiotest.data.LabTests
import com.example.etiotest.data.test.CartItem
import com.example.etiotest.databinding.ItemCartBinding

class CartAdapter(
    private val onRemove: (LabTests) -> Unit,
    private val onBookNow: (LabTests) -> Unit
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {
    private var items: List<LabTests> = emptyList()

    fun submitList(newItems: List<LabTests>) {
        this.items = newItems
        notifyDataSetChanged()
    }

    inner class CartViewHolder(val binding: ItemCartBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(cartItem: LabTests) {
            binding.tvTestName.text = cartItem.name
            binding.tvTestPrice.text = "₹ ${cartItem.originalPrice}"
//            binding.tvQuantity.text = "Qty: ${cartItem.}"
            // You can add remove button etc.
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val binding = ItemCartBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CartViewHolder(binding)
        fun bind(cartItem: LabTests) {
            binding.tvTestName.text = cartItem.name
            binding.tvTestPrice.text = "₹ ${cartItem.originalPrice}"

            // Wire up the remove button
            binding.btnRemove.setOnClickListener {
                onRemove(cartItem) // This triggers the ViewModel function you passed
            }
        }
    }


    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        holder.bind(items[position])

    }

    override fun getItemCount(): Int = items.size

    fun updateList(newItems: List<LabTests>) {
        items = newItems
        notifyDataSetChanged()
    }
}
