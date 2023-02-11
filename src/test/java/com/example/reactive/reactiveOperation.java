package com.example.reactive;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;
import reactor.util.function.Tuple2;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class reactiveOperation {

    @Test
    public void createAFlux_just() {
        // 다섯개의 String 객체로부터 Flux를 생성
        // Flux는 생성되지만 구독자(subscriber)가 없음
        Flux<String> fruitFlux = Flux.just("Apple", "Orange", "Grape", "Banana", "Strawberry");

        // 구독자 추가
        // 여기서 subscribe()에 지정된 람다는 실제로는 java.util.Consumer이고
        // 이것은 리액티브 스트림의 Subscriber 객체를 생성하기 위해 사용

        // subscribe를 호출하는 즉시 데이터가 전달되기 시작하고,
        // 이 예제에서는 다른 오퍼레이션이 없으므로 데이터는 곧바로 Flux로부터 Subscriber로 전달
        fruitFlux.subscribe(
                f -> System.out.println("Here's some fruit: " + f)
        );

        // StepVerifier가 fruitFlux를 구독한 후 각 데이터 항목에 어서션(assertion)을 적용해줌
        StepVerifier.create(fruitFlux)
                .expectNext("Apple")
                .expectNext("Orange")
                .expectNext("Grape")
                .expectNext("Banana")
                .expectNext("Strawberry")
                .verifyComplete();
    }

    /**
     * 배열로부터 Flux를 생성하려면 static 메서드인 fromArray()를 호출하며
     * 이때 소스 배열을 인자로 전달
     */
    @Test
    public void createAFlux_fromArray() {
        String[] fruits = new String[] {
            "Apple", "Orange", "Grape", "Banana", "Strawberry"
        };

        Flux<String> fruitFlux = Flux.fromArray(fruits);

        StepVerifier.create(fruitFlux)
                .expectNext("Apple")
                .expectNext("Orange")
                .expectNext("Grape")
                .expectNext("Banana")
                .expectNext("Strawberry")
                .verifyComplete();
    }

    @Test
    public void createAFlux_fromIterable() {
        List<String> fruitList = new ArrayList<>();
        fruitList.add("apple");
        fruitList.add("orange");

        Flux<String> fruitFlux = Flux.fromIterable(fruitList);

        StepVerifier.create(fruitFlux)
                .expectNext("apple")
                .expectNext("orange")
                .verifyComplete();

    }

    @Test
    public void createAFlux_fromStream() {
        Stream<String> fruitStream = Stream.of("apple", "orange");

        Flux<String> fruitFlux = Flux.fromStream(fruitStream);

        StepVerifier.create(fruitFlux)
                .expectNext("apple")
                .expectNext("orange")
                .verifyComplete();
    }

    @Test
    public void createAFlux_range() {
        Flux<Integer> intervalFlux =
                Flux.range(1, 5);

        StepVerifier.create(intervalFlux)
                .expectNext(1)
                .expectNext(2)
                .expectNext(3)
                .expectNext(4)
                .expectNext(5)
                .verifyComplete();
    }

    @Test
    public void createAFlux_interval() {
        Flux<Long> intervalFlux =
                Flux.interval(Duration.ofSeconds(1))
                        .take(5);

        StepVerifier.create(intervalFlux)
                .expectNext(0L)
                .expectNext(1L)
                .expectNext(2L)
                .expectNext(3L)
                .expectNext(4L)
                .verifyComplete();
    }

    /**
     * 두 개의 Flux 스트림을 하나의 결과 Flux로 생성
     * Flux는 가능한 빨리 데이터를 방출하기 때문에
     * 생성되는 Flux 스트림 두 개 모두에 delay 적용
     */
    @Test
    public void mergeFluxes() {

        Flux<String> chracterFlux = Flux
                .just("Garfield", "Kojak", "Barbossa")
                .delayElements(Duration.ofMillis(500));
        Flux<String> foodFlux = Flux
                .just("Lasagna", "Lollipops", "Apples")
                .delaySubscription(Duration.ofMillis(250))
                .delayElements(Duration.ofMillis(500));

        Flux<String> mergedFlux = chracterFlux.mergeWith(foodFlux);

        StepVerifier.create(mergedFlux)
                .expectNext("Garfield")
                .expectNext("Lasagna")
                .expectNext("Kojak")
                .expectNext("Lollipops")
                .expectNext("Barbossa")
                .expectNext("Apples")
                .verifyComplete();
    }

    /**
     * zip 오퍼레이션은 각 Flux 소스로부터
     * 한 항목씩 번갈아 가져와 새로운 Flux를 생성
     */
    @Test
    public void zipFluxes() {
        Flux<String> chracterFlux = Flux
                .just("Garfield", "Kojak", "Barbossa");
        Flux<String> foodFlux = Flux
                .just("Lasagna", "Lollipops", "Apples");

        Flux<Tuple2<String, String>> zippedFlux = Flux.zip(chracterFlux, foodFlux);

        StepVerifier.create(zippedFlux)
                .expectNextMatches(p ->
                        p.getT1().equals("Garfield") &&
                        p.getT2().equals("Lasagna"))
                .expectNextMatches(p ->
                        p.getT1().equals("Kojak") &&
                        p.getT2().equals("Lollipops"))
                .expectNextMatches(p ->
                        p.getT1().equals("Barbossa") &&
                        p.getT2().equals("Apples"))
                .verifyComplete();
    }

    @Test
    public void zipFluxesToObject() {
        Flux<String> chracterFlux = Flux
                .just("Garfield", "Kojak", "Barbossa");
        Flux<String> foodFlux = Flux
                .just("Lasagna", "Lollipops", "Apples");

        Flux<String> zippedFlux =
                Flux.zip(chracterFlux, foodFlux, (c,f) -> c + " eats " + f);

        StepVerifier.create(zippedFlux)
                .expectNext("Garfield eats Lasagna")
                .expectNext("Kojak eats Lollipops")
                .expectNext("Barbossa eats Apples")
                .verifyComplete();
    }

    /**
     * 느린 Flux 0.5초 경과 후 구독 신청과 발행 하므로
     * 새러 생성되는 Flux는 느린 Flux를 무시하고 빠른 Flux의 값만 발행
     */
    @Test
    public void firstFlux() {
        Flux<String> slowFlux = Flux
                .just("Garfield", "Kojak", "Barbossa")
                .delayElements(Duration.ofMillis(500));
        Flux<String> fastFlux = Flux
                .just("Lasagna", "Lollipops", "Apples");

        Flux<String> firstFlux = Flux.first(slowFlux, fastFlux);

        StepVerifier.create(firstFlux)
                .expectNext("Lasagna")
                .expectNext("Lollipops")
                .expectNext("Apples")
                .verifyComplete();

    }

    /**
     * 항목 건너뛰기
     */
    @Test
    public void skipAfew() {
        Flux<String> skipFlux = Flux.just(
                "one", "two", "skip a few", "ninety nine", "one hundred"
        ).skip(3);

        StepVerifier.create(skipFlux)
                .expectNext("ninety nine")
                .expectNext("one hundred")
                .verifyComplete();
    }

    /**
     * 일정 시간 건너뛰기
     */
    @Test
    public void skipAFewSeconds() {
        Flux<String> skipFlux = Flux.just(
                "one", "two", "skip a few", "ninety nine", "one hundred")
                .delayElements(Duration.ofSeconds(1))
                .skip(Duration.ofSeconds(4)); //4초부터 방출

        StepVerifier.create(skipFlux)
                .expectNext("ninety nine")
                .expectNext("one hundred")
                .verifyComplete();
    }

    /**
     * 지정된 수의 항목만 방출
     */
    @Test
    public void take() {
        Flux<String> skipFlux = Flux.just(
                "one", "two", "skip a few", "ninety nine", "one hundred")
                .take(3);

        StepVerifier.create(skipFlux)
                .expectNext("one","two","skip a few")
                .verifyComplete();

    }

    /**
     * 일정 시간 동안의 항목만 방출
     */
    @Test
    public void takeAFewSeconds() {
        Flux<String> skipFlux = Flux.just(
                        "one", "two", "skip a few", "ninety nine", "one hundred")
                .delayElements(Duration.ofSeconds(1))
                .take(Duration.ofMillis(3500)); //3.5초간 방출

        StepVerifier.create(skipFlux)
                .expectNext("one","two","skip a few")
                .verifyComplete();
    }

    /**
     * 방출 항목을 결정하는 조건식을 지정
     */
    @Test
    public void filter() {
        Flux<String> skipFlux = Flux.just(
                "one", "two", "skip a few", "ninety nine", "one hundred")
                .filter(sf -> !sf.contains(" "));
        StepVerifier.create(skipFlux)
                .expectNext("one", "two")
                .verifyComplete();
    }

    /**
     * 중복되지 않는 항목 방출
     */
    @Test
    public void distinct() {
        Flux<String> animalFlux = Flux.just(
                "dog", "cat", "bird", "dog", "bird"
        ).distinct();

        StepVerifier.create(animalFlux)
                .expectNext("dog", "cat", "bird")
                .verifyComplete();
    }


}
