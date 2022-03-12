package com.mobiledeveloper.vktube.base

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

abstract class BaseViewModel<State, Action, Event>(
    initialState: State
) : ViewModel() {

    private val _viewStates: MutableLiveData<State> = MutableLiveData()
    fun viewStates(): LiveData<State> = _viewStates

    private var _viewState: State? = null
    protected val viewState: State
        @Synchronized get() = _viewState
            ?: throw UninitializedPropertyAccessException("\"viewState\" was queried before being initialized")
    private val _viewActions: SingleLiveAction<Action?> = SingleLiveAction()
    fun viewEffects(): SingleLiveAction<Action?> = _viewActions

    init {
        _viewState = initialState
        _viewStates.value = initialState!!
    }

    abstract fun obtainEvent(viewEvent: Event)

    protected suspend fun updateState(state: State) {
        _viewState = state
        withContext(viewModelScope.coroutineContext) {
            _viewStates.value = state!!
        }
    }

    protected suspend fun callAction(action: Action?) =
        withContext(viewModelScope.coroutineContext) {
            _viewActions.value = action
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