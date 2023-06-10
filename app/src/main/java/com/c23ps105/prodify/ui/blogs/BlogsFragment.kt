package com.c23ps105.prodify.ui.blogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.c23ps105.prodify.R
import com.c23ps105.prodify.data.sample.CardData
import com.c23ps105.prodify.databinding.FragmentBlogsBinding
import com.c23ps105.prodify.ui.adapter.BlogsAdapter
import com.c23ps105.prodify.ui.viewModel.BlogsViewModel

class BlogsFragment : Fragment() {

    private var _binding: FragmentBlogsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val dashboardViewModel =
            ViewModelProvider(this)[BlogsViewModel::class.java]

        _binding = FragmentBlogsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val blogsAdapter = BlogsAdapter {
            view?.findNavController()
                ?.navigate(R.id.action_navigation_home_to_navigation_detail_result)
        }
        blogsAdapter.submitList(CardData.listData)

        binding.rvBlogs.apply {
            setHasFixedSize(true)
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = blogsAdapter
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}