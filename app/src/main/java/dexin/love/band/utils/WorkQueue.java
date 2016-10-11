package dexin.love.band.utils;

import java.util.LinkedList;

/**
 * Created by Carl on 2016-10-08 008.
 */

public class WorkQueue {
    private LinkedList queue = new LinkedList();// 任务队列
    private boolean state = false;//true 可运行任务,false 不可运行任务
    private static WorkQueue workQueue;

    public static synchronized WorkQueue getInstance() {
        if (workQueue == null)
            workQueue = new WorkQueue();
        return workQueue;
    }


    private WorkQueue() {
        queue = new LinkedList();
        new Thread() {
            @Override
            public void run() {
                Runnable r;
                while (true) {
                    while (queue.isEmpty() || !state) {// 如果任务队列中没有任务，等待
                    }

                    try {
                        state = false;
                        r = (Runnable) queue.removeFirst();// 有任务时，取出任务
                        System.out.println(Thread.currentThread().getName());
                        r.run();// 执行任务
                    } catch (RuntimeException e) {
                    }
                }
            }
        }.start();
    }

    /**
     * 执行任务
     * @param r
     */
    public void execute(Runnable r) {// 执行任务
        synchronized (queue) {
            queue.addLast(r);
            queue.notify();
        }
    }

    /**
     * 通知队列执行与否
     * @param state true:允许执行  false:不允许执行
     */
    public void notifyState(boolean state) {
        System.out.println("notifyState");
        this.state = state;
    }
}
