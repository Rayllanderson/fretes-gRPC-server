package br.com.zup.edu

import com.google.protobuf.Any
import com.google.rpc.Code
import io.grpc.Status
import io.grpc.protobuf.StatusProto
import com.google.rpc.Status as StatusGrpc
import io.grpc.stub.StreamObserver
import org.slf4j.LoggerFactory
import java.lang.IllegalStateException
import javax.inject.Singleton
import kotlin.math.round
import kotlin.random.Random

@Singleton
class FretesGrpcServer : FretesServiceGrpc.FretesServiceImplBase() {

    private val logger = LoggerFactory.getLogger(FretesServiceGrpc::class.java)

    override fun calculaFrete(request: CalculaFreteRequest?, responseObserver: StreamObserver<CalculaFreteResponse>?) {
        logger.info("Calculando frete para request: $request")

        val cep = request?.cep

        if (cep.isNullOrBlank()) {
            val e = Status.INVALID_ARGUMENT.withDescription("Cep deve ser informado").asRuntimeException()
            responseObserver?.onError(e)
        }

        val cepNaoEstaFormatado = !cep!!.matches("[0-9]{5}-[0-9]{3}".toRegex())
        if (cepNaoEstaFormatado) {
            val e = Status.INVALID_ARGUMENT
                .withDescription("Cep inválido")
                .augmentDescription("Formato esperado deve ser: 99999-999")
                .asRuntimeException()
            responseObserver?.onError(e)
        }

        // simulando uma verificação de segurança
        if (cep.endsWith("333")) {
            val statusProto= StatusGrpc.newBuilder()
                .setCode(Code.PERMISSION_DENIED.number)
                .setMessage("Usuário não pode acessar esse recurso")
                .addDetails(Any.pack(ErroDetails.newBuilder()
                    .setCode(401)
                    .setMensagem("Token expirado")
                    .build()
                ))
                .build()
            val e = StatusProto.toStatusRuntimeException(statusProto)
            responseObserver?.onError(e)
        }

        var valorFrete = 0.0
        try {
            valorFrete = round(Random.nextDouble(from = 20.0, until = 180.0))
            if(valorFrete > 110) throw IllegalStateException("Erro inesperado ao executar lógica de negócio!")
        } catch (e: Exception) {
            responseObserver?.onError(Status.INTERNAL
                .withDescription(e.message)
                .withCause(e)
                .asRuntimeException()
            )
        }

        val response = CalculaFreteResponse.newBuilder()
            .setCep(cep)
            .setValor(valorFrete)
            .build()

        logger.info("Frete calculado: $valorFrete")

        responseObserver?.onNext(response)
        responseObserver?.onCompleted()

    }
}