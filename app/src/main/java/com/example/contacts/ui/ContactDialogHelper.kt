package com.example.contacts.ui

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.widget.ArrayAdapter
import androidx.core.widget.addTextChangedListener
import com.example.contacts.R
import com.example.contacts.data.PhoneNumberType
import com.example.contacts.databinding.AddContactDialogBinding
import com.example.contacts.databinding.EditContactDialogBinding

object ContactDialogHelper {
    fun showCreateContactDialog(
        context: Context,
        callback: (firstName: String, lastName: String, phoneNumber: String, type: PhoneNumberType) -> Unit
    ) {
        val dialogBinding = AddContactDialogBinding.inflate(LayoutInflater.from(context))
        ArrayAdapter.createFromResource(
            context,
            R.array.phone_number_types_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            dialogBinding.typeDropdown.adapter = adapter
        }

        val dialog = AlertDialog.Builder(context)
            .setView(dialogBinding.root)
            .setTitle("Add Contact")
            .setPositiveButton("Add", null)
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .create()

        dialog.show()

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {

            val firstName = dialogBinding.firstName.text.toString().trim()
            val lastName = dialogBinding.lastName.text.toString().trim()
            val phoneNumber = dialogBinding.phoneNumber.text.toString().trim()
            val type = when (dialogBinding.typeDropdown.selectedItemPosition) {
                0 -> PhoneNumberType.HOME
                1 -> PhoneNumberType.MOBILE
                2 -> PhoneNumberType.WORK
                3 -> PhoneNumberType.MAIN
                else -> PhoneNumberType.OTHER
            }

            if (newContactValid(dialogBinding)) {
                callback(firstName, lastName, phoneNumber, type)
                dialog.dismiss()
            } else {
                dialogBinding.firstNameLayout.error =
                    if (firstName.isBlank()) "First name cannot be empty" else null
                dialogBinding.phoneNumberLayout.error =
                    if (phoneNumber.isBlank()) "Phone number cannot be empty"
                    else if (!isValidPhoneNumber(phoneNumber)) "Not a valid phone number"
                    else null
            }
        }

        // resets the error on first name label to null after typing
        dialogBinding.firstName.addTextChangedListener {
            dialogBinding.firstNameLayout.error = null
        }

        dialogBinding.phoneNumber.addTextChangedListener {
            dialogBinding.phoneNumberLayout.error = null
        }
    }


    fun showEditDialog(
        context: Context,
        oldFirstName: String,
        oldLastName: String?,
        callback: (firstName: String, lastName: String) -> Unit
    ) {
        val dialogBinding = EditContactDialogBinding.inflate(LayoutInflater.from(context))

        dialogBinding.firstName.setText(oldFirstName)
        dialogBinding.lastName.setText(oldLastName)


        val dialog = AlertDialog.Builder(context)
            .setView(dialogBinding.root)
            .setTitle("Edit Contact")
            .setPositiveButton("OK", null)
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .create()

        dialog.show()

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            val firstName = dialogBinding.firstName.text.toString().trim()
            val lastName = dialogBinding.lastName.text.toString().trim()

            if (firstName.isNotBlank()) {
                callback(firstName, lastName)
                dialog.dismiss()
            } else {
                dialogBinding.firstNameLayout.error = "First name cannot be empty"
            }
        }

        dialogBinding.firstName.addTextChangedListener {
            dialogBinding.firstNameLayout.error = null
        }

    }

    fun showDeleteAlertDialog(context: Context, onPositiveAction: () -> Unit) {
        val builder: AlertDialog.Builder = AlertDialog.Builder(context)
        builder
            .setMessage(context.getString(R.string.delete_message))
            .setPositiveButton("Yes") { dialog, _ ->
                onPositiveAction()
                dialog.dismiss()
            }
            .setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }

        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    private fun newContactValid(dialogBinding: AddContactDialogBinding): Boolean {
        val firstName = dialogBinding.firstName.text
        val phoneNumber = dialogBinding.phoneNumber.text
        return firstName?.isNotBlank() ?: false &&
                phoneNumber?.isNotBlank() ?: false && isValidPhoneNumber(phoneNumber.toString())
    }

    // Checks if number starts with plus, has only numbers 0-9 plus optional spacing and is between
    // 7 to 15 characters long
    private fun isValidPhoneNumber(phoneNumber: String): Boolean {
        val pattern = Regex("^\\+(?:[0-9] ?){6,14}[0-9]$")
        return pattern.matches(phoneNumber)
    }
}