package ru.pocketbyte.kydra.presentation

expect class PresentersSet: AbsPresentersSet {
    override fun isCurrentThreadValid(): Boolean
}