package com.netcracker.robot;

/**
 * Created by vymi1016 on 12/20/2016.
 *
 */
public class RobotCall {

    public static void main(String[] args) {
        try {
            int numbLegs = Integer.parseInt(args[0]);
            double distance = Double.parseDouble(args[1]);

            Thread robotRun = new Thread(new Robot(numbLegs, distance));
            robotRun.start();

        }catch ( NumberFormatException e){
            System.out.println("Input valid data");
        }
}

}
