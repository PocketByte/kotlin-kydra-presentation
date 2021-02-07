package ru.pocketbyte.kydra.presentation

import platform.Foundation.NSThread

actual class PresentersSet: AbsPresentersSet() {
    actual override fun isCurrentThreadValid(): Boolean {
        return NSThread.isMainThread
    }
}