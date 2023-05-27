package com.qdot.eclass

import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.qdot.eclass.databinding.AssignmentLayoutBinding
import java.text.DateFormat
import java.util.Date

class AssignmentAdapter(private var teamList: List<AssignmentModel>): RecyclerView.Adapter<AssignmentAdapter.ViewHolder>() {
    inner class ViewHolder(val binding: AssignmentLayoutBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = AssignmentLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
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
                binding.fileName.text= this.fileId+".pdf"
                binding.innerCard.setOnClickListener {
                    val intent = Intent(binding.root.context,FileViewerActivity::class.java)
                    intent.putExtra(binding.root.context.getString(R.string.fileid),this.fileId)
                    intent.putExtra(binding.root.context.getString(R.string.subDate),this.submissionDate)
                    binding.root.context.startActivity(intent)
                }
            }
        }
    }
}