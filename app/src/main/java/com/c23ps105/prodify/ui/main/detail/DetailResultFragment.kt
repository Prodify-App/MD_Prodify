package com.c23ps105.prodify.ui.main.detail

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.bumptech.glide.Glide
import com.c23ps105.prodify.R
import com.c23ps105.prodify.databinding.FragmentDetailResultBinding
import com.c23ps105.prodify.helper.ProductViewModelFactory
import com.c23ps105.prodify.helper.SessionPreferences
import com.c23ps105.prodify.ui.main.home.HomeFragment
import com.c23ps105.prodify.ui.viewModel.ProductViewModel
import com.c23ps105.prodify.utils.Result
import com.c23ps105.prodify.utils.dataStore
import com.google.android.material.snackbar.Snackbar


class DetailResultFragment : Fragment() {
    private lateinit var viewModel: ProductViewModel
    private var id: Int? = null
    private var _binding: FragmentDetailResultBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailResultBinding.inflate(layoutInflater)
        setViewModel()

        if (id != null) {
            viewModel.getProductDetail(id!!).observe(viewLifecycleOwner) {
                when (it) {
                    is Result.Error -> binding.progressBar.visibility = View.GONE
                    Result.Loading -> binding.progressBar.visibility = View.VISIBLE
                    is Result.Success -> {
                        if (it.data.isBookmarked) {
                            binding.icBookmark.setImageDrawable(
                                ContextCompat.getDrawable(
                                    binding.icBookmark.context,
                                    R.drawable.bookmarked
                                )
                            )
                        } else {
                            binding.icBookmark.setImageDrawable(
                                ContextCompat.getDrawable(
                                    binding.icBookmark.context,
                                    R.drawable.bookmark
                                )
                            )
                        }
                        binding.apply {
                            progressBar.visibility = View.GONE
                            tvTitle.text = it.data.title
                            tvDesc.text = it.data.description
                            Glide.with(ivDetail).load(it.data.imageURL)
                                .placeholder(R.drawable.placeholder)
                                .into(ivDetail)

                            icCopyTitle.setOnClickListener {
                                val text = binding.tvTitle.text.toString()
                                val clipboard = activity?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = ClipData.newPlainText("title", text)
                                clipboard.setPrimaryClip(clip)
                                Snackbar.make(binding.root, "Judul berhasil dicopy ke clipboard", Snackbar.LENGTH_SHORT).show()
                            }

                            icCopyDescription.setOnClickListener {
                                val text = binding.tvDesc.text.toString()
                                val clipboard = activity?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = ClipData.newPlainText("description", text)
                                clipboard.setPrimaryClip(clip)
                                Snackbar.make(binding.root, "Deskripsi berhasil dicopy ke clipboard", Snackbar.LENGTH_SHORT).show()
                            }

                            icBookmark.setOnClickListener { _ ->
                                if (it.data.isBookmarked) {
                                    viewModel.unBookmarkProduct(it.data)
                                } else {
                                    viewModel.bookmarkProduct(it.data)
                                }
                            }
                        }
                    }
                }
            }
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setViewModel() {
        id = arguments?.getInt(HomeFragment.EXTRA_ID, 0)
        val pref = SessionPreferences.getInstance(requireContext().dataStore)
        val factory = ProductViewModelFactory.getInstance(requireContext(), pref)
        viewModel = activityViewModels<ProductViewModel> { factory }.value
    }
}