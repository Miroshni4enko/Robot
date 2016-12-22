package com.netcracker.robot;

/**
 * Created by vymi1016 on 12/20/2016.
 */
public class Leg extends Thread{
    private final int leg;
    private Robot robot;
    volatile private boolean ready = true;

    Leg(int leg, Robot robot) {
        this.leg = leg;
        this.robot = robot;
    }

    public void setReady(boolean ready) {
        this.ready = ready;
    }

    public void run() {
        while (ready) {
            synchronized (this) {
                try {
                    this.wait();
                    System.out.println("Robot moved with leg " + leg);
                    Thread.sleep(1000);
                    synchronized (robot) {
                        robot.notify();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

