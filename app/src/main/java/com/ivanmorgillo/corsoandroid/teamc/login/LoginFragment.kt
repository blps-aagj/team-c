package com.ivanmorgillo.corsoandroid.teamc.login

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.ivanmorgillo.corsoandroid.teamc.R
import com.ivanmorgillo.corsoandroid.teamc.databinding.FragmentLoginBinding
import com.ivanmorgillo.corsoandroid.teamc.exhaustive
import com.ivanmorgillo.corsoandroid.teamc.gone
import com.ivanmorgillo.corsoandroid.teamc.utils.bindings.viewBinding
import com.ivanmorgillo.corsoandroid.teamc.visible
import org.koin.androidx.viewmodel.ext.android.viewModel

class LoginFragment : Fragment(R.layout.fragment_login) {
    private val binding by viewBinding(FragmentLoginBinding::bind)
    private val viewModel: LoginViewModel by viewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        states()
    }

    private fun states() {
        viewModel.states.observe(viewLifecycleOwner, { state ->
            when (state) {
                LoginScreenStates.Content -> {
                    binding.loginNoNetwork.root.gone()
                    binding.loginProgressBar.root.gone()
                    binding.loginScreen.visible()
                }
                LoginScreenStates.Loading -> {
                    binding.loginNoNetwork.root.gone()
                    binding.loginScreen.gone()
                    binding.loginProgressBar.root.visible()
                }
                LoginScreenStates.NoNetworkError -> {
                    binding.loginProgressBar.root.gone()
                    binding.loginScreen.gone()
                    binding.loginNoNetwork.root.visible()
                }
            }.exhaustive
        })
    }
}
