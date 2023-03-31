package com.livechat.view.all_chats

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.auth.FirebaseAuth
import com.livechat.R
import com.livechat.base.BaseFragment
import com.livechat.databinding.FragmentAllChatsBinding
import com.livechat.view.splash.SplashActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * User: Quang Phúc
 * Date: 2023-03-31
 * Time: 11:35 PM
 */
@AndroidEntryPoint
class AllChatsFragment : BaseFragment(R.layout.fragment_all_chats) {

    companion object {
        fun newInstance(): AllChatsFragment {
            return AllChatsFragment()
        }
    }

    private lateinit var binding: FragmentAllChatsBinding

    @Inject
    lateinit var firebaseAuth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAllChatsBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
    }

    override fun initView() {
        binding.tvEmail.text = firebaseAuth.currentUser?.email
    }

    override fun handleListener() {
        binding.tvSignOut.setOnClickListener {
            if (context == null) {
                return@setOnClickListener
            }

            firebaseAuth.signOut()

            val intent = Intent(context, SplashActivity::class.java)
            startActivity(intent)
            activity?.finish()
        }
    }

    override fun observeViewModel() {

    }
}