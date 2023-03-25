package com.peirong.config;

import jdk.nashorn.internal.runtime.JSONFunctions;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.ParseException;
import org.springframework.expression.ParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.stereotype.Component;

import java.util.Timer;
import java.util.TimerTask;


@Component
public class Person {
    public static void main(String[] args) {

        ExpressionParser parser = new SpelExpressionParser();

        Expression exp = parser.parseExpression("99 + 99 * 3");
        System.out.println(exp.getValue());

        exp = parser.parseExpression("T(java.lang.Math).random()");



        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                System.out.println(Thread.currentThread().getName());
            }
        },1000);


        Thread thread = new Thread(()->{
            Class<?> clazz = int.class;
            System.out.println(Integer.TYPE == int.class);
        });
        thread.start();

    }



    /**
     * 我们可以通过创建一个Timer类来让它进行定时任务调度，
     * 我们可以通过此对象来创建任意类型的定时任务，包延时任务、循环定时任务等。
     * 我们发现，虽然任务执行完成了，但是我们的程序并没有停止，
     * 这是因为Timer内存维护了一个任务队列和一个工作线程：public class Timer {
     *
     *       The timer task queue.  This data structure is shared with the timer
     *       thread.  The timer produces tasks, via its various schedule calls,
     *       and the timer thread consumes, executing timer tasks as appropriate,
     *       and removing them from the queue when they're obsolete.
     *
     *
     *          public class Timer{
     *              private final TaskQueue queue = new TaskQueue();
     *
     *              //The timer thread.
     *
     *              private final TimerThread thread = new TimerThread(queue);
     *          }
     *
     *
     * */



}
