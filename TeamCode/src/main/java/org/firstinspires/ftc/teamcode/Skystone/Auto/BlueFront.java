package org.firstinspires.ftc.teamcode.Skystone.Auto;

import android.os.SystemClock;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.teamcode.Skystone.MotionProfiler.Point;
import org.firstinspires.ftc.teamcode.Skystone.Robot;
import org.firstinspires.ftc.teamcode.Skystone.Vision;

import java.util.HashMap;

@Autonomous(name = "BlueFront", group = "LinearOpmode")
public class BlueFront extends AutoBase {
    @Override
    public void runOpMode() {
        long startTime;
        initLogic();

        waitForStart();
        startTime = SystemClock.elapsedRealtime();

        // Positions assuming center Skystone
        double firstSkystoneY = 2;
        double secondSkyStoneY = 11.25;
        double secondSkyStoneX = 45;
        double thirdStoneY = 25;
        double thirdStoneX = 52;
        double anglelock = 30;
        double angleLockAngle = Math.toRadians(55);

        Vision.Location skystoneLocation = Vision.Location.UNKNOWN;
        try {
            skystoneLocation = vision.runDetection(true, false);
        } catch (Exception e) {

        }

        telemetry.addLine("Detection Result: " + skystoneLocation.toString());
        telemetry.update();

        sleep(250);

        position2D.startOdometry();

        // Change Skystone positions if detected left or right
        if (skystoneLocation == Vision.Location.LEFT) {
            firstSkystoneY = -4.5;
            secondSkyStoneY = 5;
            secondSkyStoneX = 45;
            anglelock = 30;
            thirdStoneX = 52;
            thirdStoneY = 20;
        } else if (skystoneLocation == Vision.Location.RIGHT) {
            firstSkystoneY = 4;
            secondSkyStoneY = 21;
            secondSkyStoneX = 45;
            anglelock = 29;
            thirdStoneX = 65;
            thirdStoneY = 31.5;
            angleLockAngle = Math.toRadians(50);
        }

        double[][] toFirstStone = {
                {0, 0, 10, 0},
                {10, firstSkystoneY, 10, 0},
                {48, firstSkystoneY, 10, 0}};
        HashMap<Point, Robot.Actions> toFirstStoneActions = new HashMap<Point, Robot.Actions>() {{
            put(new Point(0, 0), Robot.Actions.START_INTAKE);
        }};

        double[][] toFoundation = {
                toFirstStone[toFirstStone.length - 1],
                {33, firstSkystoneY-5, 0, -10},
                {31, -17, -10, -20},
                {27, -20, -10, -20},
                {27, -30, -10, -20},
                {27, -43, -10, -20},
                {26, -55, 0, -20},
                {26, -67, 0, -20},
                {27, -68, 0, -20},
                {29, -74, 0, -20},
                {35, -83, 0, -10}};
        HashMap<Point, Robot.Actions> toFoundationActions = new HashMap<Point, Robot.Actions>() {{
            put(new Point(24, -26), Robot.Actions.EXTEND_OUTTAKE);
            put(new Point(24, -45), Robot.Actions.STOP_INTAKE);
        }};

        double[][] toSecondStone = {
                {toFoundation[toFoundation.length - 1][0], toFoundation[toFoundation.length - 1][1], -10, 0},
                {12, -63, 10, 0},
                {10, -60, 10, 0},
                {26, -61, -10, 0},
                {26, -29, 0, 10},
                {24, secondSkyStoneY - 10, 0, -10},
                {secondSkyStoneX, secondSkyStoneY, 30, 0},
                {secondSkyStoneX-5, secondSkyStoneY+6.5, 30, 0}};
        HashMap<Point, Robot.Actions> toSecondStoneActions = new HashMap<Point, Robot.Actions>() {{
            put(new Point(22, -73), Robot.Actions.RETRACT_OUTTAKE);
            put(new Point(10, -70), Robot.Actions.RELEASE_FOUNDATION);
            put(new Point(28, 10), Robot.Actions.START_INTAKE);
        }};

        double[][] toDepositSecondStone = {
                {toSecondStone[toSecondStone.length - 1][0], toSecondStone[toSecondStone.length - 1][1], -10, 0},
                {secondSkyStoneX - 5, secondSkyStoneY - 10, -10, 0},
                {secondSkyStoneX - 9, secondSkyStoneY - 8, -10, 0},
                {36, 0, 0, -20},
                {35, -29, 0, -20},
                {30, -63, 0, -10},
                {30, -64, 0, -10},
                {30, -65, 0, -10}};
        HashMap<Point, Robot.Actions> toDepositSecondStoneActions = new HashMap<Point, Robot.Actions>() {{
            put(new Point(28, -10), Robot.Actions.EXTEND_OUTTAKE);
            put(new Point(35, -15), Robot.Actions.STOP_INTAKE);
            put(new Point(35, -25), Robot.Actions.START_INTAKE);
        }};

        final double[][] toThirdStone = {
                toDepositSecondStone[toDepositSecondStone.length - 1],
                {30, -58, 0, 10},
                {35, -52, 5, -10},
                {33, -49, 0, 10},
                {33, -30, 0, 10},
                {34, -10, 0, -10},
                {43, -6, 0, 10},
                {thirdStoneX, thirdStoneY, 10, 0}};
        HashMap<Point, Robot.Actions> toThirdStoneActions = new HashMap<Point, Robot.Actions>() {{
            put(new Point(20, -68), Robot.Actions.RETRACT_OUTTAKE);
            put(new Point(28, -30), Robot.Actions.START_INTAKE);
        }};

        double[][] toDepositThirdStone = {
                toThirdStone[toThirdStone.length - 1],
                {41, 15, 0, 10},
                {40, -29, 0, -20},
                {39, -61, 0, -10},
                {39, -65, 0, -10}};
        HashMap<Point, Robot.Actions> toParkAfterThirdStoneActions = new HashMap<Point, Robot.Actions>() {{
            put(new Point(23, 0), Robot.Actions.EXTEND_OUTTAKE);
            put(new Point(toThirdStone[toThirdStone.length - 1][0] - 15, toThirdStone[toThirdStone.length - 1][1] - 30), Robot.Actions.STOP_INTAKE);
            put(new Point(21, -20), Robot.Actions.START_INTAKE);
        }};

        double[][] toDepositThirdStoneOtherwise = {
                toThirdStone[toThirdStone.length - 1],
                {41, 15, 0, 10},
                {40, -29, 0, -20},
                {39, -61, 0, -10},
                {39, -65, 0, -10}};
        HashMap<Point, Robot.Actions> toParkAfterThirdStoneActionsOtherwise = new HashMap<Point, Robot.Actions>() {{
            put(new Point(23, 0), Robot.Actions.EXTEND_OUTTAKE);
            put(new Point(toThirdStone[toThirdStone.length - 1][0] - 15, toThirdStone[toThirdStone.length - 1][1] - 25), Robot.Actions.STOP_INTAKE);
            put(new Point(21, -20), Robot.Actions.START_INTAKE);
        }};

        double[][] toPark = {
                {toDepositThirdStone[toDepositThirdStone.length - 1][0], toDepositThirdStone[toDepositThirdStone.length - 1][1], 0, 10},
                {37, -55, 0, 10},
                {38.5, -36, 0, 10}};
        HashMap<Point, Robot.Actions> toParkActions = new HashMap<Point, Robot.Actions>() {{
            put(new Point(25, -65), Robot.Actions.RETRACT_OUTTAKE);
        }};

        double[][] toParkDitch = {
                {toThirdStone[toThirdStone.length - 1][0], toThirdStone[toThirdStone.length - 1][1], -10, -10},
                {37, toThirdStone[toThirdStone.length - 1][1] - 20, -10, -10},
                {37, -30, -10, -10}};
        HashMap<Point, Robot.Actions> toParkDitchActions = new HashMap<Point, Robot.Actions>() {{
            put(new Point(25, -65), Robot.Actions.RETRACT_OUTTAKE);
        }};

        intake(true);
        robot.splineMove(toFirstStone, 0.6, 1, 0.55, 35, 0, 0, 20,
                toFirstStoneActions, true, 3000);
        //to first stone is 1
        robot.dumpPoints("" + startTime, "1");

        robot.splineMove(toFoundation, 1, 1, 0.4, 25, Math.toRadians(180), Math.toRadians(180), 25,
                toFoundationActions, true, 4750);
        // to foundation is 2
        robot.dumpPoints("" + startTime, "2");

        // get ready to pull foundation
        robot.foundationMovers(true);
        sleep(350);

        robot.splineMove(toSecondStone, 1, 1, 0.6, 25, 0, angleLockAngle, anglelock,
                toSecondStoneActions, true, 7000);
        //to second stone is 3
        robot.dumpPoints("" + startTime, "3");

        robot.splineMove(toDepositSecondStone, 1, 1, 0.5, 35, Math.toRadians(180), Math.toRadians(90), 18,
                toDepositSecondStoneActions, true, 4500);
        robot.getClamp().setPosition(robot.CLAMP_SERVO_RELEASED);
        //to deposit second stone is 4
        robot.dumpPoints("" + startTime, "4");

        robot.foundationMovers(false);
        robot.getClamp().setPosition(robot.CLAMP_SERVO_RELEASED);
        robot.brakeRobot();

        robot.splineMove(toThirdStone, 0.5, 1, 0.8, 70, 0, Math.toRadians(90), 20,
                toThirdStoneActions, true, 4500);
        //to thrid stone is 5
        robot.dumpPoints("" + startTime, "5");
        if (SystemClock.elapsedRealtime() - startTime < 26000) {
            if(skystoneLocation == Vision.Location.RIGHT) {
                robot.splineMove(toDepositThirdStone, 1, 1, 0.3, 30, Math.toRadians(180), Math.toRadians(90), 20, toParkAfterThirdStoneActions, true, 4000);
            }else{
                robot.splineMove(toDepositThirdStoneOtherwise, 1, 1, 0.3, 30, Math.toRadians(180), Math.toRadians(90), 20, toParkAfterThirdStoneActionsOtherwise, true, 4000);
            }
            //to deposit third stone is 6
            robot.dumpPoints("" + startTime, "6");

            robot.foundationMovers(false);
            robot.splineMove(toPark, 0.5, 1, 0.3, 10, 0, Math.toRadians(90), 5, toParkActions);

            //to park is 7
            robot.dumpPoints("" + startTime, "7");
        } else {
            robot.splineMove(toParkDitch, 0.6, 1, 0.55, 17, Math.toRadians(180), Math.toRadians(90), 5, toParkDitchActions);

            //to park is 7
            robot.dumpPoints("" + startTime, "7");
        }
    }
}