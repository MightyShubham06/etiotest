package com.example.etiotest.ui.theme.main

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.*
import androidx.fragment.app.DialogFragment
import com.example.etiotest.R
import com.example.etiotest.data.model.PatientNew

class AddPatientDialog(
    private val onSave: (PatientNew) -> Unit
) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = LayoutInflater.from(requireContext())
            .inflate(R.layout.dialog_add_patient, null)

        val etName = view.findViewById<EditText>(R.id.etDialogName)
        val etPhone = view.findViewById<EditText>(R.id.etDialogPhone)
        val etAge = view.findViewById<EditText>(R.id.etDialogAge)
        val rgGender = view.findViewById<RadioGroup>(R.id.rgGender)
        val btnSave = view.findViewById<Button>(R.id.btnSavePatient)

        val dialog = Dialog(requireContext())
        dialog.setContentView(view)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        btnSave.setOnClickListener {
            val name = etName.text.toString().trim()
            val phone = etPhone.text.toString().trim()
            val age = etAge.text.toString().trim()
            val gender = if (rgGender.checkedRadioButtonId == R.id.rbMale) "Male" else "Female"

            if (name.isEmpty() || phone.isEmpty() || age.isEmpty()) {
                Toast.makeText(requireContext(), "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val newPatient = PatientNew(
                id = System.currentTimeMillis().toInt(),
                name = "Mr. $name",
                phone = phone,
                age = age,
                gender = gender
            )
            onSave(newPatient)
            dialog.dismiss()
        }

        return dialog
    }
}