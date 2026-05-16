package com.example.etiotest.ui.theme.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.etiotest.data.model.PatientItem
import com.example.etiotest.databinding.ItemPatientBinding

class PatientAdapter(
    private val onSelect: (PatientItem) -> Unit
) : ListAdapter<PatientItem, PatientAdapter.ViewHolder>(DIFF) {

    private var selectedId: String? = null

    inner class ViewHolder(val binding: ItemPatientBinding) :
        RecyclerView.ViewHolder(binding.root)

    // ✅ FIXED HERE
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
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

            tvName.text = item.name
            tvDetails.text = "${item.age}, ${item.gender}"

            // ✅ selection UI
            radioButton.isChecked = item.id == selectedId

            root.setOnClickListener {
                selectedId = item.id
                notifyDataSetChanged() // refresh selection
                onSelect(item)
            }
        }
    }

    companion object {
        val DIFF = object : DiffUtil.ItemCallback<PatientItem>() {
            override fun areItemsTheSame(o: PatientItem, n: PatientItem) =
                o.id == n.id

            override fun areContentsTheSame(o: PatientItem, n: PatientItem) =
                o == n
        }
    }
}