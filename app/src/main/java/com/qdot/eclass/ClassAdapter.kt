package com.qdot.eclass

import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.qdot.eclass.databinding.ClassLayoutBinding
import io.appwrite.models.TeamList

class ClassAdapter(private var teamList: TeamList): RecyclerView.Adapter<ClassAdapter.ViewHolder>() {
    inner class ViewHolder(val binding: ClassLayoutBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ClassLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return teamList.teams.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder){
            with(teamList.teams[position]){
                binding.className.text = this.name
                binding.studentNo.text = (this.total-1).toString()+" Students"
                binding.root.setOnClickListener {
                    val intent = Intent(
                        binding.root.context,
                        ClassActivity::class.java
                    )
                    intent.putExtra(binding.root.context.getString(R.string.class_id),this.id)
                    intent.putExtra(binding.root.context.getString(R.string.class_name),this.name)
                    binding.root.context.startActivity(intent)
                }
            }
        }
    }
}