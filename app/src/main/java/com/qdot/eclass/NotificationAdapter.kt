package com.qdot.eclass

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.qdot.eclass.databinding.NotificationLayoutBinding
import com.qdot.eclass.room.NotificationModel
import java.text.DateFormat
import java.util.Date

class NotificationAdapter(private var nList: List<NotificationModel>): RecyclerView.Adapter<NotificationAdapter.ViewHolder>() {
    inner class ViewHolder(val binding: NotificationLayoutBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = NotificationLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return nList.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder){
            with(nList[position]){
                binding.classTitle.text = "Class : ${this.notiClass}"
                binding.notiDesc.text= this.notiDesc
                binding.notiTime.text = DateFormat.getDateTimeInstance().format(Date(this.notiTime))
                if (this.notiSeen){
                    binding.rootMaterialCard.isEnabled = false
                }
            }
        }
    }
}