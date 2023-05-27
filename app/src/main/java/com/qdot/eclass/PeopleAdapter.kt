package com.qdot.eclass

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.badge.BadgeDrawable
import com.google.android.material.badge.BadgeUtils
import com.qdot.eclass.databinding.PeopleLayoutBinding

class PeopleAdapter(private val peopleList : List<People>):RecyclerView.Adapter<PeopleAdapter.ViewHolder>() {
    inner class ViewHolder(val binding: PeopleLayoutBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = PeopleLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return peopleList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder) {
            with(peopleList[position]) {
                binding.userNameView.text = this.name
                binding.userEmailView.text = this.email
            }
        }
    }
}