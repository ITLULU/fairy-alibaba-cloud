package com.fairy.cloud.product.test;

import java.util.concurrent.*;

public class T {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        ExecutorService service = Executors.newSingleThreadExecutor();
        Future<Integer> future = service.submit(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return 1;
            }
        });
        System.out.println(future.get());

        service.execute(new Runnable() {
            @Override
            public void run() {
                System.out.println("1212");
            }
        });
        System.out.println("finish! ! !");
    }
}
