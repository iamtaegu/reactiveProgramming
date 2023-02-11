package com.example.reactive.framework;

/**
 * Subscriber Publisher I/F 결합
 * Subscriber 역할로 데이터를 수신하고 처리
 * 그 다음 Publisher 역할로 처리 결과를 자신의 Subscriber들에게 발행
 * @param <T>
 * @param <R>
 */
public interface Processor<T, R> extends Subscriber<T>, Publisher<R> {

}
