package com.example.sgpsiindex

import android.app.Application
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.sgpsiindex.api.Api
import com.example.sgpsiindex.database.Database
import com.example.sgpsiindex.model.*
import com.example.sgpsiindex.repository.Repository
import com.example.sgpsiindex.utility.Utility
import com.example.sgpsiindex.viewmodel.ViewModel
import com.google.android.gms.maps.CameraUpdateFactory

import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.material.tabs.TabLayout
import java.util.concurrent.Executors
import com.google.maps.android.ui.IconGenerator
import kotlinx.android.synthetic.main.activity_main.*
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var googleMap: GoogleMap

    private val viewModel: ViewModel by viewModels {
        object: ViewModelProvider.Factory {
            override fun <T : androidx.lifecycle.ViewModel?> create(modelClass: Class<T>): T {
                val mainApi = Api.create()
                val mainDb = Database.create(applicationContext as Application)
                val executor = Executors.newFixedThreadPool(5)

                val mainRepository = Repository(mainApi, mainDb, executor)

                @Suppress("UNCHECKED_CAST")
                return ViewModel(mainRepository) as T
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setUpToolbar()
        setUpTabs()
        setUpRefresh()
        setUpMap()
        setUpViewModel()
    }

    private fun setUpToolbar() {
        setSupportActionBar(toolbar)
        toolbar.title = getString(R.string.app_name)
    }

    private fun setUpTabs() {
        val tabItems = HashMap<String, String>()
        tabItems[Utility.PSI_TWENTY_FOUR_HOURLY] = getString(R.string.psi_twenty_four_hourly)
        tabItems[Utility.PM25_TWENTY_FOUR_HOURLY] = getString(R.string.pm25_twenty_four_hourly)

        for ((key, value) in tabItems) {
            val tab = tabLayout.newTab()
            tab.text = value
            tab.tag = key

            tabLayout.addTab(tab)
        }

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                viewModel.dataType.value = tab.tag as String
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })
    }

    private fun setUpRefresh() {
        swipeRefreshLayout.setOnRefreshListener {
            val simpleDateFormat = SimpleDateFormat(Utility.DATE_TIME_FORMAT, Locale.ENGLISH)
            viewModel.refresh(simpleDateFormat.format(Date()))
        }
    }

    private fun setUpMap() {
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    private fun setUpViewModel() {
        val simpleDateFormat = SimpleDateFormat(Utility.DATE_TIME_FORMAT, Locale.ENGLISH)
        viewModel.refresh(simpleDateFormat.format(Date()))
        viewModel.response.observe(this, Observer { updateUI(it) })
        viewModel.dataType.observe(this, Observer {
            val response = viewModel.response.value
            if (isUpdateRequired(response)) {
                updateMarkers(response!!.regions, response.items.first(), it)
            }
        })
        viewModel.state.observe(this, Observer {
            when(it.status) {
                Status.RUNNING -> {}
                Status.SUCCESS -> {
                    swipeRefreshLayout.isRefreshing = false
                }
                Status.FAILED -> {
                    swipeRefreshLayout.isRefreshing = false
                    Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    private fun updateUI(response: Response?) {
        if (!isUpdateRequired(response)) return

        val (_, regions, items) = response!!
        val item = items.first()

        val df1 = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.getDefault())
        val date = df1.parse(item.updateTimestamp) ?: Date()

        val lastUpdated = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).format(date)
        val lastUpdatedText = getString(R.string.last_update_at) + " " + lastUpdated
        lastUpdateTextView.text = lastUpdatedText

        updateNationalData(item)
        updateMarkers(regions, item, viewModel.dataType.value!!)

        mainLinearLayout.visibility = View.VISIBLE
    }

    private fun isUpdateRequired(response: Response?): Boolean {
        if (response == null) return false

        val (_, regions, items) = response
        if (regions.isEmpty()) return false
        if (items.isEmpty()) return false

        return true
    }

    private fun updateNationalData(item: Item) {
        val psi = item.readings["psi_twenty_four_hourly"]!!["national"]!!
        val (psiDescriptor, psiColor) = Utility.qualityDescriptorInfo(this, psi)
        val advisoryDescriptor = Utility.advisoryDescriptorInfo(this, psi)

        nationalPsiTextView.text = psiDescriptor
        nationalPsiTextView.setTextColor(psiColor)

        nationalHealthAdvisoryTextView.text = advisoryDescriptor
    }

    private fun updateMarkers(regions: List<Region>, item: Item, type: String) {
        googleMap.clear()

        val builder = LatLngBounds.Builder()
        for (region in regions) {
            val name = region.name
            val value = item.readings[type]!![name]!!
            val (_, valueColor) = Utility.qualityDescriptorInfo(this, value)

            if (name == Utility.PSI_NATIONAL) continue

            val iconGenerator = IconGenerator(this)

            val view = LayoutInflater
                .from(this)
                .inflate(R.layout.widget_marker, null)

            val valueTextView = view.findViewById<TextView>(R.id.valueTextView)
            val nameTextView = view.findViewById<TextView>(R.id.nameTextView)

            valueTextView.text = value.toString()

            if (type == Utility.PSI_TWENTY_FOUR_HOURLY) {
                valueTextView.setTextColor(valueColor)
            }

            nameTextView.text = name

            iconGenerator.setContentView(view)
            iconGenerator.setColor(ContextCompat.getColor(this, R.color.markerBackground))

            val latLng = LatLng(region.labelLocation["latitude"]!!, region.labelLocation["longitude"]!!)
            googleMap.addMarker(
                MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromBitmap(iconGenerator.makeIcon()))
                    .position(latLng)
            )
            builder.include(latLng)
        }

        val bounds = builder.build()
        googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 160))
    }

    override fun onMapReady(googleMap: GoogleMap) {
        this.googleMap = googleMap
        this.googleMap.uiSettings.isScrollGesturesEnabled = false
        this.googleMap.uiSettings.isZoomGesturesEnabled = false
        this.googleMap.uiSettings.isRotateGesturesEnabled = false
        this.googleMap.uiSettings.isTiltGesturesEnabled = false
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_action_refresh -> {
                swipeRefreshLayout.post {
                    swipeRefreshLayout.isRefreshing = true
                    val simpleDateFormat = SimpleDateFormat(Utility.DATE_TIME_FORMAT, Locale.ENGLISH)
                    viewModel.refresh(simpleDateFormat.format(Date()))
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
