package ru.pocketbyte.kydra.presentation

import ru.pocketbyte.kydra.presentation.ScopedPresenter.State

abstract class AbsPresentersSet: AbsScopedPresenter() {

    protected abstract fun isCurrentThreadValid(): Boolean

    private val presenters: MutableSet<ScopedPresenter> = mutableSetOf()

    /**
     * Adds presenter into set.
     * **Note:** This method can be called only in Main thread.
     * @param presenter The presenter that should be added into set
     */
    fun add(presenter: ScopedPresenter) {
        checkThread()
        presenters.add(presenter)

        if (state != State.NONE)
            prepareVisitor(presenter)

        when (state) {
            State.STARTED -> startVisitor(presenter)
            State.STOPPED -> {
                startVisitor(presenter)
                stopVisitor(presenter)
            }
            State.DESTROYED -> destroyVisitor(presenter)
            else -> {} // Do nothing
        }
    }

    /**
     * Call provided function for all presenters from set.
     * **Note:** This method can be called only in Main thread.
     * @param function The visitor function that should be called for each presenter in set.
     * If function returns true the visiting process is breaks.
     * @return True if any visitor function returns true, false otherwise.
     */
    fun visitAll(function: (presenter: ScopedPresenter) -> Boolean): Boolean {
        checkThread()
        for (presenter in presenters) {
            if (function(presenter))
                return true
        }
        return false
    }

    override fun onPrepare() {
        super.onPrepare()
        visitAll(prepareVisitor)
    }

    override fun onStart() {
        super.onStart()
        visitAll(startVisitor)
    }

    override fun onStop() {
        super.onStop()
        visitAll(stopVisitor)
    }

    override fun onDestroy() {
        super.onDestroy()
        visitAll(destroyVisitor)
    }

    private val prepareVisitor: (presenter: ScopedPresenter) -> Boolean = { presenter ->
        if (presenter.state == State.NONE) {
            presenter.prepare()
        }
        false
    }

    private val startVisitor: (presenter: ScopedPresenter) -> Boolean = { presenter ->
        if (presenter.state != State.STARTED) {
            presenter.start()
        }
        false
    }

    private val stopVisitor: (presenter: ScopedPresenter) -> Boolean = { presenter ->
        if (presenter.state != State.STOPPED) {
            presenter.stop()
        }
        false
    }

    private val destroyVisitor: (presenter: ScopedPresenter) -> Boolean = { presenter ->
        if (presenter.state != State.DESTROYED) {
            presenter.destroy()
        }
        false
    }

    private fun checkThread() {
        if (!isCurrentThreadValid()) {
            throw RuntimeException(
                "Invalid thread. Presenter expect to be changed in UI thread"
            )
        }
    }
}