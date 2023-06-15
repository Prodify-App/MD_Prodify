package com.c23ps105.prodify.ui.main.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.bumptech.glide.Glide
import com.c23ps105.prodify.R
import com.c23ps105.prodify.data.DetailUserModel
import com.c23ps105.prodify.data.local.entity.ProductEntity
import com.c23ps105.prodify.data.remote.response.BlogsItem
import com.c23ps105.prodify.databinding.FragmentDetailResultBinding
import com.c23ps105.prodify.helper.MainViewModelFactory
import com.c23ps105.prodify.helper.SessionPreferences
import com.c23ps105.prodify.ui.main.dataStore
import com.c23ps105.prodify.ui.viewModel.BookmarkViewModel
import com.c23ps105.prodify.ui.viewModel.MainViewModel
import com.c23ps105.prodify.utils.Result
import com.c23ps105.prodify.utils.cat
import com.c23ps105.prodify.utils.toClipboard
import com.google.android.material.snackbar.Snackbar


class DetailResultFragment : Fragment() {
    private var id: Int? = null
    private var state: String? = null

    private lateinit var viewModel: MainViewModel
    private lateinit var bookmarkViewModel: BookmarkViewModel
    private lateinit var productDetail: ProductEntity

    private var _binding: FragmentDetailResultBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        productDetail = arguments?.getParcelable<ProductEntity>(NEWS_DATA) as ProductEntity
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentDetailResultBinding.inflate(layoutInflater)
        setViewModel()

        if (id != null) {
            when (state) {
                BlOGS_STATE -> {
                    setUpBlogDetail {
                        binding.apply {
                            progressBar.visibility = View.GONE
                            tvCategoryContainer.visibility = View.GONE
                            icCopyDescription.visibility = View.GONE
                            icCopyTitle.visibility = View.GONE

                            tvTitle.text = it.title
                            tvDesc.text = it.description
                            ivDetail.fromImageURI(it.imageURL.toString())
                        }
                    }
                }

                PRODUCT_STATE -> {
                    setUpProductDetail {
                        binding.apply {
                            progressBar.visibility = View.GONE
                            tvTitle.text = it.title
                            tvDesc.text = it.description
                            tvCategoryWhite.text = it.category
                            ivDetail.fromImageURI(it.imageURL)

                            icCopyTitle.setOnClickListener {
                                binding.tvTitle.text.toString().toClipboard(
                                    requireActivity(), "title"
                                )
                                viewModel.setToastText("Berhasil Menyalin Title.")

                            }

                            icCopyDescription.setOnClickListener {
                                binding.tvDesc.text.toString()
                                    .toClipboard(requireActivity(), "description")
                                viewModel.setToastText("Berhasil Menyalin Deskripsi.")
                            }
                        }
                    }

                }

                else -> {
                    cat("null")
                }
            }
        }

        return binding.root
    }

    private fun setUpProductDetail(data: (DetailUserModel) -> Unit) {
        viewModel.getProductDetail(id as Int).observe(viewLifecycleOwner) {
            when (it) {
                is Result.Error -> {
                    binding.progressBar.visibility = View.GONE
                }

                Result.Loading -> binding.progressBar.visibility = View.VISIBLE
                is Result.Success -> {
                    data.invoke(it.data)
                }
            }
        }

    }

    private fun setUpBlogDetail(data: (BlogsItem) -> Unit) {
        viewModel.getBlogsById(id as Int).observe(viewLifecycleOwner) {
            when (it) {
                is Result.Error -> binding.progressBar.visibility = View.GONE
                Result.Loading -> binding.progressBar.visibility = View.VISIBLE
                is Result.Success -> {
                    binding.progressBar.visibility = View.GONE
                    data.invoke(it.data)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setViewModel() {
        id = arguments?.getInt(EXTRA_ID, 0)
        state = arguments?.getString(EXTRA_STATE)

        val pref = SessionPreferences.getInstance(requireContext().dataStore)
        val factory = MainViewModelFactory.getInstance(requireContext(), pref)
        viewModel = activityViewModels<MainViewModel> { factory }.value
        bookmarkViewModel = activityViewModels<BookmarkViewModel> { factory }.value

        viewModel.getToastText().observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let { text ->
                Snackbar.make(binding.root, text, Snackbar.LENGTH_SHORT).show()
            }
        }

        bookmarkViewModel.setProductData(productDetail)

        bookmarkViewModel.bookmarkStatus.observe(viewLifecycleOwner) { status ->
            setBookmarkState(status)
        }

        binding.icBookmark.setOnClickListener {
            bookmarkViewModel.changeBookmark(productDetail)
        }
    }

    private fun setBookmarkState(state: Boolean) {
        val white = ContextCompat.getDrawable(requireContext(), R.drawable.bookmark)
        val black = ContextCompat.getDrawable(requireContext(), R.drawable.bookmarked)
        if (state) {
            binding.icBookmark.setImageDrawable(black)
        } else {
            binding.icBookmark.setImageDrawable(white)
        }
    }

    companion object {
        private const val EXTRA_ID = "id"
        private const val EXTRA_STATE = "state"
        private const val BlOGS_STATE = "blog"
        private const val PRODUCT_STATE = "product"

        const val NEWS_DATA = "data"
    }
}

private fun ImageView.fromImageURI(uri: String) {
    Glide.with(this).load(uri)
        .placeholder(R.drawable.placeholder)
        .into(this)
}