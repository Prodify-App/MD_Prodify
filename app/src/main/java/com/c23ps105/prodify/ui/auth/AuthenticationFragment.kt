package com.c23ps105.prodify.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.c23ps105.prodify.R
import com.c23ps105.prodify.databinding.FragmentAuthenticationBinding

class AuthenticationFragment : Fragment() {
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

        binding.username.visibility = if(state == "login") View.GONE else View.VISIBLE

        binding.btnContinue.setOnClickListener {
            val mBundle = Bundle()
            mBundle.putBoolean(EXTRA_LOGIN, true)
            view?.findNavController()?.navigate(R.id.action_authenticationFragment_to_mainActivity, mBundle)
            (activity as AuthActivity).finish()
        }
        return binding.root
    }

    private fun isLogin(state: String): Boolean {
        return state == "Login"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object{
        const val EXTRA_LOGIN = "login_extra"
    }
}