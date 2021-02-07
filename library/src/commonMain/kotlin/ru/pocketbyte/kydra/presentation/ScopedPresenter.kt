package ru.pocketbyte.kydra.presentation

interface ScopedPresenter {
    enum class State {
        NONE,
        PREPARED,
        STARTED,
        STOPPED,
        DESTROYED
    }

    val state: State

    /**
     * Prepares presenter. Presenter must be prepared before any usage.
     */
    fun prepare()

    /**
     * Starts presenter.
     */
    fun start()

    /**
     * Stops presenter.
     */
    fun stop()

    /**
     * Destroys presenter. Destroyed presenter can't be used anymore.
     */
    fun destroy()
}