package com.qdot.eclass

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.tabs.TabLayoutMediator
import com.qdot.eclass.databinding.ActivityClassBinding

class ClassActivity : AppCompatActivity() {
    private lateinit var binding: ActivityClassBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityClassBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.topAppBar.setNavigationOnClickListener {
            finish()
        }
        val classId = intent.getStringExtra(getString(R.string.class_id))
        val className = intent.getStringExtra(getString(R.string.class_name))
        binding.topAppBar.title = className.toString()
        val adapter = ViewPagerAdapter(this, 3,classId.toString())
        binding.viewPager.adapter = adapter
        TabLayoutMediator(binding.tabLayout,binding.viewPager){ tab, position ->
            when(position){
                0-> tab.text = "Announcement"
                1-> tab.text = "Assignment"
                2-> tab.text = "People"
            }
        }.attach()
    }
}