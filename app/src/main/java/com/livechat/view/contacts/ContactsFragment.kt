package com.livechat.view.contacts

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.livechat.R
import com.livechat.base.BaseFragment
import com.livechat.databinding.FragmentContactsBinding
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

    private lateinit var binding: FragmentContactsBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentContactsBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
    }

    override fun initView() {

    }

    override fun handleListener() {

    }

    override fun observeViewModel() {

    }
}