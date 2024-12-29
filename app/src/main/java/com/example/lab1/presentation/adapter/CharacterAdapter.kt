package com.example.lab1.presentation.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.lab1.databinding.ListItemBinding
import com.example.lab1.models.Character

class CharacterAdapter(private var items: List<Character>, private var fontSize: Float) :
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
    fun setData(newData: List<Character>) {
        this.items = newData
        notifyDataSetChanged()
    }

    fun setFontSize(size: Float) {
        this.fontSize = size
        notifyDataSetChanged()
    }

    class CharacterResponseViewHolder(private val binding: ListItemBinding, private val fontSize: Float) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(character: Character) {
            with(binding) {
                nameTextView.text = character.name ?: "-"
                heightTextView.text = character.height?.toString() ?: "-"
                massTextView.text = character.mass?.toString() ?: "-"
                hairColorTextView.text = character.hair_color ?: "-"
                eyeColorTextView.text = character.eye_color ?: "-"
                genderTextView.text = character.gender ?: "-"
                homeworldNameTextView.text = character.homeworld ?: "-"

                nameTextView.textSize = fontSize
                heightTextView.textSize = fontSize
                massTextView.textSize = fontSize
                hairColorTextView.textSize = fontSize
                eyeColorTextView.textSize = fontSize
                genderTextView.textSize = fontSize
                homeworldNameTextView.textSize = fontSize
            }
        }
    }
}

