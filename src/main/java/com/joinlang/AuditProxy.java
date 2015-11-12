package com.joinlang;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.time.Duration;
import java.time.Instant;
import java.util.function.Function;

public class AuditProxy implements InvocationHandler {

    private Object obj;

    private AuditProxy(Object obj) {
        this.obj = obj;
    }

    public static Object newInstance(Object obj) {
        return Proxy.newProxyInstance(obj.getClass().getClassLoader(), obj.getClass().getInterfaces(), new AuditProxy(obj));
    }

    @Override
    public Object invoke(Object proxy, Method m, Object[] args) throws Throwable {
        Object result;
        Instant start = Instant.now();
        try {
            result = m.invoke(obj, args);
        } catch (InvocationTargetException e) {
            throw e.getTargetException();
        } catch (Exception e) {
            throw new RuntimeException("unexpected invocation exception: " + e.getMessage());
        } finally {
            Instant end = Instant.now();
            System.out.println("The nanoseconds : " + Duration.between(start, end).getNano());
        }
        return result;
    }

    public static <T, V> T apply(V args, Function<V, T> function) {
        Instant start = Instant.now();
        T result = function.apply(args);
        Instant end = Instant.now();
        System.out.println("The nanoseconds : " + Duration.between(start, end).getNano());
        return result;
    }
 }
