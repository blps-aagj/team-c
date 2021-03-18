package com.ivanmorgillo.corsoandroid.teamc

import android.app.Activity
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.view.GravityCompat
import androidx.navigation.NavController
import androidx.navigation.Navigation
import coil.load
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.ivanmorgillo.corsoandroid.teamc.databinding.ActivityMainBinding
import com.ivanmorgillo.corsoandroid.teamc.home.MainScreenEvent
import com.ivanmorgillo.corsoandroid.teamc.home.MainViewModel
import com.ivanmorgillo.corsoandroid.teamc.utils.imageLoader
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber

interface StartGoogleSignIn {
    fun startGoogleSignIn(onSignInSuccess: () -> Unit)
}

class MainActivity : AppCompatActivity(), StartGoogleSignIn {
    private val viewModel: MainViewModel by viewModel()
    private lateinit var actionBarToggle: ActionBarDrawerToggle
    private val navController: NavController by lazy { Navigation.findNavController(this, R.id.nav_host_fragment) }
    private lateinit var binding: ActivityMainBinding
    private var startGoogleSignInCallback: (() -> Unit)? = null

    private val user = Firebase.auth.currentUser

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
        val headerView = binding.navView.getHeaderView(0)
        if (Firebase.auth.currentUser != null) {
            binding.navView.menu.findItem(R.id.sign_in).title = "Logout"
            headerView.findViewById<TextView>(R.id.userName).text = user?.displayName
            headerView.findViewById<ImageView>(R.id.userAvatar).load(user?.photoUrl, imageLoader(this))
        } else {
            binding.navView.menu.findItem(R.id.sign_in).title = "Login"
            headerView.findViewById<TextView>(R.id.userName).text = "User Name"
            headerView.findViewById<ImageView>(R.id.userAvatar).load(R.drawable.ic_placeholder_account_img)
        }

        drawerHandling(headerView)
    }

    private fun drawerHandling(headerView: View) {
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
                    Toast.makeText(this, "About da implementare", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.sign_in -> {
                    if (user == null) {
                        startGoogleSignIn {
                            Log.d("msg", "Login successful")
                        }

                        Log.d("pippo", "${user?.displayName}")

                        binding.drawerLayout.closeDrawer(GravityCompat.START)
                    } else {
                        signOut()
                        headerView.findViewById<ImageView>(R.id.userAvatar).load(R.drawable.ic_placeholder_account_img)
                        headerView.findViewById<TextView>(R.id.userName).text = "User Name"
                        binding.navView.menu.findItem(R.id.sign_in).title = "Login"
                        binding.drawerLayout.closeDrawer(GravityCompat.START)
                    }
                    true
                }
                else -> {
                    false
                }
            }
        }
    }

    private var firebaseAuthenticationResultLauncher = registerForActivityResult(StartActivityForResult()) { result ->

        val response = IdpResponse.fromResultIntent(result.data)
        val headerView = binding.navView.getHeaderView(0)
        if (result.resultCode == Activity.RESULT_OK) {
            binding.navView.menu.findItem(R.id.sign_in).title = "Logout"
            if (user?.displayName != null) {
                Toast.makeText(this, "Welcome, ${user.displayName}", Toast.LENGTH_SHORT).show()
                headerView.findViewById<TextView>(R.id.userName).text = user.displayName
                headerView.findViewById<ImageView>(R.id.userAvatar).load(user.photoUrl, imageLoader(this))
            } else {
                val userEmail = user?.email?.split("@")?.get(0)
                headerView.findViewById<TextView>(R.id.userName).text = userEmail
                Toast.makeText(this, "Welcome, $userEmail", Toast.LENGTH_SHORT).show()
                if (user?.photoUrl == null) {
                    headerView.findViewById<ImageView>(R.id.userAvatar).load(R.drawable.ic_placeholder_account_img)
                } else {
                    headerView.findViewById<ImageView>(R.id.userAvatar).load(user.photoUrl, imageLoader(this))
                }
                Log.d("pippo 2 userEmail", "$userEmail")
            }
            Log.d("pippo 2 img", "${user?.email}")
            Log.d("pippo 2 pu", "${user?.photoUrl}")
            Log.d("pippo 2 dname", "${user?.displayName}")

            startGoogleSignInCallback?.invoke()
        } else {
            Timber.e("authentication error  ${response?.error?.errorCode}")
        }
    }

    private fun signOut() {
        Log.d("User signed out ", "user" + user)
        if (user?.displayName != null) {
            Toast.makeText(this, "Goodbye " + user.displayName, Toast.LENGTH_SHORT).show()
        } else {
            val userEmail = user?.email?.split("@")?.get(0)
            Toast.makeText(this, "Goodbye $userEmail", Toast.LENGTH_SHORT).show()
        }
        Firebase.auth.signOut()
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

    override fun startGoogleSignIn(onSignInSuccess: () -> Unit) {
        // Choose authentication providers
        this.startGoogleSignInCallback = onSignInSuccess
        val providers = arrayListOf(
            AuthUI.IdpConfig.GoogleBuilder().build(),
        )
        val intent = AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setAvailableProviders(providers)
            .enableAnonymousUsersAutoUpgrade()
            .build()
        firebaseAuthenticationResultLauncher.launch(intent)
    }
}
