package org.firstinspires.ftc.teamcode.Skystone;

import android.os.SystemClock;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.tfod.Recognition;
import org.firstinspires.ftc.robotcore.external.tfod.TFObjectDetector;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Vision{
    private final String VUFORIA_KEY = "AbSCRq//////AAAAGYEdTZut2U7TuZCfZGlOu7ZgOzsOlUVdiuQjgLBC9B3dNvrPE1x/REDktOALxt5jBEJJBAX4gM9ofcwMjCzaJKoZQBBlXXxrOscekzvrWkhqs/g+AtWJLkpCOOWKDLSixgH0bF7HByYv4h3fXECqRNGUUCHELf4Uoqea6tCtiGJvee+5K+5yqNfGduJBHcA1juE3kxGMdkqkbfSjfrNgWuolkjXR5z39tRChoOUN24HethAX8LiECiLhlKrJeC4BpdRCRazgJXGLvvI74Tmih9nhCz6zyVurHAHttlrXV17nYLyt6qQB1LtVEuSCkpfLJS8lZWS9ztfC1UEfrQ8m5zA6cYGQXjDMeRumdq9ugMkS";

    float value = 0;

    public enum Location{
        CENTER,LEFT,RIGHT,UNKNOWN;
    }

    private Location location = Location.UNKNOWN;
    private Robot robot;

    private VuforiaLocalizer vuforia;

    private TFObjectDetector tfod;

    public Vision(Robot robot){
        this.robot = robot;
    }
    public void initVision(){
        initVuforia();
        initTfod();

        tfod.activate();
    }

    public Location runDetection(){
        double shortestDetectionLength = Double.MAX_VALUE;
        float shortestBlockValue = 0;
        double difference = 0;

        /**
         * if it sees something/object detected : get the confidence
         * if confidence is greater than 0.7 : find its position and return that
         * if confidence is less than 0.7 : get the position it thinks, go through the code again, if is the same, then return that
         */

        long startTime = SystemClock.elapsedRealtime();
        // Repeat scanning for 5 seconds
        while (robot.getLinearOpMode().opModeIsActive() && SystemClock.elapsedRealtime()-startTime < 6000){
            // get all new detections
            List<Recognition> updatedRecognitions = tfod.getUpdatedRecognitions();

            // if there is a new detection run the logic
            if (updatedRecognitions != null && updatedRecognitions.size()>0) {
                // sort detections based on confidence levels
                Collections.sort(updatedRecognitions, new Comparator<Recognition>() {
                    @Override
                    public int compare(Recognition recognition, Recognition t1) {
                        return (int)(recognition.getConfidence()-t1.getConfidence());
                    }
                });

                // For every detection, see if its confidence level is greater than .5. If so, find the width of the detection and store the shortest detection.
                for (int i = 0; i < updatedRecognitions.size(); i++){
                    if (updatedRecognitions.get(i).getConfidence() >= 0.5){
                        if (updatedRecognitions.get(i).getTop()-updatedRecognitions.get(i).getBottom() < shortestDetectionLength) {
                            difference = shortestDetectionLength - (updatedRecognitions.get(i).getTop()-updatedRecognitions.get(i).getBottom());
                            shortestDetectionLength = updatedRecognitions.get(i).getTop()-updatedRecognitions.get(i).getBottom();
                            shortestBlockValue = (updatedRecognitions.get(i).getTop()+updatedRecognitions.get(i).getBottom())/2;
                        }
                    }
                    robot.getTelemetry().addLine("Shortest Value: " + shortestBlockValue);
                    robot.getTelemetry().update();
                }
                    if (shortestBlockValue > 200) {
                        location = Location.LEFT;
                    } else if (shortestBlockValue > 800){
                        location = Location.CENTER;
                    } else{
                        location = Location.RIGHT;
                    }
                return location;
            }
        }
        return location;
    }

    private void initVuforia() {

        /*
         * Configure Vuforia by creating a Parameter object, and passing it to the Vuforia engine.
         */

        VuforiaLocalizer.Parameters parameters = new VuforiaLocalizer.Parameters();
        parameters.vuforiaLicenseKey = VUFORIA_KEY;
        parameters.cameraDirection = VuforiaLocalizer.CameraDirection.BACK;

        //  Instantiate the Vuforia engine
        vuforia = ClassFactory.getInstance().createVuforia(parameters);

        // Loading trackables is not necessary for the TensorFlow Object Detection engine.
    }

    private void initTfod() {
        final String TFOD_MODEL_ASSET = "Skystone.tflite";
        final String LABEL_FIRST_ELEMENT = "Stone";
        final String LABEL_SECOND_ELEMENT = "Skystone";
        int tfodMonitorViewId = robot.getHardwareMap().appContext.getResources().getIdentifier(
                "tfodMonitorViewId", "id", robot.getHardwareMap().appContext.getPackageName());
        TFObjectDetector.Parameters tfodParameters = new TFObjectDetector.Parameters(tfodMonitorViewId);
        tfodParameters.minimumConfidence = 0.5;

        tfod = ClassFactory.getInstance().createTFObjectDetector(tfodParameters, vuforia);
        tfod.loadModelFromAsset(TFOD_MODEL_ASSET, LABEL_FIRST_ELEMENT, LABEL_SECOND_ELEMENT);
    }
    public float getValue(){
        return value;
    }
}
