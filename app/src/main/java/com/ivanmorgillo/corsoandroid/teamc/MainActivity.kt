package com.ivanmorgillo.corsoandroid.teamc

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.view.GravityCompat
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import com.ivanmorgillo.corsoandroid.teamc.databinding.ActivityMainBinding
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber

private const val RC_SIGN_IN: Int = 1234

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
                R.id.home_page -> {
                    navController.navigate(R.id.homeFragment)
                    binding.drawerLayout.closeDrawer(GravityCompat.START)
                    true
                }
                R.id.favourite_list -> {
                    navController.navigate(R.id.favouriteFragment)
                    binding.drawerLayout.closeDrawer(GravityCompat.START)
                    true
                }
                R.id.shopping_list -> {
                    Toast.makeText(this, "Lista della spesa da implementare", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.feedback -> {
                    viewModel.send(MainScreenEvent.OnFeedbackClicked)
                    val url = "https://docs.google.com/forms/d/e/1FAIpQLScDBfn5FxLD2H48W-NTjJJttWkIhDiFeHegUyj5H_EBTzYokQ/viewform?usp=sf_link"
                    val builder = CustomTabsIntent.Builder()
                    val customTabsIntent = builder.build()
                    customTabsIntent.launchUrl(this, Uri.parse(url))
                    binding.drawerLayout.closeDrawer(GravityCompat.START)
                    true
                }
                R.id.settings -> {
                    Toast.makeText(this, "Impostazioni da implementare", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.about -> {
                    // Choose authentication providers
                    val providers = arrayListOf(
                        AuthUI.IdpConfig.GoogleBuilder().build(),
                    )
                    startActivityForResult(
                        AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setAvailableProviders(providers)
                            .build(),
                        RC_SIGN_IN
                    )
                    binding.drawerLayout.closeDrawer(GravityCompat.START)
                    true
                }
                else -> {
                    false
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val response = IdpResponse.fromResultIntent(data)

            if (resultCode == Activity.RESULT_OK) {
                // Successfully signed in
                val user = FirebaseAuth.getInstance().currentUser
                Timber.d("user : $user")
                Toast.makeText(this, "Welcome, ${user.displayName}", Toast.LENGTH_LONG).show()
            } else {
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.
                // ...
                Timber.e("autentication error : ${response?.error?.errorCode}")
            }
        }
    }

    fun setCheckedItem(id: Int) {
        binding.navView.setCheckedItem(id)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (actionBarToggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}
