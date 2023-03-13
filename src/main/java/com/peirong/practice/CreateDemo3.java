package com.peirong.practice;

public class CreateDemo3 {

    public static final int MAX_TURN = 5;
    static int threadNo = 1;
    

    public static String getCurThreadName() {
        return Thread.currentThread().getName();
    }

    public static void main(String[] args) throws InterruptedException{
        Thread thread = null;
        for (int i = 0; i < 2; i++) {
            thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    for (int j = 1; j < MAX_TURN; j++) {
                        System.out.println(getCurThreadName() + "，轮次：" + j);
                    }
                    System.out.println(getCurThreadName() + " 运行结束.");
                }
            },"RunnableThread" + threadNo++);
            thread.start();
        }

        System.out.println(getCurThreadName() + " 运行结束.");

    }
}
