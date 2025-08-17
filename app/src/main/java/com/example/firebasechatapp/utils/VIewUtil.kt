package com.example.firebasechatapp.utils

import android.content.Context
import android.graphics.Color
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar

object ViewUtil {

    // Hide keyboard
    fun Fragment.hideKeyboard() {
        view?.let { v ->
            val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(v.windowToken, 0)
        }
    }

    // Show error Snackbar
    fun Fragment.showError(message: String) {
        view?.let { v ->
            Snackbar.make(v, message, Snackbar.LENGTH_LONG)
                .setBackgroundTint(ContextCompat.getColor(v.context, android.R.color.holo_red_light))
                .setTextColor(Color.WHITE)
                .show()
        }
    }

    // Show success Snackbar
    fun Fragment.showSuccess(message: String) {
        view?.let { v ->
            Snackbar.make(v, message, Snackbar.LENGTH_SHORT)
                .setBackgroundTint(ContextCompat.getColor(v.context, android.R.color.holo_green_dark))
                .setTextColor(Color.WHITE)
                .show()
        }
    }

    // Show confirmation dialog
    fun showConfirmationDialog(
        context: Context,
        title: String,
        message: String,
        onConfirm: () -> Unit
    ) {
        MaterialAlertDialogBuilder(context)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("Yes") { dialog, _ ->
                dialog.dismiss()
                onConfirm()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }
}
