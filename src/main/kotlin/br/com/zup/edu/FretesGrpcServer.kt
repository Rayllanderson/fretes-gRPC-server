package br.com.zup.edu

import io.grpc.stub.StreamObserver
import org.slf4j.LoggerFactory
import javax.inject.Singleton
import kotlin.math.round
import kotlin.random.Random

@Singleton
class FretesGrpcServer: FretesServiceGrpc.FretesServiceImplBase() {

    private val logger = LoggerFactory.getLogger(FretesServiceGrpc::class.java)

    override fun calculaFrete(request: CalculaFreteRequest?, responseObserver: StreamObserver<CalculaFreteResponse>?) {
        logger.info("Calculando frete para request: $request")


        val valorFrete = round(Random.nextDouble(from = 20.0, until = 180.0))

        val response = CalculaFreteResponse.newBuilder()
            .setCep(request?.cep)
            .setValor(valorFrete)
            .build()

        logger.info("Frete calculado: $valorFrete")

        responseObserver?.onNext(response)
        responseObserver?.onCompleted()

    }
}