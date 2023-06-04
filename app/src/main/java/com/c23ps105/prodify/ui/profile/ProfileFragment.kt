package com.c23ps105.prodify.ui.profile

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.c23ps105.prodify.data.SessionPreferences
import com.c23ps105.prodify.data.sample.CardData
import com.c23ps105.prodify.databinding.FragmentProfileBinding
import com.c23ps105.prodify.ui.MainActivity
import com.c23ps105.prodify.ui.adapter.ResultAdapter
import com.c23ps105.prodify.ui.auth.AuthActivity
import com.c23ps105.prodify.ui.viewModel.AuthViewModel
import com.c23ps105.prodify.ui.viewModel.AuthViewModelFactory

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private lateinit var authViewModel: AuthViewModel
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(layoutInflater)

        val pref = SessionPreferences.getInstance(requireContext().dataStore)
        val factory = AuthViewModelFactory.getInstance(pref)
        authViewModel = ViewModelProvider(this, factory)[AuthViewModel::class.java]

        val productAdapter = ResultAdapter {}
        productAdapter.submitList(CardData.listData)

        binding.rvResult.apply {
            setHasFixedSize(true)
            StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
            adapter = productAdapter
        }

        binding.icLogout.setOnClickListener {
            authViewModel.deleteSettings()
            requireActivity().finish()
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}