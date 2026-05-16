package com.example.etiotest.ui.theme.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.etiotest.data.model.AddressItem
import com.example.etiotest.data.model.AddressResponse
import com.example.etiotest.databinding.ItemAddressBinding

class AddressAdapter(
    private val onEditClick: (AddressItem) -> Unit,
    private val onDeleteClick: (AddressItem) -> Unit,
    private val onSelectClick: (AddressItem) -> Unit
) : ListAdapter<AddressItem, AddressAdapter.AddressViewHolder>(DIFF_CALLBACK) {

    inner class AddressViewHolder(val binding: ItemAddressBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddressViewHolder {
        val binding = ItemAddressBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return AddressViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AddressViewHolder, position: Int) {

        val address = getItem(position)

        with(holder.binding) {

            tvAddressName.text = address.name

            chipPrimary.visibility = if (address.isDefault) {
                android.view.View.VISIBLE
            } else {
                android.view.View.GONE
            }

            tvFullAddress.text = buildFullAddress(address)

            tvLandmark.text = address.landmark ?: "No landmark"

            tvPhone.text = address.phone

            tvLastUsed.text = "Recently used"

            btnEdit.setOnClickListener { onEditClick(address) }
            btnDelete.setOnClickListener { onDeleteClick(address) }
            btnSelectAddress.setOnClickListener { onSelectClick(address) }
        }
    }

    private fun buildFullAddress(address: AddressItem): String {
        return listOfNotNull(
            address.line1,
            address.line2,
            address.locality,
            address.city,
            address.state,
            address.pincode
        ).joinToString(", ")
    }

    companion object {

        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<AddressItem>() {

            override fun areItemsTheSame(
                oldItem: AddressItem,
                newItem: AddressItem
            ): Boolean = oldItem.id == newItem.id

            override fun areContentsTheSame(
                oldItem: AddressItem,
                newItem: AddressItem
            ): Boolean = oldItem == newItem
        }
    }
}