package com.livechat.view.choose_media.fragment

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

/**
 * User: Quang Phúc
 * Date: 2023-05-02
 * Time: 10:38 AM
 */
class ChooseMediaResultAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    private val fragments = ArrayList<Fragment>()

    fun addFragment(fragment: Fragment) {
        fragments.add(fragment)
    }

    override fun getItemCount(): Int {
        return fragments.size
    }

    override fun createFragment(position: Int): Fragment {
        return fragments[position]
    }
}