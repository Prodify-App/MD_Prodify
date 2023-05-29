package com.c23ps105.prodify.ui.result

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.c23ps105.prodify.R
import com.c23ps105.prodify.databinding.FragmentDetailResultBinding
import com.c23ps105.prodify.ui.home.HomeFragment


class DetailResultFragment : Fragment() {
    private var _binding: FragmentDetailResultBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val img = arguments?.getInt(HomeFragment.EXTRA_IMG)
        val title = arguments?.getString(HomeFragment.EXTRA_TITLE)
        val desc = arguments?.getString(HomeFragment.EXTRA_DESC)

        _binding = FragmentDetailResultBinding.inflate(layoutInflater)
        binding.apply {
            tvTitle.text = title
            tvDesc.text = desc
            Glide.with(root).load(img).placeholder(R.drawable.placeholder)
                .into(ivDetail)
        }
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}