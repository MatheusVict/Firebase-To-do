package com.pet.foundation.taskfirebase.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.pet.foundation.taskfirebase.R
import com.pet.foundation.taskfirebase.databinding.FragmentSignUpBinding

class SignUpFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var navControl: NavController
    private lateinit var binding: FragmentSignUpBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSignUpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init(view)
        registerEvents()
    }

    private fun registerEvents() {
        binding.signInText.setOnClickListener {
            navControl.navigate(R.id.action_signUpFragment_to_singInFragment)
        }
        binding.btn.setOnClickListener {
            val email = binding.emailInput.text.toString().trim()
            val password = binding.passwordInput.text.toString().trim()
            val verifyPass = binding.confirmPasswordInput.text.toString().trim()

            if (email.isNotEmpty() && password.isNotEmpty() && verifyPass.isNotEmpty()) {
                if (password == verifyPass) {
                    auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(OnCompleteListener {
                            if (it.isSuccessful) {
                                Toast.makeText(
                                    requireContext(),
                                    "Registered successfully",
                                    Toast.LENGTH_SHORT
                                ).show()
                                navControl.navigate(R.id.action_signUpFragment_to_homeFragment)
                            } else {
                                Toast.makeText(
                                    requireContext(),
                                    "Error: ${it.exception?.message}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        })
                }
            }
        }
    }

    private fun init(view: View) {
        navControl = Navigation.findNavController(view)
        auth = FirebaseAuth.getInstance()
    }

}