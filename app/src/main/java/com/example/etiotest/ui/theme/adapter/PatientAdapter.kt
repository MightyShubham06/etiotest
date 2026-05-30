package com.example.etiotest.ui.theme.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.etiotest.R
import com.example.etiotest.data.model.PatientItem
import com.example.etiotest.databinding.ItemPatientBinding

class PatientAdapter(
    private val onSelect: (PatientItem) -> Unit
) : ListAdapter<PatientItem, PatientAdapter.ViewHolder>(DIFF) {

    private var selectedId: String? = null

    inner class ViewHolder(val binding: ItemPatientBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {

        val binding = ItemPatientBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val item = getItem(position)

        with(holder.binding) {

            // Name
            tvName.text = item.name

            // Details
            tvDetails.text =
                "${item.age} Years • ${item.gender.replaceFirstChar { it.uppercase() }}"

            // Phone
            tvPhone.text = item.phone ?: "No phone"

            // Relation / Tag
            tvRelation.text = "Self"

            // Selection
            val isSelected = item.id == selectedId

            radioButton.isChecked = isSelected

            // Professional Selection UI
            root.strokeWidth =
                if (isSelected) 3 else 1

            root.strokeColor =
                if (isSelected)
                    Color.parseColor("#007EA7")
                else
                    Color.parseColor("#E7EEF3")

            root.cardElevation =
                if (isSelected) 6f else 2f

            // Click
            root.setOnClickListener {

                val previousSelected = selectedId
                selectedId = item.id

                // Refresh only changed items
                previousSelected?.let { oldId ->
                    val oldIndex =
                        currentList.indexOfFirst { it.id == oldId }

                    if (oldIndex != -1)
                        notifyItemChanged(oldIndex)
                }

                notifyItemChanged(position)

                onSelect(item)
            }
        }
    }

    companion object {

        val DIFF = object : DiffUtil.ItemCallback<PatientItem>() {

            override fun areItemsTheSame(
                oldItem: PatientItem,
                newItem: PatientItem
            ): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: PatientItem,
                newItem: PatientItem
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}