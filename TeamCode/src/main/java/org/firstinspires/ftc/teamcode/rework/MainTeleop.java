package org.firstinspires.ftc.teamcode.rework;

import android.os.SystemClock;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

@TeleOp
public class MainTeleop extends LinearOpMode {
    Robot robot;

    public void runOpMode() {
        initRobot();
        waitForStart();
        robot.startModules();

        while (opModeIsActive()) {
            updateDrivetrainStates();

            telemetryUpdateTime();
            robot.telemetryDump.addData("x: ", robot.odometryModule.worldX);
            robot.telemetryDump.addData("y: ", robot.odometryModule.worldY);
            robot.telemetryDump.addData("heading: ", Math.toDegrees(robot.odometryModule.worldAngleRad));
            robot.telemetryDump.addData("yLeft encoder: ", robot.odometryModule.leftPodNewPosition);
            robot.telemetryDump.addData("yRight encoder: ", robot.odometryModule.rightPodNewPosition);
            robot.telemetryDump.addData("mecanum encoder: ", robot.odometryModule.mecanumPodNewPosition);
            robot.telemetryDump.addData("xVel: ", robot.velocityModule.xVel);
            robot.telemetryDump.addData("yVel: ", robot.velocityModule.yVel);
            robot.telemetryDump.addData("angleVel: ", robot.velocityModule.angleVel);
        }
    }

    private void initRobot() {
        robot = new Robot(hardwareMap, telemetry,this);
        robot.initModules();
    }

    private void updateDrivetrainStates() {
        robot.drivetrainModule.yMovement = -gamepad1.left_stick_y;
        robot.drivetrainModule.xMovement = gamepad1.left_stick_x;
        robot.drivetrainModule.turnMovement = gamepad1.right_stick_x;
    }

    long lastUpdateTime = SystemClock.elapsedRealtime();
    long currentTime;

    /**
     * Calculates and displays (in robot.telemetryDump) the loop time of the while(isOpModeActive) loop.
     */
    private void telemetryUpdateTime() {
        currentTime = SystemClock.elapsedRealtime();

        robot.telemetryDump.addData("TeleOp while loop update time: ", (currentTime - lastUpdateTime));

        lastUpdateTime = currentTime;
    }
}
