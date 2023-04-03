package com.livechat.view.main

import android.content.Intent
import android.os.Bundle
import com.livechat.R
import com.livechat.base.BaseActivity
import com.livechat.databinding.ActivityMainBinding
import com.livechat.view.all_chats.AllChatsFragment
import com.livechat.view.contacts.ContactsFragment
import com.livechat.view.search.SearchActivity
import dagger.hilt.android.AndroidEntryPoint

/**
 * User: Quang Phúc
 * Date: 2023-03-22
 * Time: 10:55 PM
 */
@AndroidEntryPoint
class MainActivity : BaseActivity() {

    private lateinit var binding: ActivityMainBinding

    private var allChatsFragment: AllChatsFragment? = null
    private var contactsFragment: ContactsFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupView()
    }

    override fun initView() {
        if (allChatsFragment == null) {
            allChatsFragment = AllChatsFragment.newInstance()
        }
        allChatsFragment?.let {
            supportFragmentManager.beginTransaction().replace(R.id.fragment, it).commit()
        }
    }

    override fun handleListener() {
        binding.tvSearch.setOnClickListener {
            val intent = Intent(this, SearchActivity::class.java)
            startActivity(intent)
        }

        binding.bottomNavigationView.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {

                R.id.item_all_chats -> {
                    if (allChatsFragment == null) {
                        allChatsFragment = AllChatsFragment.newInstance()
                    }
                    allChatsFragment?.let {
                        supportFragmentManager.beginTransaction()
                            .replace(R.id.fragment, it)
                            .commit()
                    }
                }

                R.id.item_contacts -> {
                    if (contactsFragment == null) {
                        contactsFragment = ContactsFragment.newInstance()
                    }
                    contactsFragment?.let {
                        supportFragmentManager.beginTransaction()
                            .replace(R.id.fragment, it)
                            .commit()
                    }
                }
            }

            true
        }
    }

    override fun observeViewModel() {

    }
}