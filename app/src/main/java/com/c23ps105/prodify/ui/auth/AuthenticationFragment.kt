package com.c23ps105.prodify.ui.auth

import android.content.Context
import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.c23ps105.prodify.R
import com.c23ps105.prodify.helper.SessionPreferences
import com.c23ps105.prodify.databinding.FragmentAuthenticationBinding
import com.c23ps105.prodify.ui.viewModel.AuthViewModel
import com.c23ps105.prodify.helper.AuthViewModelFactory
import com.c23ps105.prodify.utils.Result
import com.google.android.material.snackbar.Snackbar

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class AuthenticationFragment : Fragment() {
    private lateinit var viewModel: AuthViewModel
    private var _binding: FragmentAuthenticationBinding? = null
    private val binding get() = _binding!!
    private lateinit var state: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        state = arguments?.getString(WelcomeFragment.EXTRA_STATE).toString()
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAuthenticationBinding.inflate(inflater, container, false)

        val pref = SessionPreferences.getInstance(requireActivity().dataStore)
        val factory = AuthViewModelFactory.getInstance(pref)
        viewModel = ViewModelProvider(this, factory)[AuthViewModel::class.java]
        viewModel.getToastText().observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let { text ->
                showSnackBar(requireView(), text)
            }
        }

        when (state) {
            WelcomeFragment.LOGIN_STATE -> {
                binding.username.visibility = View.GONE
                binding.btnContinue.setOnClickListener {
                    viewModel.login(
                        binding.email.text.toString(),
                        binding.password.text.toString()
                    ).observe(viewLifecycleOwner) {
                        if (it != null) {
                            when (it) {
                                is Result.Loading -> {
                                    binding.progressBar.visibility = View.VISIBLE
                                }

                                is Result.Success -> {
                                    viewModel.saveSettings(it.data.first())
                                    viewModel.setToastText(it.data.last())

                                    findNavController().navigate(R.id.action_authenticationFragment_to_mainActivity)
                                    requireActivity().finish()
                                    binding.progressBar.visibility = View.GONE
                                }

                                is Result.Error -> {
                                    binding.progressBar.visibility = View.GONE
                                    viewModel.setToastText(it.error)
                                }
                            }
                        }
                    }
                }
            }

            WelcomeFragment.REGISTER_STATE -> {
                binding.username.visibility = View.VISIBLE
                viewModel.getRegisterResult().observe(viewLifecycleOwner) {
                    when (it) {
                        null -> {
                            binding.progressBar.visibility = View.VISIBLE
                        }

                        else -> {
                            binding.progressBar.visibility = View.GONE
                        }
                    }
                }

                binding.btnContinue.setOnClickListener {
                    viewModel.register(
                        binding.username.text.toString(),
                        binding.email.text.toString(),
                        binding.password.text.toString()
                    )
                }
            }
        }

        return binding.root
    }

    private fun showSnackBar(rootView: View, snackbarText: String) {
        val snackbar = Snackbar.make(rootView, snackbarText, Snackbar.LENGTH_SHORT)
        val snackbarView = snackbar.view
        val layoutParams = snackbarView.layoutParams as ViewGroup.MarginLayoutParams
        val navigationBarHeight = getNavigationBarHeight()

        layoutParams.setMargins(
            layoutParams.leftMargin,
            layoutParams.topMargin,
            layoutParams.rightMargin,
            layoutParams.bottomMargin + navigationBarHeight
        )

        snackbarView.layoutParams = layoutParams
        snackbar.show()
    }

    private fun getNavigationBarHeight(): Int {
        val visibleFrame = Rect()
        activity?.window?.decorView?.getWindowVisibleDisplayFrame(visibleFrame)
        val screenHeight = activity?.window?.decorView?.rootView?.height ?: 0
        return screenHeight - visibleFrame.bottom
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private val TAG = AuthActivity::class.java.simpleName
    }
}