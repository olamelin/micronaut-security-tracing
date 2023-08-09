package com.example

import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.Produces
import io.micronaut.serde.annotation.Serdeable
import jakarta.annotation.security.PermitAll

@Controller("/my-controller")
@Produces(MediaType.APPLICATION_JSON)
class Controller {

    @Get("/")
    @PermitAll
    fun get(): HttpResponse<Response> {
        return HttpResponse.status<Response>(HttpStatus.OK).body(Response(ContextOnThread.getValue()))
    }
}

@Serdeable
data class Response(val value: String?)
