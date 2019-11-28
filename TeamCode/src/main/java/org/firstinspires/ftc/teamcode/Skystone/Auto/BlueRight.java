package org.firstinspires.ftc.teamcode.Skystone.Auto;

import android.os.SystemClock;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.teamcode.Skystone.MathFunctions;
import org.firstinspires.ftc.teamcode.Skystone.MotionProfiler.PathPoints;
import org.firstinspires.ftc.teamcode.Skystone.MotionProfiler.Point;
import org.firstinspires.ftc.teamcode.Skystone.MotionProfiler.SplineGenerator;
import org.firstinspires.ftc.teamcode.Skystone.Robot;
import org.firstinspires.ftc.teamcode.Skystone.Vision;

import java.util.HashMap;

@Autonomous(name="BlueRight", group ="LinearOpmode")
public class BlueRight extends AutoBase{
    @Override
    public void runOpMode() {
        long startTime;
        initLogic();

        telemetry.addLine("HEREEE");
        telemetry.update();


        waitForStart();
        startTime = SystemClock.elapsedRealtime();


        // this will be the center positions
        int firstSkystoneY = 2;
        int secondSkyStoneY = 20;
        int secondSkyStoneX = 65;

        Vision.Location skystoneLocation = vision.runDetection();

        sleep(250);

        position2D.startOdometry();

        if (skystoneLocation == Vision.Location.LEFT){
            firstSkystoneY = -5;
            secondSkyStoneY = 13;
        } else if (skystoneLocation == Vision.Location.RIGHT){
            firstSkystoneY = 7;
            secondSkyStoneY = 27;
            secondSkyStoneX = 48;
        }
        double[][] toFirstStone = {
                {0,0,10,0},
                {10,firstSkystoneY,10,0},
                {50,firstSkystoneY,10,0}};
        HashMap<Point,Robot.Actions> toFirstStoneActions = new HashMap<Point,Robot.Actions>();

        double[][] toFoundation = {
                {55,firstSkystoneY,-30,0},
                {26,-10,0,-10},
                {26,-30,0,-10},
                {24,-80,0,-10},
                {35,-90,10,0}};
        HashMap<Point,Robot.Actions> toFoundationActions = new HashMap<Point,Robot.Actions>() {{
            put(new Point(24,-40), Robot.Actions.EXTEND_OUTTAKE);
            put(new Point(24,-30), Robot.Actions.STOP_INTAKE);
        }};

        double[][] toSecondStone = {
                {31,-75,-10,0},
                {10,-70,0,10},
                {24,-57,-10,0},
                {26,-30,0,-10},
                {26,secondSkyStoneY - 5,10,0},
                {secondSkyStoneX,secondSkyStoneY,30,0}};
        HashMap<Point,Robot.Actions> toSecondStoneActions = new HashMap<Point,Robot.Actions>() {{
            put(new Point(32,-80), Robot.Actions.RETRACT_OUTTAKE);
            put(new Point(20,-55), Robot.Actions.RELEASE_FOUNDATION);
            put(new Point(28,-55), Robot.Actions.START_INTAKE);
        }};

        double[][] toDepositSecondStone = {
                {55,secondSkyStoneY,-30,0},
                {30,10,0,-20},
                {30,-30,0,10},
                {25,-75,0,-10}};
        HashMap<Point,Robot.Actions> toDepositSecondStoneActions = new HashMap<Point,Robot.Actions>() {{
            put(new Point(15,-32), Robot.Actions.EXTEND_OUTTAKE);
            put(new Point(30,-30), Robot.Actions.STOP_INTAKE);
        }};

        double[][] toPark = {
                {15,-60,0,10},
                {28,-50,0,10},
                {27,-30,0,10}};
        HashMap<Point,Robot.Actions> toParkActions = new HashMap<Point,Robot.Actions>(){{
            put(new Point(16,-70), Robot.Actions.RETRACT_OUTTAKE);
        }};

        double[][] toThirdStone = {
                {16,-60,5,10},
                {28,-30, 10,0},
                {45,-6,0,10},
                {52,10, 10,0},
                {55,20, 30,0}};
        HashMap<Point,Robot.Actions> toThirdStoneActions = new HashMap<Point,Robot.Actions>() {{
            put(new Point(16,-67), Robot.Actions.RETRACT_OUTTAKE);
            put(new Point(28,-30), Robot.Actions.START_INTAKE);
        }};

        double[][] toParkAfterThirdStone = {
                {30,10,0,10},
                {27,-30,0,10}};
        HashMap<Point,Robot.Actions> toParkAfterThirdStoneActions = new HashMap<Point,Robot.Actions>();

        double[][] toDepositThirdStone = {
                {55,20,-30,0},
                {24,10,0,-20},
                {15,-30,0,10},
                {15,-60,0,-10}};
        HashMap<Point,Robot.Actions> toDepositThirdStoneActions = new HashMap<Point,Robot.Actions>() {{
            put(new Point(15,-30), Robot.Actions.EXTEND_OUTTAKE);
            put(new Point(15,-20), Robot.Actions.STOP_INTAKE);
        }};

        intake(true);
        robot.splineMove(toFirstStone,0.5,1, 0.5,3,0,0,30,
                toFirstStoneActions);

        robot.splineMove(toFoundation,1,1, 0.5, 10, Math.toRadians(180),Math.toRadians(180),30,
                toFoundationActions);

        // get ready to pull foundation
        robot.foundationMover(true);
        sleep(250);

        robot.splineMove(toSecondStone,1,1, 0.5, 20,0,Math.toRadians(15),30,
                toSecondStoneActions);

        robot.splineMove(toDepositSecondStone,0.9,1, 0.5, 10, Math.toRadians(180),Math.toRadians(90),10,
                toDepositSecondStoneActions);
        robot.getClamp().setPosition(robot.CLAW_SERVO_RELEASED);

        if (SystemClock.elapsedRealtime() - startTime < 25000){
            robot.splineMove(toThirdStone, 1,1, 0.5, 20,0,Math.toRadians(90),20,
                    toThirdStoneActions);

//            robot.splineMove(toDepositThirdStone, 1, 1, 0.5, 20, Math.toRadians(180), Math.toRadians(270), 10,
//                    toDepositThirdStoneActions);
//
//            retractOuttakeWait();
            robot.splineMove(toParkAfterThirdStone, 1, 1, 0.3, 10, Math.toRadians(180), Math.toRadians(90), 5, toParkAfterThirdStoneActions);


        }else {
            robot.splineMove(toPark, 1, 1, 0.3, 10, 0, Math.toRadians(90), 5, toParkActions);
        }
    }
}