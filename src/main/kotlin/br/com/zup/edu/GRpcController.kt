package br.com.zup.edu

import io.micronaut.grpc.server.GrpcEmbeddedServer
import io.micronaut.http.HttpResponse
import io.micronaut.http.MutableHttpResponse
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get

@Controller("/grpc-server")
class GRpcController(
    val grpcServer: GrpcEmbeddedServer
) {

    @Get("/stop")
    fun stopServer(): MutableHttpResponse<String> {
        grpcServer.stop()
        return HttpResponse.ok("is running? ${grpcServer.isRunning}")
    }
}