package com.c23ps105.prodify.ui.main.result

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.c23ps105.prodify.R
import com.c23ps105.prodify.databinding.FragmentResultBinding
import com.c23ps105.prodify.helper.ProductViewModelFactory
import com.c23ps105.prodify.helper.SessionPreferences
import com.c23ps105.prodify.ui.adapter.ResultAdapter
import com.c23ps105.prodify.ui.main.home.HomeFragment
import com.c23ps105.prodify.ui.viewModel.ProductViewModel
import com.c23ps105.prodify.utils.Result
import com.c23ps105.prodify.utils.dataStore

class ResultFragment : Fragment() {
    private var _binding: FragmentResultBinding? = null
    private lateinit var viewModel: ProductViewModel
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentResultBinding.inflate(inflater, container, false)
        setViewModel()
        val root: View = binding.root

        val productAdapter = ResultAdapter(
            onBookmarkClick = { bookmark ->
                if (bookmark.isBookmarked) {
                    viewModel.unBookmarkProduct(bookmark)
                } else {
                    viewModel.bookmarkProduct(bookmark)
                }
            },
            onProductClick = { product ->
                val mBundle = Bundle()
                mBundle.putInt(HomeFragment.EXTRA_ID, product.id)

                findNavController().enableOnBackPressed(true)
                findNavController().navigate(
                    R.id.action_navigation_result_to_navigation_detail_result, mBundle
                )
            }
        )

        viewModel.getProductList().observe(viewLifecycleOwner) {
            when (it) {
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

        binding.rvResult.apply {
            setHasFixedSize(true)
            StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
            adapter = productAdapter
        }

        return root
    }

    private fun setViewModel() {
        val pref = SessionPreferences.getInstance(requireContext().dataStore)
        val factory = ProductViewModelFactory.getInstance(requireContext(), pref)
        viewModel = activityViewModels<ProductViewModel> { factory }.value
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val adapter = "ResultAdapter"
    }
}