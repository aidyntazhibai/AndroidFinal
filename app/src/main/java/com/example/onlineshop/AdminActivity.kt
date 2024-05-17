package com.example.onlineshop

import com.example.onlineshop.fragment.CatalogFragment
import ShopFragment
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.onlineshop.databinding.ActivityMainBinding
import com.example.onlineshop.fragment.BasketFragment
import com.example.onlineshop.fragment.LikeFragment
import PersonalAdminFragment


class AdminActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        replaceFragment(ShopFragment())

        binding.bNav.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.shop -> replaceFragment(ShopFragment())
                R.id.catalog -> replaceFragment(CatalogFragment())
                R.id.like -> replaceFragment(LikeFragment())
                R.id.orders -> replaceFragment(PersonalAdminFragment())
                R.id.basket -> replaceFragment(BasketFragment())

                else -> {
                }
            }
            true
        }

    }
    private fun replaceFragment(fragment: Fragment){
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment, fragment)
            .commit()
    }
}