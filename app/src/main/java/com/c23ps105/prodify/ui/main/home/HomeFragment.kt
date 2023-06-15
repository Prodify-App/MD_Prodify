package com.c23ps105.prodify.ui.main.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.c23ps105.prodify.R
import com.c23ps105.prodify.data.Blog
import com.c23ps105.prodify.data.Product
import com.c23ps105.prodify.databinding.FragmentHomeBinding
import com.c23ps105.prodify.helper.AuthViewModelFactory
import com.c23ps105.prodify.helper.MainViewModelFactory
import com.c23ps105.prodify.helper.SessionPreferences
import com.c23ps105.prodify.ui.adapter.BlogsAdapter
import com.c23ps105.prodify.ui.adapter.ProductAdapter
import com.c23ps105.prodify.ui.main.dataStore
import com.c23ps105.prodify.ui.main.detail.DetailResultFragment
import com.c23ps105.prodify.ui.viewModel.AuthViewModel
import com.c23ps105.prodify.ui.viewModel.MainViewModel
import com.c23ps105.prodify.utils.Result

class HomeFragment : Fragment() {
    private lateinit var viewModel: MainViewModel
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        setViewModel()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupProductList {
            it.adapter.submitList(it.list)
            binding.rvHomeHistory.apply {
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                adapter = it.adapter
            }
        }

        setupBlogList {
            it.adapter.submitList(it.list)
            binding.rvBlogs.apply {
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                setHasFixedSize(true)
                adapter = it.adapter
            }
        }
    }

    private fun setupBlogList(adapter: (Blog) -> Unit) {
        val blogsAdapter = BlogsAdapter { blog ->
            val mBundle = Bundle().also {
                it.putInt(EXTRA_ID, blog.blogId)
                it.putString(EXTRA_STATE, BLOG_STATE)
            }

            findNavController().enableOnBackPressed(true)
            findNavController().navigate(
                R.id.action_navigation_home_to_navigation_detail_result,
                mBundle
            )
        }
        viewModel.getBlogs().observe(viewLifecycleOwner) {
            when (it) {
                is Result.Success -> {
                    adapter(Blog(blogsAdapter, it.data))
                }

                else -> {}
            }
        }


    }

    private fun setupProductList(
        adapter: (Product) -> Unit,
    ) {
        val productAdapter = ProductAdapter(
            onProductClick = { product ->
                val mBundle = Bundle().also {
                    it.putInt(EXTRA_ID, product.id)
                    it.putString(EXTRA_STATE, PRODUCT_STATE)
                    it.putParcelable(DetailResultFragment.NEWS_DATA, product)
                }

                findNavController().enableOnBackPressed(true)
                findNavController().navigate(
                    R.id.action_navigation_home_to_navigation_detail_result, mBundle
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
                    adapter.invoke(Product(productAdapter, it.data))
                }

                is Result.Error -> {
                    binding.progressBar.visibility = View.GONE
                }
            }
        }
    }

    private fun setViewModel() {
        val pref = SessionPreferences.getInstance(requireContext().dataStore)
        val factory = MainViewModelFactory.getInstance(requireContext(), pref)
        viewModel = activityViewModels<MainViewModel> { factory }.value

        val aFactory = AuthViewModelFactory.getInstance(pref)
        val authViewModel = activityViewModels<AuthViewModel> { aFactory }.value
        authViewModel.getPreferences().observe(viewLifecycleOwner) {
            binding.navbar.tvUserHome.text =
                getString(R.string.greetings).replace("{z}", it.username)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private companion object {
        private const val EXTRA_ID = "id"
        private const val EXTRA_STATE = "state"
        private const val BLOG_STATE = "blog"
        private const val PRODUCT_STATE = "product"
    }
}