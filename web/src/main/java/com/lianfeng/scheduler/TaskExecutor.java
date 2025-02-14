package com.lianfeng.scheduler;

import org.springframework.stereotype.Component;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class TaskExecutor {
    /**
     * 用于存储需要执行的任务
     */
    private final BlockingQueue<Runnable> taskQueue = new LinkedBlockingQueue<>();
    /**
     * 任务的数量
     */
    private final AtomicInteger taskCounter = new AtomicInteger(0);

    /**
     * 从任务队列中取出任务并执行
     */
    private final Thread workerThread;

    /**
     * 项目启动时就启动任务队列
     */
    public TaskExecutor() {
        System.out.println("队列任务启动");
        workerThread = new Thread(() -> {
            while (true) {
                try {
                    Runnable task = taskQueue.take(); // 从队列中取出任务
                    task.run(); // 执行任务
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        });
        workerThread.start();
    }

    /**
     * 提交普通任务
     * @param task
     */
    public void submitTask(Runnable task) {
        int taskId = taskCounter.incrementAndGet(); // 在任务添加时分配编号
        System.out.println("添加普通任务队列成功，任务编号: " + taskId);
        taskQueue.add(() -> {
            System.out.println("任务队列 " + taskId + " 启动");
            task.run(); // 执行原始任务
            System.out.println("任务队列 " + taskId + " 完成");
        });
    }

    /**
     * 立即执行任务
     * @param task
     */
    public void executeImmediately(Runnable task) {
        int taskId = taskCounter.incrementAndGet(); // 在任务执行时分配编号
        System.out.println("立即执行任务，任务编号: " + taskId);
        System.out.println("任务队列 " + taskId + " 启动");
        task.run(); // 直接执行任务
        System.out.println("任务队列 " + taskId + " 完成");
    }

    /**
     * 关闭工作线程
     */
    public void shutdown() {
        workerThread.interrupt();
    }


}