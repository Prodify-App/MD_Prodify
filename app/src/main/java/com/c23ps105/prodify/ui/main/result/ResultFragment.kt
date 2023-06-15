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
import com.c23ps105.prodify.data.Product
import com.c23ps105.prodify.data.UserProduct
import com.c23ps105.prodify.databinding.FragmentResultBinding
import com.c23ps105.prodify.helper.AuthViewModelFactory
import com.c23ps105.prodify.helper.MainViewModelFactory
import com.c23ps105.prodify.helper.SessionPreferences
import com.c23ps105.prodify.ui.adapter.ResultAdapter
import com.c23ps105.prodify.ui.main.dataStore
import com.c23ps105.prodify.ui.viewModel.AuthViewModel
import com.c23ps105.prodify.ui.viewModel.MainViewModel
import com.c23ps105.prodify.utils.Result
import com.c23ps105.prodify.utils.cat
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class ResultFragment : Fragment() {
    private var _binding: FragmentResultBinding? = null
    private lateinit var viewModel: MainViewModel
    private lateinit var authViewModel: AuthViewModel
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentResultBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setViewModel()
        setupResultList {
            it.adapter.submitList(it.list)
            binding.rvResult.apply {
                layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
                adapter = it.adapter
            }

        }
    }

    private fun setViewModel() {
        val pref = SessionPreferences.getInstance(requireContext().dataStore)

        val factory = MainViewModelFactory.getInstance(requireContext(), pref)
        viewModel = activityViewModels<MainViewModel> { factory }.value

        val authFactory = AuthViewModelFactory.getInstance(pref)
        authViewModel = activityViewModels<AuthViewModel> { authFactory }.value
        authViewModel.getPreferences().observe(viewLifecycleOwner) {
            binding.navbar.tvUserHome.text =
                getString(R.string.greetings).replace("{z}", it.username)
        }

    }

    private fun setupResultList(adapter: (UserProduct) -> Unit) {
        val resultAdapter = ResultAdapter { result ->
            val mBundle = Bundle().also {
                it.putInt(EXTRA_ID, result.id)
                it.putString(EXTRA_STATE, PRODUCT_STATE)
            }

            findNavController().enableOnBackPressed(true)
            findNavController().navigate(
                R.id.action_navigation_result_to_navigation_detail_result,
                mBundle
            )
        }

        viewModel.getProductList().observe(viewLifecycleOwner) {
            when (it) {
                is Result.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                }

                is Result.Success -> {
                    binding.progressBar.visibility = View.GONE
                    adapter.invoke(UserProduct(resultAdapter, it.data))
                }

                is Result.Error -> {
                    binding.progressBar.visibility = View.GONE
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val EXTRA_ID = "id"
        private const val PRODUCT_STATE = "product"
        private const val EXTRA_STATE = "state"
    }
}