package com.livechat.view.contacts

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.livechat.R
import com.livechat.base.BaseFragment
import com.livechat.common.Constants
import com.livechat.databinding.FragmentContactsBinding
import com.livechat.extension.gone
import com.livechat.extension.toJson
import com.livechat.extension.visible
import com.livechat.model.ChatModel
import com.livechat.view.chat.ChatActivity
import com.livechat.view.search.SearchActivity
import dagger.hilt.android.AndroidEntryPoint

/**
 * User: Quang Phúc
 * Date: 2023-04-01
 * Time: 12:39 AM
 */
@AndroidEntryPoint
class ContactsFragment : BaseFragment(R.layout.fragment_contacts) {

    companion object {
        fun newInstance(): ContactsFragment {
            return ContactsFragment()
        }
    }

    private lateinit var viewModel: ContactsViewModel
    private lateinit var binding: FragmentContactsBinding

    private var contactsAdapter: ContactsAdapter? = null
    private var contacts = ArrayList<ChatModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this)[ContactsViewModel::class.java]
        binding = FragmentContactsBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
    }

    override fun initView() {
        getContacts()
    }

    override fun handleListener() {
        binding.refresh.setOnRefreshListener {
            getContacts()
        }

        binding.btnAddContact.setOnClickListener {
            val intent = Intent(context, SearchActivity::class.java)
            startActivity(intent)
        }
    }

    override fun observeViewModel() {
        viewModel.state.observe(this) {
            when (it.status) {

                ContactsState.Status.GET_CONTACTS_SUCCESS -> {
                    contacts = it.data as ArrayList<ChatModel>
                    binding.progressBar.gone()
                    binding.refresh.isRefreshing = false
                    if (contacts.isEmpty()) {
                        binding.imgEmpty.visible()
                    } else {
                        binding.recyclerView.visible()
                    }

                    if (context == null) {
                        return@observe
                    }

                    contactsAdapter = ContactsAdapter(
                        requireContext(),
                        contacts,
                        object : ContactsAdapter.Listener {

                            override fun onClick(chatModel: ChatModel, position: Int) {
                                val intent = Intent(context, ChatActivity::class.java)
                                intent.putExtra(Constants.CHAT_MODEL, chatModel.toJson())
                                startActivity(intent)
                            }
                        })
                    binding.recyclerView.adapter = contactsAdapter
                }

                ContactsState.Status.GET_CONTACTS_ERROR -> {

                }
            }
        }
    }

    private fun getContacts() {
        binding.recyclerView.gone()
        binding.imgEmpty.gone()
        binding.progressBar.visible()
        viewModel.getContacts()
    }
}
