package io.github.stcksmsh.kap.data

import android.content.SharedPreferences

fun SharedPreferences.Editor.putDouble(key: String, value: Double) =
    putLong(key, java.lang.Double.doubleToRawLongBits(value))

fun SharedPreferences.getDouble(key: String, default: Double) =
    java.lang.Double.longBitsToDouble(getLong(key, java.lang.Double.doubleToRawLongBits(default)))
