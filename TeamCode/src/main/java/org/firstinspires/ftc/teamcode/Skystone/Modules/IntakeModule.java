package org.firstinspires.ftc.teamcode.Skystone.Modules;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.Skystone.HardwareCollection;
import org.firstinspires.ftc.teamcode.Skystone.Robot;

import static org.firstinspires.ftc.teamcode.Skystone.Constants.*;

public class IntakeModule {
    public double intakeLeftPower;
    public double intakeRightPower;

    public boolean isIntakeMode;

    public IntakeModule(){
        intakeLeftPower = 0.0;
        intakeRightPower = 0.0;
        isIntakeMode = false;
    }

    public synchronized void update(HardwareCollection hardwareCollection){

        if (isIntakeMode){

            // TODO change this to outtakeModule
            hardwareCollection.backClamp.setPosition(BACKCLAMP_CLAMPED);
            hardwareCollection.frontClamp.setPosition(FRONTCLAMP_CLAMPED);
            hardwareCollection.intakePusher.setPosition(PUSHER_PUSHED);

            boolean stoneInIntake = false;

            if (Double.isNaN(hardwareCollection.intakeStoneDistance.getDistance(DistanceUnit.CM))) {
                stoneInIntake = false;
            } else if (hardwareCollection.intakeStoneDistance.getDistance(DistanceUnit.CM) < 40) {
                stoneInIntake = true;
            }

            if (stoneInIntake) {
                if (intakeLeftPower > 0) {
                    intakeLeftPower = 0;
                }
                if (intakeRightPower > 0) {
                    intakeRightPower = 0;
                }
            }
        }

        hardwareCollection.intakeLeft.setPower(intakeLeftPower);
        hardwareCollection.intakeRight.setPower(intakeRightPower);
    }
}