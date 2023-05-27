package com.qdot.eclass

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.qdot.eclass.databinding.AnnouncementLayoutBinding
import java.text.DateFormat
import java.util.Date

class AnnouncementAdapter(private var teamList: List<AnnouncementModel>): RecyclerView.Adapter<AnnouncementAdapter.ViewHolder>() {
    inner class ViewHolder(val binding: AnnouncementLayoutBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = AnnouncementLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return teamList.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder){
            with(teamList[position]){
                binding.aTitle.text = this.title
                binding.aDesc.text = this.desc
                binding.aDate.text = DateFormat.getDateTimeInstance().format(Date(this.time))
            }
        }
    }
}