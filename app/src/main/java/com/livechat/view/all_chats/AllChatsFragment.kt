package com.livechat.view.all_chats

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.livechat.R
import com.livechat.base.BaseFragment
import com.livechat.common.Constants
import com.livechat.common.CurrentUser
import com.livechat.databinding.FragmentAllChatsBinding
import com.livechat.extension.showToast
import com.livechat.extension.toJson
import com.livechat.model.ChatModel
import com.livechat.view.chat.ChatActivity
import com.livechat.view.splash.SplashActivity
import dagger.hilt.android.AndroidEntryPoint

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

    private lateinit var viewModel: AllChatsViewModel
    private lateinit var binding: FragmentAllChatsBinding

    private var adapter: AllChatsAdapter? = null

    private var chats: ArrayList<ChatModel> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this)[AllChatsViewModel::class.java]
        binding = FragmentAllChatsBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()

        viewModel.startChatsListener()
    }

    override fun initView() {
        binding.tvEmail.text = CurrentUser.email
    }

    override fun handleListener() {
        binding.tvSignOut.setOnClickListener {
            if (context == null) {
                return@setOnClickListener
            }

            viewModel.signOut()
        }
    }

    override fun observeViewModel() {
        viewModel.state.observe(this) {
            when (it.status) {
                AllChatsState.Status.LOADING -> {

                }

                AllChatsState.Status.UPDATE_CHATS -> {
                    if (context == null) {
                        return@observe
                    }

                    chats = it.data as ArrayList<ChatModel>
                    adapter = AllChatsAdapter(requireContext(), chats) { chatModel, position ->
                        val intent = Intent(context, ChatActivity::class.java)
                        intent.putExtra(Constants.CHAT_MODEL, chatModel.toJson())
                        startActivity(intent)
                    }
                    binding.rvAllChats.adapter = adapter
                }

                AllChatsState.Status.SIGN_OUT_SUCCESS -> {
                    val intent = Intent(context, SplashActivity::class.java)
                    startActivity(intent)
                    activity?.finish()
                }

                AllChatsState.Status.SIGN_OUT_ERROR -> {
                    context?.showToast("Sign out error")
                }
            }
        }
    }
}