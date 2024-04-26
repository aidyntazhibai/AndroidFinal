package com.example.onlineshop.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.onlineshop.R
import com.example.onlineshop.databinding.FragmentLikeBinding


class LikeFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentLikeBinding.inflate(inflater)
        return inflater.inflate(R.layout.fragment_like, container, false)
    }

    companion object {

        @JvmStatic
        fun newInstance() = LikeFragment()
    }
}