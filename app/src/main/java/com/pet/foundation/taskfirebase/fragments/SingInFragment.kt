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
import com.pet.foundation.taskfirebase.databinding.FragmentSingInBinding


class SingInFragment : Fragment() {
    private lateinit var auth: FirebaseAuth
    private lateinit var navControl: NavController
    private lateinit var binding: FragmentSingInBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSingInBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init(view)
        registerEvents()
    }

    private fun registerEvents() {
        binding.textViewSignUp.setOnClickListener {
            navControl.navigate(R.id.action_signUpFragment_to_singInFragment)
        }
        binding.nextBtn.setOnClickListener {
            val email = binding.emailEt.text.toString().trim()
            val password = binding.passEt.text.toString().trim()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(OnCompleteListener {
                        if (it.isSuccessful) {
                            Toast.makeText(
                                requireContext(),
                                "Login successfully",
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

    private fun init(view: View) {
        navControl = Navigation.findNavController(view)
        auth = FirebaseAuth.getInstance()
    }

}