package com.peirong.practice;

import sun.misc.ThreadGroupUtils;

public class CreateDemo2 {
    public static final int MAX_TURN = 5;
    static int threadNo = 1;

    public static String getCurThreadName() {
        return Thread.currentThread().getName();
    }
    static class RunTarget implements Runnable {

        @Override
        public void run(){
            for (int j = 1; j < MAX_TURN; j++) {
                System.out.println(getCurThreadName() + ", 轮次：" + j);
            }
            System.out.println(getCurThreadName() + " 运行结束.");
        }
    }

    public static void main(String[] args) {
        Thread thread = null;
        for (int i = 0; i < 2; i++) {
            Runnable target = new RunTarget();
            thread = new Thread(target,"RunnableThread" + threadNo++);
            thread.start();
        }
    }
}
