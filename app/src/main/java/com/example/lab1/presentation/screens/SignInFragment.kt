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
import com.example.lab1.databinding.FragmentSignInBinding

class SignInFragment : Fragment() {

    private lateinit var binding: FragmentSignInBinding
    private val args: SignInFragmentArgs by navArgs()

    private val TAG = "SignInFragment"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSignInBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val user = args.user

        Log.d(TAG, "User received: $user")

        binding.btnSignin.setOnClickListener {

            val username = binding.usernameTextInput.text.toString()
            val email = binding.emailTextInput.text.toString()
            val password = binding.passwordTextInput.text.toString()

            if (username == user.username && email == user.email && password == user.password) {
                Log.d(TAG, "Successful login")
                val action = SignInFragmentDirections.actionSignInFragmentToHomeFragment(user)
                findNavController().navigate(action)
            } else {
                Log.d(TAG, "Error! Invalid username or password")
                Toast.makeText(requireContext(), "Invalid username or password", Toast.LENGTH_SHORT).show()
            }

        }
    }
}
