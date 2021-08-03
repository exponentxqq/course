package com.course.concurrency04.question02;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionService;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/** 思考有多少种方式，在 main 函数启动一个新线程，运行一个方法，拿到这个方法的返回值后，退出主线程 */
public class HomeWork {

  public static void main(String[] args) throws Exception {
    System.out.printf("method01: \t%s\t(Thread.join)%n", method1());
    System.out.printf("method02: \t%s\t(CountDownLatch)%n", method2());
    System.out.printf("method03: \t%s\t(Semaphore)%n", method3());
    System.out.printf("method04: \t%s\t(CyclicBarrier)%n", method4());
    System.out.printf("method05: \t%s\t(Lock)%n", method5());
    System.out.printf("method06: \t%s\t(while)%n", method6());
    System.out.printf("method07: \t%s\t(Object.wait/notify)%n", method7());
    System.out.printf("method08: \t%s\t(FutureTask)%n", method8());
    System.out.printf("method09: \t%s\t(CompletableFuture.supplyAsync.get)%n", method9());
    System.out.printf("method10: \t%s\t(ExecutorService)%n", method10());
    System.out.printf("method11: \t%s\t(CompletionService)%n", method11());
    System.out.printf("method12: \t%s\t(ArrayBlockingQueue)%n", method12());
    System.out.printf(
        "method13: \t%s\t(CompletableFuture.supplyAsync.thenAccept.join)%n", method13());
    System.out.printf("method14: \t%s\t(CompletableFuture.runAsync.join)%n", method14());
    System.out.printf(
        "method15: \t%s\t(CompletableFuture.supplyAsync.thenApply.join)%n", method15());
  }

  private static int method1() throws InterruptedException {
    AtomicInteger result = new AtomicInteger();
    final Thread thread =
        new Thread(
            () -> {
              final int res = fibo();
              result.set(res);
            });
    thread.start();
    thread.join();
    return result.get();
  }

  private static int method2() throws InterruptedException {
    AtomicInteger result = new AtomicInteger();
    CountDownLatch countDownLatch = new CountDownLatch(1);
    new Thread(
            () -> {
              result.set(fibo());
              countDownLatch.countDown();
            })
        .start();
    countDownLatch.await();
    return result.get();
  }

  private static int method3() throws InterruptedException {
    final AtomicInteger result = new AtomicInteger();
    final Semaphore semaphore = new Semaphore(1);
    new Thread(
            () -> {
              try {
                semaphore.acquireUninterruptibly();
                result.set(fibo());
              } finally {
                semaphore.release();
              }
            })
        .start();
    Thread.sleep(100);
    semaphore.acquireUninterruptibly();
    return result.get();
  }

  private static int method4() throws Exception {
    final AtomicInteger result = new AtomicInteger();
    final CyclicBarrier cyclicBarrier = new CyclicBarrier(2);
    new Thread(
            () -> {
              result.set(fibo());
              try {
                cyclicBarrier.await();
              } catch (InterruptedException | BrokenBarrierException e) {
              }
            })
        .start();
    cyclicBarrier.await();
    return result.get();
  }

  private static int method5() throws InterruptedException {
    final AtomicInteger result = new AtomicInteger();
    Lock lock = new ReentrantLock(true);
    new Thread(
            () -> {
              try {
                lock.lock();
                result.set(fibo());
              } finally {
                lock.unlock();
              }
            })
        .start();
    try {
      Thread.sleep(100);
      lock.lock();
      return result.get();
    } finally {
      lock.unlock();
    }
  }

  private static int method6() {
    AtomicInteger result = new AtomicInteger();
    new Thread(() -> result.set(fibo())).start();
    while (result.get() == 0) {}
    return result.get();
  }

  private static int method7() throws InterruptedException {
    AtomicInteger result = new AtomicInteger();
    new Thread(
            () -> {
              synchronized (result) {
                result.set(fibo());
                result.notify();
              }
            })
        .start();
    synchronized (result) {
      result.wait();
    }
    return result.get();
  }

  private static int method8() throws ExecutionException, InterruptedException {
    Future<Integer> future = new FutureTask<>(HomeWork::fibo);
    new Thread((Runnable) future).start();
    return future.get();
  }

  private static int method9() throws ExecutionException, InterruptedException {
    final CompletableFuture<Integer> completableFuture =
        CompletableFuture.supplyAsync(HomeWork::fibo);
    return completableFuture.get();
  }

  private static int method10() throws ExecutionException, InterruptedException {
    final ExecutorService executorService = Executors.newSingleThreadExecutor();
    try {
      final Future<Integer> future = executorService.submit((Callable<Integer>) HomeWork::fibo);
      return future.get();
    } finally {
      executorService.shutdown();
    }
  }

  private static int method11() throws InterruptedException, ExecutionException {
    final ExecutorService executorService = Executors.newSingleThreadExecutor();
    CompletionService<Integer> completionService = new ExecutorCompletionService<>(executorService);
    completionService.submit(HomeWork::fibo);
    try {
      final Future<Integer> future = completionService.take();
      return future.get();
    } finally {
      executorService.shutdown();
    }
  }

  private static int method12() throws InterruptedException {
    final ArrayBlockingQueue<Integer> queue = new ArrayBlockingQueue<>(1);
    new Thread(
            () -> {
              try {
                queue.put(fibo());
              } catch (InterruptedException e) {
                // ignored
              }
            })
        .start();
    return queue.take();
  }

  private static int method13() {
    AtomicInteger result = new AtomicInteger();
    final CompletableFuture<Integer> completableFuture =
        CompletableFuture.supplyAsync(HomeWork::fibo);
    final CompletableFuture<Void> setResultFuture = completableFuture.thenAccept(result::set);
    setResultFuture.join();
    return result.get();
  }

  private static int method14() {
    AtomicInteger result = new AtomicInteger();
    final CompletableFuture<Void> future = CompletableFuture.runAsync(() -> result.set(fibo()));
    future.join();
    return result.get();
  }

  private static int method15() {
    AtomicInteger result = new AtomicInteger();
    final CompletableFuture<Integer> completableFuture =
        CompletableFuture.supplyAsync(HomeWork::fibo);
    final CompletableFuture<Object> future =
        completableFuture.thenApply(
            res -> {
              result.set(res);
              return null;
            });
    future.join();
    return result.get();
  }

  private static int fibo() {
    return fibo(36);
  }

  private static int fibo(int a) {
    if (a < 2) {
      return 1;
    }
    return fibo(a - 1) + fibo(a - 2);
  }
}
