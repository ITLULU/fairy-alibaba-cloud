package com.fairy.cloud.gateway.exception.gateway;//package com.fairy.cloud.gateway.exception.gateway;
//
//import com.fairy.common.exception.GateWayException;
//import com.fairy.common.response.Result;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
//import org.springframework.cloud.gateway.support.NotFoundException;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.MediaType;
//import org.springframework.http.codec.HttpMessageReader;
//import org.springframework.http.codec.HttpMessageWriter;
//import org.springframework.http.server.reactive.ServerHttpRequest;
//import org.springframework.util.Assert;
//import org.springframework.web.reactive.function.BodyInserters;
//import org.springframework.web.reactive.function.server.RequestPredicates;
//import org.springframework.web.reactive.function.server.RouterFunctions;
//import org.springframework.web.reactive.function.server.ServerRequest;
//import org.springframework.web.reactive.function.server.ServerResponse;
//import org.springframework.web.reactive.result.view.ViewResolver;
//import org.springframework.web.server.ResponseStatusException;
//import org.springframework.web.server.ServerWebExchange;
//import reactor.core.publisher.Mono;
//
//import java.time.Instant;
//import java.util.Collections;
//import java.util.List;
//
///**
// * @author 鹿少年
// * @date 2022/7/30 12:13
// */
//@Deprecated
//@Slf4j
//public class GlobalJsonExceptionHandler implements ErrorWebExceptionHandler {
//    /**
//     * MessageReader
//     */
//    private List<HttpMessageReader<?>> messageReaders = Collections.emptyList();
//
//    /**
//     * MessageWriter
//     */
//    private List<HttpMessageWriter<?>> messageWriters = Collections.emptyList();
//
//    /**
//     * ViewResolvers
//     */
//    private List<ViewResolver> viewResolvers = Collections.emptyList();
//
//    /**
//     * 存储处理异常后的信息
//     */
//    private ThreadLocal<Result> exceptionHandlerResult = new ThreadLocal<>();
//
//    /**
//     * 参考AbstractErrorWebExceptionHandler
//     */
//    public void setMessageReaders(List<HttpMessageReader<?>> messageReaders) {
//        Assert.notNull(messageReaders, "'messageReaders' must not be null");
//        this.messageReaders = messageReaders;
//    }
//
//    /**
//     * 参考AbstractErrorWebExceptionHandler
//     */
//    public void setViewResolvers(List<ViewResolver> viewResolvers) {
//        this.viewResolvers = viewResolvers;
//    }
//
//    /**
//     * 参考AbstractErrorWebExceptionHandler
//     */
//    public void setMessageWriters(List<HttpMessageWriter<?>> messageWriters) {
//        Assert.notNull(messageWriters, "'messageWriters' must not be null");
//        this.messageWriters = messageWriters;
//    }
//
//    @Override
//    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
//        // 按照异常类型进行处理
//        HttpStatus httpStatus;
//        String body;
//        if (ex instanceof NotFoundException) {
//            httpStatus = HttpStatus.NOT_FOUND;
//            body = HttpStatus.NOT_FOUND.getReasonPhrase();
//        } else if (ex instanceof ResponseStatusException) {
//            ResponseStatusException responseStatusException = (ResponseStatusException) ex;
//            httpStatus = responseStatusException.getStatus();
//            body = httpStatus.getReasonPhrase();
//        } else if(ex instanceof GateWayException){
//            httpStatus = HttpStatus.BAD_GATEWAY;
//            body = ((GateWayException) ex).getMsg();
//        }else {
//            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
//            body = HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase();
//        }
//        Result  result = new Result();
//        result.setData(body);
//        result.setCode(String.valueOf(httpStatus.value()));
//        result.setSuccess(false);
//        result.setTime(Instant.now());
//        result.setMsg(ex.getMessage());
//        //错误记录
//        ServerHttpRequest request = exchange.getRequest();
//        log.error("[全局异常处理]异常请求路径:{},记录异常信息:{}", request.getPath(), ex.getMessage());
//        //参考AbstractErrorWebExceptionHandler
//        if (exchange.getResponse().isCommitted()) {
//            return Mono.error(ex);
//        }
//        exceptionHandlerResult.set(result);
//        ServerRequest newRequest = ServerRequest.create(exchange, this.messageReaders);
//        return RouterFunctions.route(RequestPredicates.all(), this::renderErrorResponse).route(newRequest)
//                .switchIfEmpty(Mono.error(ex))
//                .flatMap((handler) -> handler.handle(newRequest))
//                .flatMap((response) -> write(exchange, response));
//
//    }
//
//    /**
//     * 参考DefaultErrorWebExceptionHandler
//     */
//    protected Mono<ServerResponse> renderErrorResponse(ServerRequest request) {
//       Result result = exceptionHandlerResult.get();
//        return ServerResponse.status( HttpStatus.valueOf(Integer.valueOf(result.getCode())))
//                .contentType(MediaType.APPLICATION_JSON_UTF8)
//                .body(BodyInserters.fromObject(result));
//    }
//
//    /**
//     * 参考AbstractErrorWebExceptionHandler
//     */
//    private Mono<? extends Void> write(ServerWebExchange exchange,
//                                       ServerResponse response) {
//        exchange.getResponse().getHeaders()
//                .setContentType(MediaType.APPLICATION_JSON);
//        return response.writeTo(exchange, new ResponseContext());
//    }
//
//    /**
//     * 参考AbstractErrorWebExceptionHandler
//     */
//    private class ResponseContext implements ServerResponse.Context {
//
//        @Override
//        public List<HttpMessageWriter<?>> messageWriters() {
//            return GlobalJsonExceptionHandler.this.messageWriters;
//        }
//
//        @Override
//        public List<ViewResolver> viewResolvers() {
//            return GlobalJsonExceptionHandler.this.viewResolvers;
//        }
//    }
//}
