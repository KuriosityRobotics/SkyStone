package org.firstinspires.ftc.teamcode.Skystone;

import android.os.SystemClock;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.Skystone.Auto.Actions.MotionAction;
import org.firstinspires.ftc.teamcode.Skystone.Odometry.Position2D;

import java.util.LinkedList;
import java.util.Queue;

@TeleOp(name = "MainTeleOpSky", group = "Linear Opmode")
public class MainTeleop extends LinearOpMode {
    Robot robot;

    double fLPower;
    double fRPower;
    double bLPower;
    double bRPower;

    double spoolPower;

    long currentTime;

    boolean onSlowDrive, changedSlowDrive = false;

    boolean isRetract = false;
    boolean isExtend = false;
    boolean isClamp = false;
    boolean is90 = false;
    boolean foundationToggle = false;
    boolean resetfoundation = false;

    public static double powerScaleFactor = 0.9;

    boolean isIntakeMode = false;


    @Override
    public void runOpMode() {
        resetRobot();
        robot.initServos();
        waitForStart();

        Position2D position2D = new Position2D(robot);
        position2D.startOdometry();

        while (opModeIsActive()) {
            telemetry.update();

            robotModeLogic();

            slowDriveLogic();
            driveLogic();

            foundationLogic();

            outtakeLogic();
            capStoneLogic();
            teamMarkerLogic();
            spoolLogic();
            intakeLogic();


            if (robot.isDebug()) {
                telemetry.addLine("xPos: " + robot.getRobotPos().x);
                telemetry.addLine("yPos: " + robot.getRobotPos().y);
                telemetry.addLine("angle: " + Math.toDegrees(MathFunctions.angleWrap(robot.getAnglePos())));
                telemetry.addLine("XPODLeft " + robot.getfLeft().getCurrentPosition());
                telemetry.addLine("XPODRight " + robot.getfRight().getCurrentPosition());
                telemetry.addLine("YPOD " + robot.getbLeft().getCurrentPosition());
            }

            if (isIntakeMode) {
                telemetry.addLine("CURRENT ROBOT MODE: INTAKE BOT");
            } else {
                telemetry.addLine("CURRENT ROBOT MODE: NORMAL");
            }
        }
    }

    private void spoolLogic() {
        spoolPower = -gamepad2.left_stick_y;

        if (gamepad2.left_trigger != 0) {
            spoolPower = .15;
        }

        if (!robot.isMovingLift) {
            robot.getOuttakeSpool().setPower(spoolPower);
            robot.getOuttakeSpool2().setPower(spoolPower);
        }
//        telemetry.addLine("Spool Position " + robot.getOuttakeSpool().getCurrentPosition());
    }

    private void resetRobot() {
        robot = new Robot(hardwareMap, telemetry, this);

        robot.setDrivetrainMotorModes(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        robot.setDrivetrainMotorModes(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        robot.getClampPivot().setDirection(Servo.Direction.FORWARD);

        robot.getOuttakeSpool().setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        robot.getOuttakeSpool2().setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        robot.getOuttakeSpool().setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        robot.getOuttakeSpool2().setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        robot.getIntakeLeft().setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        robot.getIntakeRight().setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
    }

    //teleop methods
    private void driveLogic() {
        // TODO: change all of this stuff to x, y, and turn movements
        //tank drive
        fLPower = (-gamepad1.left_stick_y + gamepad1.right_stick_x + gamepad1.left_stick_x) * powerScaleFactor;
        fRPower = (-gamepad1.left_stick_y - gamepad1.right_stick_x - gamepad1.left_stick_x) * powerScaleFactor;
        bLPower = (-gamepad1.left_stick_y + gamepad1.right_stick_x - gamepad1.left_stick_x) * powerScaleFactor;
        bRPower = (-gamepad1.left_stick_y - gamepad1.right_stick_x + gamepad1.left_stick_x) * powerScaleFactor;
        if (gamepad1.right_trigger != 0) {
            fLPower = (gamepad1.right_trigger) * powerScaleFactor;
            fRPower = (-gamepad1.right_trigger) * powerScaleFactor;
            bLPower = (-gamepad1.right_trigger) * powerScaleFactor;
            bRPower = (gamepad1.right_trigger) * powerScaleFactor;
        } else if (gamepad1.left_trigger != 0) {
            fLPower = (-gamepad1.left_trigger) * powerScaleFactor;
            fRPower = (gamepad1.left_trigger) * powerScaleFactor;
            bLPower = (gamepad1.left_trigger) * powerScaleFactor;
            bRPower = (-gamepad1.left_trigger) * powerScaleFactor;
        }
        //Straight D-Pad move
        if (gamepad1.dpad_up) {
            fLPower = (gamepad1.left_stick_y) + powerScaleFactor;
            bLPower = (gamepad1.left_stick_y) + powerScaleFactor;
            fRPower = (gamepad1.right_stick_y + powerScaleFactor);
            bRPower = (gamepad1.right_stick_y + powerScaleFactor);
        } else if (gamepad1.dpad_down) {
            fLPower = (gamepad1.left_stick_y) - powerScaleFactor;
            bLPower = (gamepad1.left_stick_y) - powerScaleFactor;
            fRPower = (gamepad1.right_stick_y - powerScaleFactor);
            bRPower = (gamepad1.right_stick_y) - powerScaleFactor;
        } else if (gamepad1.dpad_right) {
            fLPower = (gamepad1.right_stick_y) + powerScaleFactor;
            bLPower = (gamepad1.right_stick_y) + powerScaleFactor;
            fRPower = (gamepad1.left_stick_y) - powerScaleFactor;
            bRPower = (gamepad1.left_stick_y) - powerScaleFactor;
        } else if (gamepad1.dpad_left) {
            fRPower = (gamepad1.right_stick_y) + powerScaleFactor;
            bRPower = (gamepad1.right_stick_y) + powerScaleFactor;
            fLPower = (gamepad1.left_stick_y) - powerScaleFactor;
            bLPower = (gamepad1.left_stick_y) - powerScaleFactor;
        }

        robot.allWheelDrive(fLPower, fRPower, bLPower, bRPower);
    }

    boolean isDumpingCapstone = false;
    private void teamMarkerLogic() {
        if (gamepad2.left_bumper) {
            robot.getCapstoneServo().getController().pwmEnable();

            isDumpingCapstone = true;
            long startTime = SystemClock.elapsedRealtime();
            robot.getClamp().setPosition(robot.CLAMP_SERVO_INTAKEPOSITION);
            robot.getBackStopper().setPosition(robot.BACK_STOPPER_DOWN);

            while (opModeIsActive() && SystemClock.elapsedRealtime() - startTime < 250) {
                driveLogic();
                slowDriveLogic();
                intakeLogic();
                foundationLogic();
            }

            robot.getIntakePusher().setPosition(robot.PUSHER_PUSHED - 0.12);

            robot.getOuttakeSpool().setPower(1);
            robot.getOuttakeSpool2().setPower(1);

            while (SystemClock.elapsedRealtime() - startTime < 1000 && opModeIsActive() && !gamepad1.x) {
                driveLogic();
                slowDriveLogic();
                intakeLogic();
                foundationLogic();
            }

            robot.getOuttakeSpool().setPower(0);
            robot.getOuttakeSpool2().setPower(0);

            startTime = SystemClock.elapsedRealtime();
            robot.getCapstoneServo().setPosition(robot.CAPSTONE_DUMP);

            while (SystemClock.elapsedRealtime() - startTime < 750) {
                driveLogic();
                slowDriveLogic();
                intakeLogic();
                foundationLogic();
            }

            robot.getCapstoneServo().setPosition(robot.CAPSTONE_RETRACT);

            while (SystemClock.elapsedRealtime() - startTime < 1000) {
                driveLogic();
                slowDriveLogic();
                intakeLogic();
                foundationLogic();
            }

            while (SystemClock.elapsedRealtime() - startTime < 1800) {
                driveLogic();
                slowDriveLogic();
                intakeLogic();
                foundationLogic();
                robot.getOuttakeSpool().setPower(-1);
                robot.getOuttakeSpool2().setPower(-1);
            }

            startTime = SystemClock.elapsedRealtime();

            robot.getOuttakeSpool().setPower(0);
            robot.getOuttakeSpool2().setPower(0);

            robot.getBackStopper().setPosition(robot.BACK_STOPPER_UP);

            while (SystemClock.elapsedRealtime() - startTime < 250) {
                driveLogic();
                slowDriveLogic();
                intakeLogic();
                foundationLogic();
            }

            robot.getIntakePusher().setPosition(robot.PUSHER_PUSHED);

            while (SystemClock.elapsedRealtime() - startTime < 500) {
                driveLogic();
                slowDriveLogic();
                intakeLogic();
                foundationLogic();
            }

            robot.getIntakePusher().setPosition(robot.PUSHER_RETRACTED);
            robot.getClamp().setPosition(robot.CLAMP_SERVO_CLAMPED);

            isDumpingCapstone = false;
        }
    }

    private void slowDriveLogic() {
        //toggle driving speed
        if (gamepad1.left_bumper && !changedSlowDrive) {
            powerScaleFactor = (onSlowDrive) ? 0.9 : 0.4;
            onSlowDrive = !onSlowDrive;
            changedSlowDrive = true;
        } else if (!gamepad1.left_bumper) {
            changedSlowDrive = false;
        }

//        if (powerScaleFactor == 0.4) {
////            telemetry.addData("Driving Mode","Slow");
//        } else {
////            telemetry.addData("Driving Mode","Normal");
//        }
    }

    private boolean isTogglingBackStopper = false;
    private boolean isBackStopperDown = false;
    private boolean isBackStopperReset = true;
    private long backStopperRisingTime;

    private void intakeLogic() {
        if (Math.abs(gamepad2.left_stick_y) <= 0.25) {
            double intakeLeftPower = 0;
            double intakeRightPower = 0;

            intakeLeftPower = gamepad2.right_stick_y;
            intakeRightPower = gamepad2.right_stick_y;

            robot.getIntakeLeft().setPower(intakeLeftPower);
            robot.getIntakeRight().setPower(intakeRightPower);
            if(Math.abs(gamepad2.right_stick_y) >=0.25 && !isIntakeMode && !isClamp && !isRetract && !isExtend && !is90 && robot.getOuttakeExtender().getPosition() != robot.OUTTAKE_SLIDE_EXTENDED && robot.getOuttakeExtender().getPosition() != robot.OUTTAKE_SLIDE_PARTIAL_EXTEND){
                robot.getClamp().setPosition(robot.CLAMP_SERVO_INTAKEPOSITION);
                robot.getIntakePusher().setPosition(robot.PUSHER_RETRACTED);
            }
        }

        if (isIntakeMode) {
            robot.getIntakePusher().setPosition(robot.PUSHER_PUSHED-0.15);
            robot.getClamp().setPosition(robot.CLAMP_SERVO_CLAMPED);
        }

        if (gamepad2.right_trigger != 0 && !isTogglingBackStopper) {
            if (isBackStopperDown) {
                robot.getBackStopper().setPosition(robot.BACK_STOPPER_UP);
                isBackStopperDown = false;

                backStopperRisingTime = SystemClock.elapsedRealtime();
            } else {
                robot.getBackStopper().setPosition(robot.BACK_STOPPER_DOWN);
                isBackStopperDown = true;

                isBackStopperReset = false;
            }
            isTogglingBackStopper = true;

            robot.getBackStopper().getController().pwmEnable();
        } else if (gamepad2.right_trigger == 0) {
            isTogglingBackStopper = false;
        }

        if (currentTime >= backStopperRisingTime + 750 && !isBackStopperDown) {
            isBackStopperReset = true;
        }
    }

    Queue<MotionAction> outtakeActions = new LinkedList<>();

    double xDump;
    double yDump;

    private boolean isTogglingG2A = false;
    private boolean isTogglingG2B = false;
    private boolean isTogglingG2X = false;
    private boolean isTogglingG2Y = false;

    private void outtakeLogic() {
        currentTime = SystemClock.elapsedRealtime();
        // Logic to control outtake; with a delay on the pivot so that the slides can extend before pivot rotation
        if (gamepad2.a && !isTogglingG2A) { // Clamp and Extend
            isTogglingG2A = true;

            outtakeActions.clear();

            outtakeActions.add(new MotionAction(robot.getCapstoneServo(), robot.CAPSTONE_RETRACT, currentTime + robot.DELAY_CAPSTONE));
            outtakeActions.add(new MotionAction(robot.getBackStopper(), robot.BACK_STOPPER_UP, currentTime + robot.DELAY_BACKSTOPPER));
            outtakeActions.add(new MotionAction(robot.getClamp(), robot.CLAMP_SERVO_CLAMPED, currentTime + robot.DELAY_CLAMP_ON_EXTEND));
            outtakeActions.add(new MotionAction(robot.getOuttakeExtender(), robot.OUTTAKE_SLIDE_EXTENDED, currentTime + robot.DELAY_SLIDE_ON_EXTEND));
            outtakeActions.add(new MotionAction(robot.getClampPivot(), robot.OUTTAKE_PIVOT_EXTENDED, currentTime + robot.DELAY_PIVOT_ON_EXTEND));
            outtakeActions.add(new MotionAction(robot.getOuttakeExtender(), robot.OUTTAKE_SLIDE_PARTIAL_EXTEND, currentTime + robot.DELAY_PARTIAL_SLIDE_ON_EXTEND));
        } else if (!gamepad2.a) {
            isTogglingG2A = false;
        }
        if (gamepad2.b && !isTogglingG2B) { // Deposit and Reset
            isTogglingG2B = true;

            xDump = robot.getRobotPos().x;
            yDump = robot.getRobotPos().y;

            outtakeActions.clear();

            outtakeActions.add(new MotionAction(robot.getCapstoneServo(), robot.CAPSTONE_RETRACT, currentTime + robot.DELAY_CAPSTONE));
            outtakeActions.add(new MotionAction(robot.getBackStopper(), robot.BACK_STOPPER_UP, currentTime + robot.DELAY_BACKSTOPPER));
            outtakeActions.add(new MotionAction(robot.getIntakePusher(), robot.PUSHER_RETRACTED, currentTime + robot.DELAY_PUSHER_ON_RETRACT));
            outtakeActions.add(new MotionAction(robot.getClamp(), robot.CLAMP_SERVO_RELEASED, currentTime + robot.DELAY_RELEASE_CLAMP_ON_RETRACT));
            outtakeActions.add(new MotionAction(robot.getOuttakeExtender(), robot.OUTTAKE_SLIDE_EXTENDED, currentTime + robot.DELAY_EXTEND_SLIDE_ON_RETRACT, true));
        } else if (!gamepad2.b) {
            isTogglingG2B = false;
        }
        if (gamepad2.x && !isTogglingG2X) {
            isTogglingG2X = true;

            outtakeActions.clear();

            outtakeActions.add(new MotionAction(robot.getCapstoneServo(), robot.CAPSTONE_RETRACT, currentTime + robot.DELAY_CAPSTONE));
            outtakeActions.add(new MotionAction(robot.getBackStopper(), robot.BACK_STOPPER_UP, currentTime + robot.DELAY_BACKSTOPPER));
            outtakeActions.add(new MotionAction(robot.getIntakePusher(), robot.PUSHER_PUSHED, currentTime + robot.DELAY_PUSHER_ON_CLAMP));
            outtakeActions.add(new MotionAction(robot.getIntakePusher(), robot.PUSHER_RETRACTED, currentTime + robot.DELAY_RETRACT_PUSHER_ON_CLAMP));
            outtakeActions.add(new MotionAction(robot.getClamp(), robot.CLAMP_SERVO_CLAMPED, currentTime + robot.DELAY_CLAMP_ON_CLAMP));
        } else if (!gamepad2.x) {
            isTogglingG2X = false;
        }
        if (gamepad2.y && !isTogglingG2Y) {
            isTogglingG2Y = true;

            outtakeActions.clear();

            outtakeActions.add(new MotionAction(robot.getCapstoneServo(), robot.CAPSTONE_RETRACT, currentTime + robot.DELAY_CAPSTONE));
            outtakeActions.add(new MotionAction(robot.getBackStopper(), robot.BACK_STOPPER_UP, currentTime + robot.DELAY_BACKSTOPPER));
            outtakeActions.add(new MotionAction(robot.getClamp(), robot.CLAMP_SERVO_CLAMPED, currentTime + robot.DELAY_CLAMP_ON_EXTEND));
            outtakeActions.add(new MotionAction(robot.getOuttakeExtender(), robot.OUTTAKE_SLIDE_EXTENDED, currentTime + robot.DELAY_SLIDE_ON_EXTEND));
            outtakeActions.add(new MotionAction(robot.getClampPivot(), robot.OUTTAKE_PIVOT_90, currentTime + robot.DELAY_PIVOT_ON_EXTEND));
        } else if (!gamepad2.y) {
            isTogglingG2Y = false;
        }

        boolean motionExecuted = true;
        while (motionExecuted) {
            currentTime = SystemClock.elapsedRealtime();
            MotionAction currentMotion = outtakeActions.peek();

            if (currentMotion != null) {
                if (currentMotion.getDelayStartTime() <= currentTime) {
                    if (currentMotion.isLocationToggle()) {
                        if ((Math.hypot(robot.getRobotPos().x - xDump, robot.getRobotPos().y - yDump) > 5) || gamepad1.b) {
                            currentMotion.executeMotion();

                            outtakeActions.add(new MotionAction(robot.getClampPivot(), robot.OUTTAKE_PIVOT_RETRACTED, currentTime + robot.DELAY_PIVOT_ON_RETRACT));
                            outtakeActions.add(new MotionAction(robot.getOuttakeExtender(), robot.OUTTAKE_SLIDE_RETRACTED, currentTime + robot.DELAY_SLIDE_ON_RETRACT));

                            outtakeActions.remove();
                        } else {
                            motionExecuted = false;
                        }
                    } else {
                        currentMotion.executeMotion();
                        outtakeActions.remove();
                    }
                } else {
                    motionExecuted = false;
                }
            } else {
                motionExecuted = false;
            }
        }
    }

    private void foundationLogic() {
        if (gamepad1.right_bumper) {
            if (foundationToggle && !resetfoundation) {
                foundationToggle = false;
            } else if (!foundationToggle && !resetfoundation) {
                foundationToggle = true;
            }
            resetfoundation = true;

            robot.getLeftFoundation().getController().pwmEnable();
        } else {
            if (!isDumpingCapstone && !foundationToggle && isBackStopperReset) {
                robot.getLeftFoundation().getController().pwmDisable();
            }
            resetfoundation = false;
        }

        robot.foundationMovers(foundationToggle);
    }

    private void capStoneLogic() {
        if (gamepad1.y) {
            robot.getCapstoneServo().setPosition(robot.CAPSTONE_RETRACT);
        }
    }

    private boolean toggleMode = true;

    private void robotModeLogic() {
        if (gamepad1.a && gamepad1.x && toggleMode) {
            if (isIntakeMode) {
                isIntakeMode = false;
            } else {
                isIntakeMode = true;
            }
            toggleMode = false;
        } else if (!toggleMode && !(gamepad1.a && gamepad1.x)) {
            toggleMode = true;
        }
    }
}