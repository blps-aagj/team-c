package com.ivanmorgillo.corsoandroid.teamc

import android.app.Activity
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.view.GravityCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.ErrorCodes
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.ivanmorgillo.corsoandroid.teamc.databinding.ActivityMainBinding
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber

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
                R.id.sign_in -> {
                    // Choose authentication providers
                    val providers = arrayListOf(
                        AuthUI.IdpConfig.GoogleBuilder().build(),
                    )
                    val intent = AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .enableAnonymousUsersAutoUpgrade()
                        .build()
                    firebaseAuthenticationResultLauncher.launch(intent)
                    binding.drawerLayout.closeDrawer(GravityCompat.START)
                    true
                }
                else -> {
                    false
                }
            }
        }
    }

    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = Firebase.auth.currentUser
        if (currentUser == null) {
            signinAnonymously()
        } else {
            Log.d("pippo già loggato", "${currentUser.uid}")
        }
    }

    private fun signinAnonymously() {
        lifecycleScope.launch {
            val tmp = Firebase.auth.signInAnonymously().await()
            Log.d("pippo anonimo -> ", "${tmp.user.uid}")
        }
    }

    private var firebaseAuthenticationResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->

        val response = IdpResponse.fromResultIntent(result.data)

        if (result.resultCode == Activity.RESULT_OK) {
            // Successfully signed in
            val user = FirebaseAuth.getInstance().currentUser
            Log.d("pippo google", "${user?.uid}")
            Toast.makeText(this, "Welcome, ${user?.displayName}", Toast.LENGTH_LONG).show()
        } else {
            // Sign in failed. If response is null the user canceled the
            // sign-in flow using the back button. Otherwise check
            // response.getError().getErrorCode() and handle the error.
            // ...
            // Sign in failed
            if (response?.error?.errorCode == ErrorCodes.ANONYMOUS_UPGRADE_MERGE_CONFLICT) {
                // Store relevant anonymous user data
                // Get the non-anoymous credential from the response
                val nonAnonymousCredential = response.credentialForLinking
                // Sign in with credential
                FirebaseAuth.getInstance().signInWithCredential(nonAnonymousCredential)
                    .addOnSuccessListener {
                        Log.d("pippo mergiando", "${it.user.uid}")
                    }
            }
            Timber.e("authentication error  ${response?.error?.errorCode}")
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
