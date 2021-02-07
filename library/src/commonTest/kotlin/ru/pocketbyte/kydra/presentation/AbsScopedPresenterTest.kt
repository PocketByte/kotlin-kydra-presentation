package ru.pocketbyte.kydra.presentation

import ru.pocketbyte.kydra.presentation.ScopedPresenter.State
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class AbsScopedPresenterTest {

    @Test
    fun testNormalFlow() {
        val presenter = AbsScopedPresenterImpl()

        assertEquals(State.NONE, presenter.state)

        presenter.prepare()
        assertEquals(State.PREPARED, presenter.state)

        presenter.start()
        assertEquals(State.STARTED, presenter.state)

        presenter.stop()
        assertEquals(State.STOPPED, presenter.state)

        presenter.destroy()
        assertEquals(State.DESTROYED, presenter.state)
    }

    @Test
    fun testUseWithoutPreparation() {
        assertFailsWith(IllegalStateException::class) {
            val presenter = AbsScopedPresenterImpl()
            presenter.start()
        }

        assertFailsWith(IllegalStateException::class) {
            val presenter = AbsScopedPresenterImpl()
            presenter.stop()
        }

        assertFailsWith(IllegalStateException::class) {
            val presenter = AbsScopedPresenterImpl()
            presenter.destroy()
        }
    }

    @Test
    fun testSecondPreparation() {
        val getPreparedPresenter: () -> ScopedPresenter = {
            AbsScopedPresenterImpl().apply {
                prepare()
            }
        }
        assertFailsWith(IllegalStateException::class) {
            val presenter = getPreparedPresenter()
            presenter.prepare()
        }
        assertFailsWith(IllegalStateException::class) {
            val presenter = getPreparedPresenter()
            presenter.stop()
            presenter.prepare()
        }
        assertFailsWith(IllegalStateException::class) {
            val presenter = getPreparedPresenter()
            presenter.start()
            presenter.prepare()
        }
    }

    @Test
    fun testUseAfterDestroy() {
        val getDestroyedPresenter: () -> ScopedPresenter = {
            AbsScopedPresenterImpl().apply {
                prepare()
                destroy()
            }
        }
        assertFailsWith(IllegalStateException::class) {
            val presenter = getDestroyedPresenter()
            presenter.prepare()
        }
        assertFailsWith(IllegalStateException::class) {
            val presenter = getDestroyedPresenter()
            presenter.stop()
        }
        assertFailsWith(IllegalStateException::class) {
            val presenter = getDestroyedPresenter()
            presenter.start()
        }
        assertFailsWith(IllegalStateException::class) {
            val presenter = getDestroyedPresenter()
            presenter.destroy()
        }
    }

    private class AbsScopedPresenterImpl: AbsScopedPresenter()
}