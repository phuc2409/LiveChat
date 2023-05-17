package com.livechat.view.main

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import com.bumptech.glide.Glide
import com.livechat.R
import com.livechat.base.BaseActivity
import com.livechat.common.CurrentUser
import com.livechat.databinding.ActivityMainBinding
import com.livechat.extension.showToast
import com.livechat.view.all_chats.AllChatsFragment
import com.livechat.view.contacts.ContactsFragment
import com.livechat.view.search.SearchActivity
import com.livechat.view.settings.SettingsActivity
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

    /**
     * Fragment đang hiển thị
     */
    private var currentFragment = 0

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // FCM SDK (and your app) can post notifications.
            showToast("Can post notifications")
        } else {
            // TODO: Inform user that that your app will not show notifications.
            showToast("Can't post notifications")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupView()

//        askNotificationPermission()
    }

    override fun initView() {
        if (allChatsFragment == null) {
            allChatsFragment = AllChatsFragment.newInstance()
        }
        if (contactsFragment == null) {
            contactsFragment = ContactsFragment.newInstance()
        }

        allChatsFragment?.let {
            supportFragmentManager.beginTransaction().replace(R.id.fragment, it).commit()
        }
        contactsFragment?.let {
            supportFragmentManager.beginTransaction().add(R.id.fragment, it).hide(it).commit()
        }

        if (CurrentUser.avatarUrl.isNotBlank()) {
            Glide.with(this).load(CurrentUser.avatarUrl).into(binding.imgAvatar)
        }
        binding.tvName.text = CurrentUser.fullName
    }

    override fun handleListener() {
        binding.imgAvatar.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }

        binding.cvSearch.setOnClickListener {
            val intent = Intent(this, SearchActivity::class.java)
            startActivity(intent)
        }

        binding.bottomNavigationView.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {

                R.id.item_all_chats -> {
                    showAllChats()
                }

                R.id.item_contacts -> {
                    contactsFragment?.let {
                        supportFragmentManager.beginTransaction().show(it).commit()
                        currentFragment = 1
                    }
                }
            }

            true
        }
    }

    override fun observeViewModel() {

    }

    private fun showAllChats() {
        contactsFragment?.let {
            supportFragmentManager.beginTransaction().hide(it).commit()
            currentFragment = 0
        }
    }

    override fun onBackPressed() {
        if (currentFragment != 0) {
            showAllChats()
            binding.bottomNavigationView.selectedItemId = R.id.item_all_chats
        } else {
            super.onBackPressed()
        }
    }

    private fun askNotificationPermission() {
        // This is only necessary for API level >= 33 (TIRAMISU)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
                // FCM SDK (and your app) can post notifications.
            } else if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
                // TODO: display an educational UI explaining to the user the features that will be enabled
                //       by them granting the POST_NOTIFICATION permission. This UI should provide the user
                //       "OK" and "No thanks" buttons. If the user selects "OK," directly request the permission.
                //       If the user selects "No thanks," allow the user to continue without notifications.
            } else {
                // Directly ask for the permission
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }
}