package com.c23ps105.prodify.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.c23ps105.prodify.R
import com.c23ps105.prodify.data.CardData
import com.c23ps105.prodify.databinding.FragmentHomeBinding
import com.c23ps105.prodify.ui.BlogsAdapter
import com.c23ps105.prodify.ui.ProductAdapter

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this)[HomeViewModel::class.java]
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        val productAdapter = ProductAdapter {
            val mBundle = Bundle()
            mBundle.putInt(EXTRA_IMG, it.imgData ?: 0)
            mBundle.putString(EXTRA_TITLE, it.titleData)
            mBundle.putString(EXTRA_DESC, it.contentData)
            view?.findNavController()
                ?.navigate(R.id.action_navigation_home_to_navigation_detail_result, mBundle)
        }
        productAdapter.submitList(CardData.listData)

        val blogsAdapter = BlogsAdapter{
            view?.findNavController()?.navigate(R.id.action_navigation_home_to_navigation_detail_result)
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