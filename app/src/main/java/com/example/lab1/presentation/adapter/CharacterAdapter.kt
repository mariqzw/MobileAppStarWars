package com.example.lab1.presentation.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.lab1.database.CharacterEntity
import com.example.lab1.databinding.ListItemBinding

class CharacterAdapter(private var items: List<CharacterEntity>, private var fontSize: Float) :
    RecyclerView.Adapter<CharacterAdapter.CharacterResponseViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CharacterResponseViewHolder {
        val binding = ListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CharacterResponseViewHolder(binding, fontSize)
    }

    override fun onBindViewHolder(holder: CharacterResponseViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    @SuppressLint("NotifyDataSetChanged")
    fun setData(newData: List<CharacterEntity>) {
        this.items = newData
        notifyDataSetChanged()
    }

    fun setFontSize(size: Float) {
        this.fontSize = size
        notifyDataSetChanged()
    }

    class CharacterResponseViewHolder(private val binding: ListItemBinding, private val fontSize: Float) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(character: CharacterEntity) {
            with(binding) {
                nameTextView.text = character.name ?: "-"
                heightTextView.text = character.height?.toString() ?: "-"
                massTextView.text = character.mass?.toString() ?: "-"
                hairColorTextView.text = character.hair_color ?: "-"
                eyeColorTextView.text = character.eye_color ?: "-"
                genderTextView.text = character.gender ?: "-"
                homeworldNameTextView.text = character.homeworld ?: "-"

                val textViews = listOf(
                    nameTextView,
                    heightTextView,
                    massTextView,
                    hairColorTextView,
                    eyeColorTextView,
                    genderTextView,
                    homeworldNameTextView
                )

                textViews.forEach { textView -> textView.textSize = fontSize }
            }
        }
    }
}
