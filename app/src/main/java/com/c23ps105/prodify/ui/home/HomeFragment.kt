package com.c23ps105.prodify.ui.home

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.c23ps105.prodify.R
import com.c23ps105.prodify.data.SessionPreferences
import com.c23ps105.prodify.data.sample.CardData
import com.c23ps105.prodify.databinding.FragmentHomeBinding
import com.c23ps105.prodify.ui.adapter.BlogsAdapter
import com.c23ps105.prodify.ui.adapter.ProductAdapter
import com.c23ps105.prodify.ui.viewModel.ProductViewModel
import com.c23ps105.prodify.ui.viewModel.ViewModelFactory
import com.c23ps105.prodify.utils.Result

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val pref = SessionPreferences.getInstance(requireContext().dataStore)
        val factory = ViewModelFactory.getInstance(requireContext(), pref)
        val viewModel : ProductViewModel by activityViewModels { factory }
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        val productAdapter = ProductAdapter {
            view?.findNavController()
                ?.navigate(R.id.action_navigation_home_to_navigation_detail_result)
        }

        viewModel.getProductList().observe(viewLifecycleOwner){
            when(it){
                is Result.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                }

                is Result.Success -> {
                    binding.progressBar.visibility = View.GONE
                    productAdapter.submitList(it.data)
                }

                is Result.Error -> {
                    binding.progressBar.visibility = View.GONE
                }
            }
        }

        val blogsAdapter = BlogsAdapter {
            view?.findNavController()
                ?.navigate(R.id.action_navigation_home_to_navigation_detail_result)
        }
        blogsAdapter.submitList(CardData.listData)

        binding.rvHomeHistory.apply {
            setHasFixedSize(true)
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = productAdapter
        }

        binding.rvBlogs.apply {
            setHasFixedSize(true)
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = blogsAdapter
        }
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val EXTRA_IMG = "extra_image"
        const val EXTRA_TITLE = "extra_title"
        const val EXTRA_DESC = "extra_desc"
    }
}