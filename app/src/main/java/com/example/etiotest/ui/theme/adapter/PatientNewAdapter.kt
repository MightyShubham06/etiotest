package com.example.etiotest.ui.theme.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.etiotest.data.model.PatientNew
import com.example.etiotest.databinding.ItemPatientBinding

class PatientNewAdapter(
    private val onSelect: (PatientNew) -> Unit
) : RecyclerView.Adapter<PatientNewAdapter.ViewHolder>() {

    private var patients: List<PatientNew> = emptyList()

    inner class ViewHolder(private val binding: ItemPatientBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(patient: PatientNew) {
            binding.tvName.text = patient.name
            binding.root.setOnClickListener {
                onSelect(patient)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemPatientBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(patients[position])
    }

    override fun getItemCount() = patients.size

    fun updateList(newList: List<PatientNew>) {
        patients = newList
        notifyDataSetChanged()
    }
}