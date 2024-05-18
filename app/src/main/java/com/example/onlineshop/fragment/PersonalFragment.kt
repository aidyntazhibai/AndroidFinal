package com.example.onlineshop.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.onlineshop.LoginActivity
import com.example.onlineshop.databinding.FragmentPersonalBinding
import com.google.firebase.auth.FirebaseAuth

class PersonalFragment : Fragment() {
    private var _binding: FragmentPersonalBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPersonalBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sharedPref = activity?.getSharedPreferences("MySharedPref", Context.MODE_PRIVATE)
        val email = sharedPref?.getString("email", "No Email")
        val password = sharedPref?.getString("password", "No Password")

        binding.tvEmail.text = "email: $email"
        binding.tvPassword.text = "password: $password"

        binding.buttonLogout.setOnClickListener {
            logout()
        }
    }

    private fun logout() {
        val sharedPref = activity?.getSharedPreferences("MySharedPref", Context.MODE_PRIVATE)
        val editor = sharedPref?.edit()
        editor?.clear()
        editor?.apply()

        FirebaseAuth.getInstance().signOut()

        val intent = Intent(activity, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        @JvmStatic
        fun newInstance() = PersonalFragment()
    }
}
