package com.snitron.vkinternship

import android.annotation.SuppressLint
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import android.widget.Button
import androidx.viewpager2.widget.ViewPager2
import com.snitron.vkinternship.clock.ClockView.Companion.DEFAULT_BACKGROUND_COLOR
import com.snitron.vkinternship.clock.ClockView.Companion.DEFAULT_BORDER_COLOR
import com.snitron.vkinternship.clock.ClockView.Companion.DEFAULT_BORDER_WIDTH
import com.snitron.vkinternship.clock.ClockView.Companion.DEFAULT_DIVISION_COLOR
import com.snitron.vkinternship.clock.ClockView.Companion.DEFAULT_DIVISION_COUNT
import com.snitron.vkinternship.clock.ClockView.Companion.DEFAULT_DIVISION_RADIUS
import com.snitron.vkinternship.clock.ClockView.Companion.DEFAULT_DIVISION_TEXT_COLOR
import com.snitron.vkinternship.clock.ClockView.Companion.DEFAULT_DIVISION_TEXT_SIZE
import com.snitron.vkinternship.clock.ClockView.Companion.DEFAULT_HOUR_HAND_COLOR
import com.snitron.vkinternship.clock.ClockView.Companion.DEFAULT_HOUR_HAND_RADIUS
import com.snitron.vkinternship.clock.ClockView.Companion.DEFAULT_HOUR_HAND_WIDTH
import com.snitron.vkinternship.clock.ClockView.Companion.DEFAULT_MINUTE_HAND_COLOR
import com.snitron.vkinternship.clock.ClockView.Companion.DEFAULT_MINUTE_HAND_RADIUS
import com.snitron.vkinternship.clock.ClockView.Companion.DEFAULT_MINUTE_HAND_WIDTH
import com.snitron.vkinternship.clock.ClockView.Companion.DEFAULT_REDRAW_INTERVAL
import com.snitron.vkinternship.clock.ClockView.Companion.DEFAULT_SECOND_HAND_COLOR
import com.snitron.vkinternship.clock.ClockView.Companion.DEFAULT_SECOND_HAND_RADIUS
import com.snitron.vkinternship.clock.ClockView.Companion.DEFAULT_SECOND_HAND_WIDTH

class MainActivity : AppCompatActivity() {
    private lateinit var clocks: ArrayList<ClockInitData>
    private lateinit var adapter: ClockAdapter
    private lateinit var viewPager2: ViewPager2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewPager2 = findViewById(R.id.viewPager2)

        @Suppress("UNCHECKED_CAST")
        clocks = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            (savedInstanceState?.getSerializable("clocks", ArrayList::class.java)
                    as? ArrayList<ClockInitData>) ?: arrayListOf()
        } else {
            @Suppress("DEPRECATION")
            (savedInstanceState?.getSerializable("clocks")
                    as? ArrayList<ClockInitData>) ?: arrayListOf()
        }

        if (clocks.isEmpty()) {
            clocks.add(
                ClockInitData(
                    DEFAULT_SECOND_HAND_WIDTH,
                    DEFAULT_MINUTE_HAND_WIDTH,
                    DEFAULT_HOUR_HAND_WIDTH,
                    DEFAULT_SECOND_HAND_RADIUS,
                    DEFAULT_MINUTE_HAND_RADIUS,
                    DEFAULT_HOUR_HAND_RADIUS,
                    DEFAULT_SECOND_HAND_COLOR,
                    DEFAULT_MINUTE_HAND_COLOR,
                    DEFAULT_HOUR_HAND_COLOR,
                    DEFAULT_BACKGROUND_COLOR,
                    DEFAULT_BORDER_COLOR,
                    DEFAULT_BORDER_WIDTH,
                    DEFAULT_DIVISION_COUNT,
                    DEFAULT_DIVISION_COLOR,
                    DEFAULT_DIVISION_RADIUS,
                    DEFAULT_DIVISION_TEXT_COLOR,
                    DEFAULT_DIVISION_TEXT_SIZE,
                    DEFAULT_REDRAW_INTERVAL
                )
            )
        }

        adapter = ClockAdapter(clocks)
        viewPager2.adapter = adapter

        val buttonRandomize = findViewById<Button>(R.id.buttonRandom)

        buttonRandomize.setOnClickListener {
            adapter.add(viewPager2.currentItem, ClockInitData())
            viewPager2.setCurrentItem(viewPager2.currentItem + 1, true)
        }

        val buttonClose = findViewById<Button>(R.id.buttonClose)

        buttonClose.setOnClickListener {
            if (clocks.isNotEmpty()) {
                adapter.remove(viewPager2.currentItem)
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putSerializable("clocks", clocks)
        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(
        savedInstanceState: Bundle?,
        persistentState: PersistableBundle?
    ) {
        super.onRestoreInstanceState(savedInstanceState, persistentState)
        @Suppress("UNCHECKED_CAST")
        clocks = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            (savedInstanceState?.getSerializable("clocks", ArrayList::class.java)
                    as? ArrayList<ClockInitData>) ?: arrayListOf()
        } else {
            @Suppress("DEPRECATION")
            (savedInstanceState?.getSerializable("clocks")
                    as? ArrayList<ClockInitData>) ?: arrayListOf()
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onStart() {
        super.onStart()

        adapter.notifyDataSetChanged()
    }
}