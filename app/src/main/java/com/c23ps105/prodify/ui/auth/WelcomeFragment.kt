package com.c23ps105.prodify.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.c23ps105.prodify.R
import com.c23ps105.prodify.databinding.FragmentWelcomeBinding


class WelcomeFragment : Fragment() {
    private var _binding: FragmentWelcomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWelcomeBinding.inflate(layoutInflater)
        val mbundle = Bundle()
        binding.btnRegister.setOnClickListener {
            mbundle.putString(EXTRA_STATE, REGISTER_STATE)
            view?.findNavController()
                ?.navigate(R.id.action_welcomeFragment_to_authenticationFragment, mbundle)
        }

        binding.btnLogin.setOnClickListener {
            mbundle.putString(EXTRA_STATE, LOGIN_STATE)
            view?.findNavController()
                ?.navigate(R.id.action_welcomeFragment_to_authenticationFragment, mbundle)

        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val EXTRA_STATE = "state"
        const val LOGIN_STATE = "login"
        const val REGISTER_STATE = "register"
    }
}