package com.example.reactive.framework;

public interface Publisher<T> {

    void subscribe(Subscriber<? super T> subscriber);
}
