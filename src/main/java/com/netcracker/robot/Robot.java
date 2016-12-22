package com.netcracker.robot;
import javax.swing.SwingUtilities;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.util.Random;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by vymi1016 on 12/20/2016.
 */
public class Robot implements Runnable {
    static final double MAX_STEP = 0.5;
    static final double MIN_STEP = 1.5;
    volatile private double passDistance ;
    volatile private int amountLegs ;
    volatile private int numberLegs ;
    volatile private double needDistance ;
    private int stepCounter ;
    volatile private LinkedBlockingQueue<Leg> legs;
    private Random rnd;
    private ConfigFrame configFrame ;


    public Robot(int amountLegs, double needDistance) {
        this.needDistance = needDistance;
        this.amountLegs = amountLegs;
        this.rnd = new Random(System.currentTimeMillis());
    }

    public void run() {
        try {

               createConfigGUI();

                legs = new LinkedBlockingQueue<Leg>();
                for (int i = 1; i <= amountLegs; i++) {
                    Leg leg = new Leg(++numberLegs, this);
                    legs.add(leg);
                    leg.setDaemon(true);
                    leg.start();
                }

                synchronized (this) {
                    while (passDistance < needDistance) {
                        for (Leg leg : legs) {

                            synchronized (leg) {
                                leg.notify();
                            }
                            this.wait();

                            stepCounter++;
                            passDistance = randomCounter(passDistance);
                            if (passDistance < needDistance) {
                                System.out.println("Passed distance " + passDistance);
                            } else break;
                        }
                    }
                }

                System.out.println("Passed distance " + passDistance + " \nNumber of steps " + stepCounter);
                configFrame.closeFrame();

            } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    public void addLegs(int amountAddLeg){
        for (int i = 1; i <= amountAddLeg; i++) {
                Leg leg = new Leg(++numberLegs, this);
                ++amountLegs;
                legs.add(leg);
                leg.setDaemon(true);
                leg.start();
        }
    }


    public void removeLegs(int amountRemoveLeg) {
        if(amountRemoveLeg<amountLegs) {
            for (int i = 1; i <= amountRemoveLeg; i++) {
                    legs.peek().setReady(false);
                    legs.remove();
                    amountLegs--;
            }
        }
        else {
            System.out.println("Can't remove all legs");
        }
    }

    /**
     * Method for create GUI, which configuration add and remove legs
     */
    private void createConfigGUI() {
        Runnable threadGUI = new Runnable() {
            public void run() {
                configFrame = new ConfigFrame("Configure");
                configFrame.getAddButton().addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        try {
                            addLegs(Integer.parseInt(configFrame.getAddField().getText()));
                        } catch (NumberFormatException e1) {
                            System.out.println("Input valid data");
                        }
                    }
                });

                configFrame.getRemoveButton().addActionListener(new ActionListener() {

                    public void actionPerformed(ActionEvent e) {
                        try {
                            removeLegs(Integer.parseInt(configFrame.getRemoveField().getText()));
                        } catch (NumberFormatException e1) {
                            System.out.println("Input valid data");
                        }
                    }
                });

            }
        };
        SwingUtilities.invokeLater(threadGUI);
    }

    /**
     * Method count next pass distance
     * @param distance
     * @return next pass distance with add random step
     */
    private double randomCounter(double distance){
        double step = MIN_STEP + (MAX_STEP - MIN_STEP) * rnd.nextDouble();
        distance = distance +step;
        distance = BigDecimal.valueOf(distance).setScale(2, BigDecimal.ROUND_HALF_DOWN).doubleValue(); // for rounding distance
        return distance;
    }

}


