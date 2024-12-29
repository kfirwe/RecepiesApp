package com.example.finalproject.ui.adapters


import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

/**
 * Adapter for managing fragments in a ViewPager2.
 */
class ViewPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    private val fragments = mutableListOf<Fragment>()
    private val titles = mutableListOf<String>()

    /**
     * Adds a fragment and its title to the adapter.
     *
     * @param fragment The fragment to add.
     * @param title The title associated with the fragment.
     */
    fun addFragment(fragment: Fragment, title: String) {
        fragments.add(fragment)
        titles.add(title)
    }

    override fun getItemCount(): Int = fragments.size

    override fun createFragment(position: Int): Fragment = fragments[position]

    /**
     * Gets the title of the fragment at a given position.
     *
     * @param position The position of the fragment.
     * @return The title of the fragment.
     */
    fun getTitle(position: Int): String = titles[position]
}
