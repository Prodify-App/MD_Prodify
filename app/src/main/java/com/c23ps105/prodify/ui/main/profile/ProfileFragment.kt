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
import com.c23ps105.prodify.helper.ProductViewModelFactory
import com.c23ps105.prodify.helper.SessionPreferences
import com.c23ps105.prodify.ui.adapter.ProductAdapter
import com.c23ps105.prodify.ui.adapter.ResultAdapter
import com.c23ps105.prodify.ui.main.home.HomeFragment
import com.c23ps105.prodify.ui.viewModel.AuthViewModel
import com.c23ps105.prodify.ui.viewModel.ProductViewModel

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private lateinit var authViewModel: AuthViewModel
    private lateinit var productViewModel: ProductViewModel
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(layoutInflater)
        setViewModel()

        val productAdapter = ResultAdapter(
            onBookmarkClick = { bookmark ->
                if (bookmark.isBookmarked) {
                    productViewModel.unBookmarkProduct(bookmark)
                } else {
                    productViewModel.bookmarkProduct(bookmark)
                }
            },
            onProductClick = { product ->
                val mBundle = Bundle()
                mBundle.putInt(HomeFragment.EXTRA_ID, product.id)

                findNavController().enableOnBackPressed(true)
                findNavController().navigate(
                    R.id.action_navigation_home_to_navigation_detail_result, mBundle
                )
            }
        )

        productViewModel.getBookmarkedProduct().observe(viewLifecycleOwner) {
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
            requireActivity().finish()
        }

        val pFactory = ProductViewModelFactory.getInstance(requireContext(), pref)
        productViewModel = viewModels<ProductViewModel> { pFactory }.value
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}