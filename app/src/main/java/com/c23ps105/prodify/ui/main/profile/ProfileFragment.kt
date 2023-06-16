package com.c23ps105.prodify.ui.main.profile

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.c23ps105.prodify.R
import com.c23ps105.prodify.databinding.FragmentProfileBinding
import com.c23ps105.prodify.helper.AuthViewModelFactory
import com.c23ps105.prodify.helper.MainViewModelFactory
import com.c23ps105.prodify.helper.SessionPreferences
import com.c23ps105.prodify.ui.adapter.ProductAdapter
import com.c23ps105.prodify.ui.adapter.ResultAdapter
import com.c23ps105.prodify.ui.main.detail.DetailResultFragment
import com.c23ps105.prodify.ui.viewModel.AuthViewModel
import com.c23ps105.prodify.ui.viewModel.MainViewModel

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private lateinit var authViewModel: AuthViewModel
    private lateinit var mainViewModel: MainViewModel
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentProfileBinding.inflate(layoutInflater)
        setViewModel()

        val productAdapter = ProductAdapter(
            onProductClick = { product ->
                val mBundle = Bundle().also {
                    it.putString(EXTRA_STATE, PRODUCT_STATE)
                    it.putInt(EXTRA_ID, product.id)
                    it.putParcelable(DetailResultFragment.NEWS_DATA, product)
                }
                findNavController().enableOnBackPressed(true)
                findNavController().navigate(
                    R.id.action_navigation_profile_to_navigation_detail_result, mBundle
                )
            }
        )

        mainViewModel.getBookmarkedProduct().observe(viewLifecycleOwner) {
            binding.progressBar.visibility = View.GONE
            productAdapter.submitList(it)
        }

        binding.rvResult.apply {
            setHasFixedSize(true)
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = productAdapter
        }
        return binding.root
    }

    private fun setViewModel() {
        val pref = SessionPreferences.getInstance(requireContext().dataStore)
        val factory = AuthViewModelFactory.getInstance(pref)
        authViewModel = viewModels<AuthViewModel> { factory }.value

        binding.icLogout.setOnClickListener {
            authViewModel.deleteSettings()
            findNavController().navigate(R.id.action_navigation_profile_to_authActivity2)
            requireActivity().finish()
        }

        authViewModel.getPreferences().observe(viewLifecycleOwner) {
            binding.username.text =
                getString(R.string.prodify).replace("Prodify", it.username)
            binding.email.text =
                getString(R.string.email_prodify).replace("Prodiy@example.com", it.email)
        }

        val pFactory = MainViewModelFactory.getInstance(requireContext(), pref)
        mainViewModel = viewModels<MainViewModel> { pFactory }.value
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val EXTRA_ID = "id"
        private const val PRODUCT_STATE = "product"
        private const val EXTRA_STATE = "state"

    }
}