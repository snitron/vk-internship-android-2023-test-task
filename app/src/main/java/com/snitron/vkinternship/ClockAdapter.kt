package com.snitron.vkinternship

import android.content.ClipData.Item
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.snitron.vkinternship.clock.ClockView
import kotlin.math.max
import kotlin.math.min
import kotlin.random.Random

class ClockAdapter(private val clocksData: MutableList<ClockInitData>): RecyclerView.Adapter<ItemClockViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemClockViewHolder = ItemClockViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.clock_item, parent, false)
    )

    override fun getItemCount(): Int = clocksData.size

    override fun onBindViewHolder(holder: ItemClockViewHolder, position: Int) {
        holder.clockView.secondHandWidth = clocksData[position].secondHandWidth
        holder.clockView.minuteHandWidth = clocksData[position].minuteHandWidth
        holder.clockView.hourHandWidth = clocksData[position].hourHandWidth

        holder.clockView.secondHandRadius = clocksData[position].secondHandRadius
        holder.clockView.minuteHandRadius = clocksData[position].minuteHandRadius
        holder.clockView.hourHandRadius = clocksData[position].hourHandRadius

        holder.clockView.secondHandColor = clocksData[position].secondHandColor
        holder.clockView.minuteHandColor = clocksData[position].minuteHandColor
        holder.clockView.hourHandColor = clocksData[position].hourHandColor

        holder.clockView.cvBackgroundColor = clocksData[position].cvBackgroundColor
        holder.clockView.borderColor = clocksData[position].borderColor
        holder.clockView.borderWidth = clocksData[position].borderWidth

        holder.clockView.divisionCount = clocksData[position].divisionCount

        holder.clockView.divisionColor = clocksData[position].divisionColor
        holder.clockView.divisionRadius = clocksData[position].divisionRadius
        holder.clockView.divisionTextColor = clocksData[position].divisionTextColor
        holder.clockView.divisionTextSize = clocksData[position].divisionTextSize

        holder.clockView.redrawInterval = clocksData[position].redrawInterval
        holder.clockView.invalidate()
    }

    fun add(index: Int, clockInitData: ClockInitData) {
        if (clocksData.isEmpty()) {
            clocksData.add(clockInitData)
            notifyItemInserted(0)
        } else {
            clocksData.add(index + 1, clockInitData)
            notifyItemRangeChanged(index, 2)
        }
    }

    fun remove(index: Int) {
        clocksData.removeAt(index)
        notifyItemRemoved(index)
    }
}

class ItemClockViewHolder(view: View): RecyclerView.ViewHolder(view) {
    val clockView: ClockView
    init {
        clockView = view.findViewById(R.id.itemClockView)
    }
}


private val random = Random(System.currentTimeMillis())

data class ClockInitData(
    val secondHandWidth: Float  = (random.nextFloat() + 0.1f) * 15f,
    val minuteHandWidth: Float  = (random.nextFloat() + 0.1f) * 15f,
    val hourHandWidth: Float    = (random.nextFloat() + 0.1f) * 15f,
    val secondHandRadius: Float = max(0.1f, min(1.0f, random.nextFloat())),
    val minuteHandRadius: Float = max(0.1f, min(1.0f, random.nextFloat())),
    val hourHandRadius: Float   = max(0.1f, min(1.0f, random.nextFloat())),
    val secondHandColor: Int    = random.randomColor(),
    val minuteHandColor: Int    = random.randomColor(),
    val hourHandColor: Int    = random.randomColor(),
    val cvBackgroundColor: Int  = random.randomColor(),
    val borderColor: Int    = random.randomColor(),
    val borderWidth: Float      = (random.nextFloat() + 0.1f) * 30f,
    val divisionCount: Int      = random.nextInt(0, 100),
    val divisionColor: Int     = random.randomColor(),
    val divisionRadius: Float   = (random.nextFloat() + 0.1f) * 10f,
    val divisionTextColor: Int  = random.randomColor(),
    val divisionTextSize: Float = (random.nextFloat() + 0.1f) * 50f + 10f,
    val redrawInterval: Int      = random.nextInt(0, 2000)
)