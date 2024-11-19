package com.example.lab1.presentation.screens

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.lab1.R
import com.example.lab1.databinding.FragmentSignUpBinding
import com.example.lab1.models.User

class SignUpFragment : Fragment() {

    private lateinit var binding: FragmentSignUpBinding

    private val TAG = "SignUpFragment"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSignUpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.d(TAG, "onViewCreate called")


        binding.btnSignup.setOnClickListener {

            val username = binding.usernameTextInput.text.toString()
            val email = binding.emailTextInput.text.toString()
            val password = binding.passwordTextInput.text.toString()

            if (password.length >= 3) {
                val user = User(username, email, password)
                val action = SignUpFragmentDirections
                    .actionSignUpFragmentToSignInFragment(user)
                findNavController().navigate(action)
                Log.d(TAG, "Successful registration")
            } else {
                Log.d(TAG, "Error! Short password")
                Toast.makeText(requireContext(), "Password must be 3 characters at least", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
