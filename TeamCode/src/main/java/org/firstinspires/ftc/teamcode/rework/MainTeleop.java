package org.firstinspires.ftc.teamcode.rework;

import android.os.SystemClock;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.rework.ActionTools.Action;
import org.firstinspires.ftc.teamcode.rework.ModuleTools.TelemetryProvider;
import org.firstinspires.ftc.teamcode.rework.RobotTools.ToggleButton;

import java.util.ArrayList;

@TeleOp
public class MainTeleop extends LinearOpMode implements TelemetryProvider {
    Robot robot;
    long lastUpdateTime;

    private double SLOW_MODE_SCALE_FACTOR = 0.3;

    private boolean lastArrowMoveState = false;
    private double arrowMoveAngle = 0;

    ToggleButton rb = new ToggleButton();
    ToggleButton lb = new ToggleButton();

    public void runOpMode() {
        initRobot();
        robot.telemetryDump.registerProvider(this);
        waitForStart();
        robot.startModules();

        while (opModeIsActive()) {
            updateDrivetrainStates();

            if(rb.isPressed(gamepad2.right_bumper)){
                robot.actionQueueModule.registerAction(new Action(0,0));
            } else if (lb.isPressed(gamepad2.left_bumper)){
                robot.actionQueueModule.registerAction(new Action(0,1));
            }

            lastUpdateTime = SystemClock.elapsedRealtime();
        }
    }

    private void initRobot() {
        robot = new Robot(hardwareMap, telemetry,this);
        robot.initModules();
    }

    private void updateDrivetrainStates() {

        double yMovement = 0;
        double xMovement = 0;
        double turnMovement = 0;

        if (usingJoysticks()){
            yMovement = -gamepad1.left_stick_y;
            xMovement = gamepad1.left_stick_x;
            turnMovement = gamepad1.right_stick_x;
        } else if(usingDPad()){
            if (gamepad1.dpad_up){
                yMovement = 1;
            } else if (gamepad1.dpad_down){
                yMovement = -1;
            } else if (gamepad1.dpad_left){
                turnMovement = -1;
            } else if (gamepad1.dpad_right){
                turnMovement = 1;
            }
        }

        if (usingArrowMove()){
            if (!lastArrowMoveState){
                arrowMoveAngle = robot.odometryModule.worldAngleRad;
                lastArrowMoveState = true;
            }

            double r = Math.hypot(yMovement, xMovement);
            double aT = Math.atan2(yMovement, xMovement);
            double t = aT + robot.odometryModule.worldAngleRad - arrowMoveAngle;

            double nXMovement = r * Math.cos(t);
            double nYMovement = r * Math.sin(t);

            xMovement = nXMovement;
            yMovement = nYMovement;
        } else {
            lastArrowMoveState = false;
        }

        if(usingSlowMode()){
            xMovement *= SLOW_MODE_SCALE_FACTOR;
            yMovement *= SLOW_MODE_SCALE_FACTOR;
            turnMovement *= SLOW_MODE_SCALE_FACTOR;
        }

        robot.drivetrainModule.yMovement = yMovement;
        robot.drivetrainModule.xMovement = xMovement;
        robot.drivetrainModule.turnMovement = turnMovement;

    }

    private boolean usingJoysticks(){
        return gamepad1.left_stick_y != 0 || gamepad1.left_stick_x != 0 || gamepad1.right_stick_x != 0;
    }

    private boolean usingDPad(){
        return gamepad1.dpad_up || gamepad1.dpad_down || gamepad1.dpad_left || gamepad1.dpad_right;
    }

    private boolean usingArrowMove(){
        return gamepad1.left_bumper;
    }

    private boolean usingSlowMode(){
        return gamepad1.right_bumper;
    }

    @Override
    public ArrayList<String> getTelemetryData() {
        long currentTime = SystemClock.elapsedRealtime();

        ArrayList<String> data = new ArrayList<>();
        data.add("TeleOp while loop update time: " + String.valueOf(currentTime - lastUpdateTime));
        lastUpdateTime = currentTime;

        return data;
    }
}