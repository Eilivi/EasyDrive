package com.peirong.practice;

public class CreateDemo {
    public static final int MAX_TURN = 5;
    public static String getCurThreadName() {
        return Thread.currentThread().getName();
    }

    static int threadNo = 1;
    static class DemoThread extends Thread {
        public DemoThread() {
            super("DemoThread-" + threadNo++);
        }

        public void run() {
            for (int i = 1; i < MAX_TURN; i++) {
                System.out.println(getName() + ", 轮次：" + i);
            }
            System.out.println(getName() + " 运行结束.");
        }

    }
    public static void main(String[] args) throws InterruptedException{
        Thread thread = null;
        for (int i = 0; i < 2; i++) {
            thread = new DemoThread();
            thread.start();
        }
        System.out.println(getCurThreadName() + "运行结束.");
    }
}
