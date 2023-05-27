package com.qdot.eclass

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class ViewPagerAdapter(fragmentActivity: FragmentActivity, private var totalCount: Int, private var classId: String) :
    FragmentStateAdapter(fragmentActivity){
    override fun getItemCount(): Int {
        return totalCount
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> AnnouncementFragment(classId)
            1 -> AssignmentFragment(classId)
            2 -> PeopleFragment(classId)
            else -> AnnouncementFragment(classId)
        }

    }
}