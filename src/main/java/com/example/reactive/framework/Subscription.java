package com.example.reactive.framework;

public interface Subscription {
    void request(long n);
    void cancel();
}
