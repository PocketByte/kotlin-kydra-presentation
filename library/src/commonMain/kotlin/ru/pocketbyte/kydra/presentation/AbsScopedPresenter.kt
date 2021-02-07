package ru.pocketbyte.kydra.presentation

import ru.pocketbyte.kydra.presentation.ScopedPresenter.State

abstract class AbsScopedPresenter: ScopedPresenter {

    final override var state = State.NONE
        private set

    final override fun prepare() {
        checkNotDestroyed()
        if (this.state == State.NONE) {
            this.state = State.PREPARED
            onPrepare()
        } else {
            throw IllegalStateException("Presenter can be prepared only once")
        }
    }

    final override fun start() {
        checkNotDestroyed()
        checkPrepared()
        this.state = State.STARTED
        onStart()
    }

    final override fun stop() {
        checkNotDestroyed()
        checkPrepared()
        this.state = State.STOPPED
        onStop()
    }

    final override fun destroy() {
        checkNotDestroyed()
        checkPrepared()
        this.state = State.DESTROYED
        onDestroy()
    }

    /**
     * Called when presenter goes to PREPARED state.
     */
    protected open fun onPrepare() { }

    /**
     * Called when presenter goes to STARTED state.
     */
    protected open fun onStart() { }

    /**
     * Called when presenter goes to STOPPED state.
     */
    protected open fun onStop() { }

    /**
     * Called when presenter goes to DESTROYED state.
     */
    protected open fun onDestroy() { }

    private fun checkNotDestroyed() {
        if (this.state == State.DESTROYED) {
            throw IllegalStateException("Presenter was destroyed and can't be used anymore")
        }
    }

    private fun checkPrepared() {
        if (this.state == State.NONE) {
            throw IllegalStateException("Presenter must be prepared before use")
        }
    }
}