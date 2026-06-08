package hr.algebra.voya.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import hr.algebra.voya.R
import hr.algebra.voya.api.ApiClient
import hr.algebra.voya.api.TokenManager
import hr.algebra.voya.databinding.ActivityReservationListBinding
import hr.algebra.voya.model.ReservationDto
import kotlinx.coroutines.launch

class ReservationListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityReservationListBinding
    private val reservations = mutableListOf<ReservationDto>()
    private lateinit var adapter: ReservationListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReservationListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        title = "Reservations"

        adapter = ReservationListAdapter(reservations)
        binding.listReservations.adapter = adapter

        binding.buttonNewReservation.setOnClickListener {
            startActivity(Intent(this, CreateReservationActivity::class.java))
        }

        binding.listReservations.setOnItemClickListener { _, _, position, _ ->
            val reservation = reservations[position]
            val intent = Intent(this, ReservationDetailActivity::class.java)
            intent.putExtra(ReservationDetailActivity.EXTRA_RESERVATION_ID, reservation.id)
            startActivity(intent)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_reservations -> true
            R.id.menu_profile -> {
                startActivity(Intent(this, ProfileActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onResume() {
        super.onResume()
        loadReservations()
    }

    private fun loadReservations() {
        setLoading(true)

        lifecycleScope.launch {
            try {
                val response = ApiClient.getApiService(this@ReservationListActivity).getMyReservations()

                if (response.isSuccessful) {
                    val body = response.body().orEmpty()
                    reservations.clear()
                    reservations.addAll(body.sortedByDescending { it.time })
                    adapter.notifyDataSetChanged()
                    binding.textEmptyReservations.isVisible = reservations.isEmpty()
                    return@launch
                }

                if (response.code() == 401) {
                    TokenManager.clear(this@ReservationListActivity)
                    Toast.makeText(this@ReservationListActivity, "Session expired. Please login again.", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this@ReservationListActivity, LoginActivity::class.java))
                    finish()
                    return@launch
                }

                Toast.makeText(
                    this@ReservationListActivity,
                    "Error: ${response.code()}",
                    Toast.LENGTH_SHORT
                ).show()
            } catch (e: Exception) {
                Toast.makeText(
                    this@ReservationListActivity,
                    "Network error: ${e.message ?: "unknown"}",
                    Toast.LENGTH_SHORT
                ).show()
            } finally {
                setLoading(false)
            }
        }
    }

    private fun setLoading(isLoading: Boolean) {
        binding.progressReservations.isVisible = isLoading
        binding.listReservations.isVisible = !isLoading
        if (isLoading) {
            binding.textEmptyReservations.isVisible = false
        }
    }

    private inner class ReservationListAdapter(
        items: List<ReservationDto>
    ) : ArrayAdapter<ReservationDto>(
        this@ReservationListActivity,
        android.R.layout.simple_list_item_2,
        android.R.id.text1,
        items
    ) {

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val view = convertView ?: LayoutInflater.from(context)
                .inflate(android.R.layout.simple_list_item_2, parent, false)

            val reservation = getItem(position) ?: return view
            val textPrimary = view.findViewById<TextView>(android.R.id.text1)
            val textSecondary = view.findViewById<TextView>(android.R.id.text2)

            textPrimary.text = "#${reservation.id} - ${reservation.status} - ${reservation.vehicleCategoryName}"
            textSecondary.text = "${reservation.time} | ${reservation.pickupLocation} -> ${reservation.dropoffLocation}"

            return view
        }
    }
}
