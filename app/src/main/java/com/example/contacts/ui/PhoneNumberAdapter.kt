package com.example.contacts.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.contacts.data.PhoneNumber
import com.example.contacts.databinding.PhoneNumberItemBinding

class PhoneNumberAdapter : RecyclerView.Adapter<PhoneNumberAdapter.PhoneNumberViewHolder>() {

    private var numbers: MutableList<PhoneNumber> = mutableListOf()
    var onNumberClick: ((number: PhoneNumber) -> Unit)? = null
    var onDeleteNumber: ((number: PhoneNumber) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhoneNumberViewHolder =
        PhoneNumberViewHolder(
            PhoneNumberItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: PhoneNumberViewHolder, position: Int) {
        val appInfo = numbers[position]
        holder.bind(appInfo)
    }

    override fun getItemCount(): Int {
        return numbers.size
    }

    inner class PhoneNumberViewHolder(private val binding: PhoneNumberItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: PhoneNumber) {
            binding.phoneNumber.text = item.number
            binding.phoneType.text = item.type.name.lowercase()

            binding.root.setOnClickListener {
                onNumberClick?.invoke(item)
            }
        }
    }

    fun deleteItem(position: Int) {
        onDeleteNumber?.invoke(numbers[position])
        numbers.removeAt(position)
        notifyItemRemoved(position)
    }

    fun updateList(items: List<PhoneNumber>) {
        val callback = PhoneNumberDiffCallback(this.numbers, items)
        val diffResult = DiffUtil.calculateDiff(callback)
        this.numbers.clear()
        this.numbers.addAll(items)
        diffResult.dispatchUpdatesTo(this)
    }

    class PhoneNumberDiffCallback(
        private val oldList: List<PhoneNumber>,
        private val newList: List<PhoneNumber>
    ) : DiffUtil.Callback() {

        override fun getOldListSize() = oldList.size

        override fun getNewListSize() = newList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition].id == newList[newItemPosition].id
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition] == newList[newItemPosition]
        }

    }

}
