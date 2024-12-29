//package com.example.finalproject
//
//import androidx.fragment.app.Fragment
//import androidx.viewpager2.adapter.FragmentStateAdapter
//
//class ViewPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
//    private val fragments = mutableListOf<Fragment>()
//    private val titles = mutableListOf<String>()
//
//    fun addFragment(fragment: Fragment, title: String) {
//        fragments.add(fragment)
//        titles.add(title)
//    }
//
//    override fun getItemCount() = fragments.size
//
//    override fun createFragment(position: Int): Fragment = fragments[position]
//
//    fun getTitle(position: Int): String = titles[position]
//}
