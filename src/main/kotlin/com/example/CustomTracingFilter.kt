package com.example

import io.micronaut.core.propagation.PropagatedContext
import io.micronaut.core.propagation.ThreadPropagatedContextElement
import io.micronaut.http.HttpRequest
import io.micronaut.http.MutableHttpResponse
import io.micronaut.http.annotation.Filter
import io.micronaut.http.annotation.Filter.MATCH_ALL_PATTERN
import io.micronaut.http.filter.HttpServerFilter
import io.micronaut.http.filter.ServerFilterChain
import io.micronaut.http.filter.ServerFilterPhase
import org.reactivestreams.Publisher


@Filter(MATCH_ALL_PATTERN)
class CustomTracingFilter : HttpServerFilter {

    override fun getOrder(): Int {
        return ServerFilterPhase.TRACING.after()
        // Replace order to after security to make the test pass
//        return ServerFilterPhase.SECURITY.after()
    }

    override fun doFilter(request: HttpRequest<*>, chain: ServerFilterChain): Publisher<MutableHttpResponse<*>> {
        val trace = request.headers.get("X-Trace")
        PropagatedContext.get().plus(MyPropagationContext(trace)).propagate().use {
            return chain.proceed(request)
        }
    }
}

data class MyPropagationContext(val state: String) : ThreadPropagatedContextElement<String?> {

    override fun updateThreadContext(): String? {
        val oldState = ContextOnThread.getValue()
        ContextOnThread.setValue(state)
        return oldState
    }

    override fun restoreThreadContext(oldState: String?) {
        if (oldState == null) {
            ContextOnThread.unsetValue()
        } else {
            ContextOnThread.setValue(oldState)
        }
    }

}

object ContextOnThread {
    private val threadValue = ThreadLocal<String?>()

    fun setValue(value: String) {
        threadValue.set(value)
    }

    fun unsetValue() {
        threadValue.remove()
    }

    fun getValue(): String? {
        return threadValue.get()
    }
}
