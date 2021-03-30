package com.ivanmorgillo.corsoandroid.teamc

import android.app.Activity
import android.net.Uri
import android.os.Bundle
import android.util.DisplayMetrics
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
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.ivanmorgillo.corsoandroid.teamc.databinding.ActivityMainBinding
import com.ivanmorgillo.corsoandroid.teamc.utils.imageLoader
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber

interface StartGoogleSignIn {
    fun startGoogleSignIn(onSignInSuccess: () -> Unit)
}

class MainActivity : AppCompatActivity(), StartGoogleSignIn {
    private val viewModel: MainViewModel by viewModel()
    private val actionBarToggle: ActionBarDrawerToggle by lazy { ActionBarDrawerToggle(this, binding.drawerLayout, R.string.open, R.string.close) }
    private val navController: NavController by lazy { Navigation.findNavController(this, R.id.nav_host_fragment) }
    private val firebaseAuth: FirebaseAuth by lazy { Firebase.auth }
    private val binding: ActivityMainBinding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private var startGoogleSignInCallback: (() -> Unit)? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.plant(Timber.DebugTree())
        setContentView(binding.root)
        binding.drawerLayout.addDrawerListener(actionBarToggle)

        // Call syncState() on the action bar so it'll automatically change to the back button when the drawer layout is open
        actionBarToggle.syncState()

        // Display the hamburger icon to launch the drawer
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val headerView = binding.navView.getHeaderView(0)
        if (Firebase.auth.currentUser != null) {
            binding.navView.menu.findItem(R.id.sign_in).title = "Logout"
            headerView.findViewById<TextView>(R.id.userName).text = Firebase.auth.currentUser?.displayName
            headerView.findViewById<ImageView>(R.id.userAvatar).load(Firebase.auth.currentUser?.photoUrl, imageLoader(this))
        } else {
            binding.navView.menu.findItem(R.id.sign_in).title = "Login"
            headerView.findViewById<TextView>(R.id.userName).text = "My CookBook"
            headerView.findViewById<ImageView>(R.id.userAvatar).load(
                R.drawable.ic_chef
            )
        }
        drawerHandling(headerView)

        // Initialization Google Ads SDK
        setupAds()
    }

    private val adSize: AdSize
        get() {
            val display = windowManager.defaultDisplay
            val outMetrics = DisplayMetrics()
            display.getMetrics(outMetrics)
            val density = outMetrics.density

            var adWidthPixels = binding.adViewContainer.width.toFloat()
            if (adWidthPixels == 0f) {
                adWidthPixels = outMetrics.widthPixels.toFloat()
            }

            val adWidth = (adWidthPixels / density).toInt()
            return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(this, adWidth)
        }

    private fun setupAds() {
        MobileAds.initialize(this@MainActivity) {}
        val adView = AdView(this)
        binding.adViewContainer.addView(adView)
        adView.adListener = object : AdListener() {
            override fun onAdLoaded() = Unit

            override fun onAdFailedToLoad(adError: LoadAdError) {
                Timber.e(Throwable("Cannot Load Ad: ${adError.code}"))
            }

            override fun onAdOpened() = Unit

            override fun onAdClicked() = Unit

            override fun onAdLeftApplication() = Unit

            override fun onAdClosed() = Unit
        }
        adView.adUnitId = "ca-app-pub-3940256099942544/6300978111"

        adView.adSize = adSize

        // Create an ad request. Check your logcat output for the hashed device ID to
        // get test ads on a physical device, e.g.,
        // "Use AdRequest.Builder.addTestDevice("ABCDE0123") to get test ads on this device."
        val requestConfig = RequestConfiguration.Builder().setTestDeviceIds(
            listOf(
                "362E16B73FDC0FD988C3E2B13E5F0F29",
                "1EBA99F3F495E2249F5BDEB38B940769",
                "C2B7E85F57B7276965C4A1A1A10FB7B4",
                "60D5B4ACDF7C7F3527C7F5C42A051BF5"
            )
        ).build()
        MobileAds.setRequestConfiguration(requestConfig)
        val adRequest = AdRequest.Builder().build()

        // Start loading the ad in the background.
        adView.loadAd(adRequest)
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
                    viewModel.send(MainScreenEvent.OnFavouriteListMenuClicked)
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
                    if (Firebase.auth.currentUser == null) {
                        startGoogleSignIn {}
                        binding.drawerLayout.closeDrawer(GravityCompat.START)
                    } else {
                        signOut()
                        headerView.findViewById<ImageView>(R.id.userAvatar).load(
                            R.drawable.ic_chef
                        )
                        headerView.findViewById<TextView>(R.id.userName).text = "My CookBook"
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
        val currentUser = firebaseAuth.currentUser
        if (result.resultCode == Activity.RESULT_OK) {
            binding.navView.menu.findItem(R.id.sign_in).title = "Logout"
            if (currentUser?.displayName != null) {
                Toast.makeText(this, "Welcome, ${currentUser.displayName}", Toast.LENGTH_SHORT).show()
                headerView.findViewById<TextView>(R.id.userName).text = currentUser.displayName
                headerView.findViewById<ImageView>(R.id.userAvatar).load(currentUser.photoUrl, imageLoader(this))
            } else {
                val userEmail = currentUser?.email?.split("@")?.get(0)
                headerView.findViewById<TextView>(R.id.userName).text = userEmail
                Toast.makeText(this, "Welcome, $userEmail", Toast.LENGTH_SHORT).show()
                if (currentUser?.photoUrl == null) {
                    headerView.findViewById<ImageView>(R.id.userAvatar).load(
                        R.drawable.ic_chef
                    )
                } else {
                    headerView.findViewById<ImageView>(R.id.userAvatar).load(currentUser.photoUrl, imageLoader(this))
                }
            }
            startGoogleSignInCallback?.invoke()
        } else {
            Timber.e("authentication error  ${response?.error?.errorCode}")
        }
    }

    private fun signOut() {
        val user = firebaseAuth.currentUser
        if (user?.displayName != null) {
            Toast.makeText(this, "Goodbye " + user.displayName, Toast.LENGTH_SHORT).show()
        } else {
            val userEmail = user?.email?.split("@")?.get(0)
            Toast.makeText(this, "Goodbye $userEmail", Toast.LENGTH_SHORT).show()
        }
        AuthUI.getInstance().signOut(this)
        firebaseAuth.signOut()
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
            .setIsSmartLockEnabled(false).setIsSmartLockEnabled(false, false)
            .setAvailableProviders(providers)
            .build()
        firebaseAuthenticationResultLauncher.launch(intent)
    }
}
