package ru.pocketbyte.kydra.presentation

import android.os.Looper

actual class PresentersSet: AbsPresentersSet() {
    actual override fun isCurrentThreadValid(): Boolean {
        return Looper.myLooper() == Looper.getMainLooper()
    }
}