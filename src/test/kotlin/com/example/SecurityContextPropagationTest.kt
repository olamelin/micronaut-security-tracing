package com.example

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.micronaut.http.HttpMethod
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.annotation.Client
import io.micronaut.http.simple.SimpleHttpRequest
import io.micronaut.test.extensions.kotest5.annotation.MicronautTest

@MicronautTest
class SecurityContextPropagationTest(@Client("/") val client: HttpClient) : StringSpec({

    // Changing micronaut.security.enabled to false will make this test pass
    // Putting the CustomTracingFilter after the security filter phase will cause the test to pass as well
    "tracing should be propagated even with security" {
        val httpRequest = SimpleHttpRequest(HttpMethod.GET, "/my-controller", "")
            .header("X-trace", "Hello World")
        val resp: String = client.toBlocking().retrieve(httpRequest)

        resp shouldBe """{"value":"Hello World"}"""
    }
})
