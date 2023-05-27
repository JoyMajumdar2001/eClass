package com.qdot.eclass

import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.animation.AnimationUtils.lerp
import com.qdot.eclass.databinding.ColClassLayoutBinding
import io.appwrite.models.Team

class CarouselAdapter(private var teamList: List<Team>): RecyclerView.Adapter<CarouselAdapter.ViewHolder>() {
    inner class ViewHolder(val binding: ColClassLayoutBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ColClassLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return teamList.size
    }

    @SuppressLint("SetTextI18n", "RestrictedApi")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder){
            with(teamList[position]){
                binding.titleText.text = this.name
                binding.root.setOnMaskChangedListener {
                    binding.titleText.translationX = it.left
                    binding.titleText.alpha = lerp(1F, 0F, 0F, 80F, it.left)
                }
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