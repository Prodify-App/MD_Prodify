package com.c23ps105.prodify.ui.result

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.c23ps105.prodify.R
import com.c23ps105.prodify.data.sample.CardData
import com.c23ps105.prodify.databinding.FragmentResultBinding
import com.c23ps105.prodify.ui.adapter.ResultAdapter
import com.c23ps105.prodify.ui.home.HomeFragment

class ResultFragment : Fragment() {

    private var _binding: FragmentResultBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val resultViewModel =
            ViewModelProvider(this).get(ResultViewModel::class.java)

        _binding = FragmentResultBinding.inflate(inflater, container, false)
        val root: View = binding.root

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
//            setHasFixedSize(true)
            StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
            adapter = productAdapter
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}