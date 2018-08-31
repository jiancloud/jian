package com.cloud.jian.core.lambda;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class LambdaApplication {

    public static void main(String[] args) {
        LambdaApplication la = new LambdaApplication();
        System.out.println(la.countPrimes(1000));
        ThreadLocal<DateFormat> threadLocal = ThreadLocal.withInitial(() -> new SimpleDateFormat("yyyy-MM-dd"));
        AtomicInteger threadId = new AtomicInteger();
        ThreadLocal<Integer> localId = ThreadLocal.withInitial(() -> threadId.getAndIncrement());
        List<String> cities = new ArrayList(){{
            add("New York");
            add("New York");
            add("New York");
        }};
        List<String> c1 = Stream.of("New York", "Beijing").collect(Collectors.toList());
    }

    public long countPrimes(int upTo) {
        return IntStream.range(1, upTo).parallel().filter(this::isPrime).count();
    }

    public boolean isPrime(int n) {
        return IntStream.range(2, n).allMatch(x -> (n % x) != 0);
    }

    public List<String> findTitles(Reader input) {
        try (BufferedReader reader = new BufferedReader(input)) {
            return reader.lines().filter(line -> line.endsWith(":")).map(line -> line.substring(0, line.length() - 1)).collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public List<String> findHeadings(Reader input) {
        return withLinesOf(input,
                lines -> lines.filter(line -> line.endsWith(":"))
                        .map(line -> line.substring(0, line.length() - 1))
                        .collect(Collectors.toList()),
                RuntimeException::new);
    }

    private <T> T withLinesOf(Reader input,
                              Function<Stream<String>, T> handler,
                              Function<IOException, RuntimeException> error) {
        try (BufferedReader reader = new BufferedReader(input)) {
            return handler.apply(reader.lines());
        } catch (IOException e) {
            throw error.apply(e);
        }
    }

}
