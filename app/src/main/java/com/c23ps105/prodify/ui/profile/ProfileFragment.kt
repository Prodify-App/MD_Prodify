package com.c23ps105.prodify.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.c23ps105.prodify.R
import com.c23ps105.prodify.data.CardData
import com.c23ps105.prodify.databinding.FragmentProfileBinding
import com.c23ps105.prodify.ui.ProductAdapter
import com.c23ps105.prodify.ui.ResultAdapter
import com.c23ps105.prodify.ui.home.HomeFragment

class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(layoutInflater)

        val productAdapter = ResultAdapter {
            val mBundle = Bundle()
            mBundle.putInt(HomeFragment.EXTRA_IMG, it.imgData ?: 0)
            mBundle.putString(HomeFragment.EXTRA_TITLE, it.titleData)
            mBundle.putString(HomeFragment.EXTRA_DESC, it.contentData)
            view?.findNavController()
                ?.navigate(R.id.action_navigation_home_to_navigation_detail_result, mBundle)
        }
        productAdapter.submitList(CardData.listData)

        binding.rvResult.apply {
            setHasFixedSize(true)
            StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL)
            adapter = productAdapter
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}