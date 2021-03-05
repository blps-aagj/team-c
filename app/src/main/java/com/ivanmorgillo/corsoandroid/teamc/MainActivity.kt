package com.ivanmorgillo.corsoandroid.teamc

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.core.view.GravityCompat
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.ivanmorgillo.corsoandroid.teamc.databinding.ActivityMainBinding
import com.novoda.simplechromecustomtabs.SimpleChromeCustomTabs
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {
    private val viewModel: MainViewModel by viewModel()
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
                R.id.feedback -> {
                    viewModel.send(MainScreenEvent.OnFeedbackClicked)
                    SimpleChromeCustomTabs.getInstance().navigateTo("https://forms.gle/SbtkKSk5T7eC9J4z7".toUri(), this)
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

    override fun onResume() {
        super.onResume()
        SimpleChromeCustomTabs.getInstance().connectTo(this)
    }

    override fun onPause() {
        SimpleChromeCustomTabs.getInstance().disconnectFrom(this)
        super.onPause()
    }
}

fun AppCompatActivity.openUrl(url: String) {
    val intent = Intent(Intent.ACTION_VIEW)
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    intent.data = Uri.parse(url)
    startActivity(intent)
}
