package com.ivanmorgillo.corsoandroid.teamc

import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.ivanmorgillo.corsoandroid.teamc.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var actionBarToggle: ActionBarDrawerToggle
    private val navController: NavController by lazy { Navigation.findNavController(this, R.id.nav_host_fragment) }

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        actionBarToggle = ActionBarDrawerToggle(this, binding.drawerLayout, R.string.open, R.string.close)
        binding.drawerLayout.addDrawerListener(actionBarToggle)

        // Call syncState() on the action bar so it'll automatically change to the back button when the drawer layout is open
        actionBarToggle.syncState()

        // Display the hamburger icon to launch the drawer
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.favourite_list -> {
                    Toast.makeText(this, "favourite list is clicked", Toast.LENGTH_SHORT).show()
                    navController.navigate(R.id.favouriteFragment)
                    binding.drawerLayout.closeDrawer(GravityCompat.START)
                    true
                }
                else -> {
                    Toast.makeText(this, "Antani", Toast.LENGTH_SHORT).show()
                    false
                }
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (actionBarToggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}
