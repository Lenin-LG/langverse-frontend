package com.example.proyfronted.ui.auth

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.proyfronted.R
import androidx.navigation.fragment.findNavController
import com.example.proyfronted.backend.Auth.Client.RetrofitClient
import com.example.proyfronted.backend.Auth.Model.LoginRequest
import com.example.proyfronted.backend.Auth.SessionManager
import com.example.proyfronted.HomeActivity
import com.example.proyfronted.databinding.FragmentLoginBinding
import kotlinx.coroutines.launch

@androidx.media3.common.util.UnstableApi
class LoginFragment : Fragment() {
    private lateinit var binding: FragmentLoginBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        binding.tvRegister.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }

        binding.btnLogin.setOnClickListener {
            if (validateFields()) {
                loginUser()
            }
        }
    }

    private fun loginUser() {
        val username = binding.etUsername.text.toString()
        val password = binding.etPassword.text.toString()

        binding.progressBar.visibility = View.VISIBLE

        lifecycleScope.launch {
            try {
                val response = RetrofitClient.api.login(LoginRequest(username, password))

                if (response.isSuccessful) {
                    val tokenResponse = response.body()

                    if (tokenResponse != null) {
                        val session = SessionManager(requireContext())
                        session.saveToken(tokenResponse.access_token)
                     session.saveRefreshToken(tokenResponse.refresh_token)

                        // Ir al HomeActivity
                        val intent = Intent(requireContext(), HomeActivity::class.java)
                        startActivity(intent)
                        requireActivity().finish()
                    } else {
                        showError(" Error: respuesta del token es nula")
                    }
                } else {
                    showError("Credenciales incorrectas")
                }
            } catch (e: Exception) {
                showError("Error de red: ${e.localizedMessage}")
            } finally {
                binding.progressBar.visibility = View.GONE
            }
        }
    }

    private fun validateFields(): Boolean {
        val email = binding.etUsername.text.toString()
        val password = binding.etPassword.text.toString()

        if (email.isEmpty()) {
            binding.etUsername.error = "Username requerido"
            return false
        }

        if (password.isEmpty()) {
            binding.etPassword.error = "Contraseña requerida"
            return false
        }

        return true
    }
    private fun showError(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
    }
}