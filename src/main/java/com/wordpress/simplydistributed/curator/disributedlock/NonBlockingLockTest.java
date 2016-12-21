package com.wordpress.simplydistributed.curator.disributedlock;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.retry.ExponentialBackoffRetry;

public class NonBlockingLockTest {

    public static void main(String[] args) throws Exception {
        final String zkConnect = "192.168.99.100:2181";
        final String lockPath = "/lock";
        
        CuratorFramework curatorClient = CuratorFrameworkFactory.newClient(zkConnect, new ExponentialBackoffRetry(1000, 3));
        curatorClient.start();
        final InterProcessMutex dMutex = new InterProcessMutex(curatorClient, lockPath);

        Runnable task = new Runnable() {
            @Override
            public void run() {
                AtomicBoolean acquired = null;
                for (int i = 1; i <= 2; i++) {
                    try {
                        System.out.println("Process " + Thread.currentThread().getName() + " TRYING lock");
                        acquired = new AtomicBoolean(false);
                        if (dMutex.acquire(2, TimeUnit.SECONDS)) {
                            //dMutex.acquire();
                            acquired.set(true);
                            System.out.println("Process " + Thread.currentThread().getName() + " ACQUIRED lock. Iteration " + i);
                            System.out.println("Process " + Thread.currentThread().getName() + " WORK-IN-PROGRESS");
                            Thread.sleep(5000); //simulating some work
                        }

                    } catch (Exception ex) {
                        Logger.getLogger(NonBlockingLockTest.class.getName()).log(Level.SEVERE, null, ex);
                    } finally {

                        try {
                            if (acquired.get()) {
                                dMutex.release();
                                System.out.println("Process " + Thread.currentThread().getName() + " RELEASED lock");
                            }

                        } catch (Exception ex) {
                            Logger.getLogger(NonBlockingLockTest.class.getName()).log(Level.SEVERE, null, ex);
                        }

                    }
                }
            }
        };

        new Thread(task, "1").start();
        new Thread(task, "2").start();
        //new Thread(task, "3").start();
        //new Thread(task, "4").start();
    }

}
