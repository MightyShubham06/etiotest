package com.example.etiotest.ui.theme

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.widget.TextView
import com.example.etiotest.R

class LoaderDialog(context: Context) {

    private val dialog = Dialog(context)

    init {

        val view = LayoutInflater.from(context)
            .inflate(R.layout.dialog_loader, null)

        dialog.setContentView(view)

        dialog.window?.setBackgroundDrawable(
            ColorDrawable(Color.TRANSPARENT)
        )

        dialog.setCancelable(false)
    }

    fun show(message: String = "Please wait...") {

        val tvLoading =
            dialog.findViewById<TextView>(R.id.tvLoading)

        tvLoading.text = message

        if (!dialog.isShowing) {
            dialog.show()
        }
    }

    fun dismiss() {

        if (dialog.isShowing) {
            dialog.dismiss()
        }
    }
}