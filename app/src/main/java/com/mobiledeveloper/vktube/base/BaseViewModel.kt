package com.mobiledeveloper.vktube.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

abstract class BaseViewModel<State, Action, Event>(
    initialState: State
) : ViewModel() {

    private val _viewStates: MutableStateFlow<State> = MutableStateFlow(initialState)
    fun viewStates(): StateFlow<State> = _viewStates

    private var _viewState: State = initialState
    protected val viewState: State = _viewState

    private val _viewActions: Channel<Action?> = Channel()
    fun viewActions(): Flow<Action?> = _viewActions.receiveAsFlow()

    abstract fun obtainEvent(viewEvent: Event)

    protected suspend fun updateState(state: State) {
        _viewState = state
        withContext(viewModelScope.coroutineContext) {
            _viewStates.emit(state)
        }
    }

    protected suspend fun callAction(action: Action?) =
        withContext(viewModelScope.coroutineContext) {
            _viewActions.send(action)
        }

    /**
     * Вспомогательная обертка над [viewModelScope], которая возвращает [Unit], а не [Job], чтобы
     * можно было использовать данный метод, для методов, где возвращаемый тип определен как [Unit].
     *
     * Пример:
     * ```
     * override fun obtainEvent(viewEvent: StoreDetailEvent) = withViewModelScope {
     *      ..
     * }
     * ```
     */
    protected fun withViewModelScope(block: suspend CoroutineScope.() -> Unit) {
        viewModelScope.launch {
            block.invoke(this)
        }
    }
}