package com.snitron.vkinternship

import kotlin.random.Random

fun Random.randomColor() = (nextFloat() * 16777215).toInt() or (0xFF shl 24)