package ru.pocketbyte.kydra.presentation

actual class PresentersSet: AbsPresentersSet() {
    actual override fun isCurrentThreadValid(): Boolean {
        // JS has only one thread
        return true
    }
}