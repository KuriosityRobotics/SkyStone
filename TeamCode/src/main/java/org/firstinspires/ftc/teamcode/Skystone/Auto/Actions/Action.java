package org.firstinspires.ftc.teamcode.Skystone.Auto.Actions;

import org.firstinspires.ftc.teamcode.Skystone.Auto.Actions.Enums.*;
import org.firstinspires.ftc.teamcode.Skystone.MotionProfiler.Point;
import org.firstinspires.ftc.teamcode.Skystone.Robot;

import java.util.ArrayList;

public class Action {
    Robot robot;

    private ActionType actionType;
    private Point actionPoint;
    private boolean isExecuteOnEndOfPath;
    ActionState actionState = ActionState.PENDING;

    private long actionsStartTime;

    ArrayList<MotionAction> motionActions = new ArrayList();

    public Action(ActionType type, Point executePoint, Robot robot) {
        this.robot = robot;

        this.actionType = type;
        this.actionPoint = executePoint;

        this.isExecuteOnEndOfPath = false;

        generateActions();
    }

    public Action(ActionType type, Robot robot, boolean isExecuteOnEndOfPath) {
        this.robot = robot;

        this.actionType = type;

        this.isExecuteOnEndOfPath = isExecuteOnEndOfPath;

        generateActions();
    }

    public void executeAction(long currentTime) {
        if (actionState == ActionState.COMPLETE) {
            return;
        }

        if(actionState == ActionState.PENDING) {
            this.actionsStartTime = currentTime;
            actionState = ActionState.PROCESSING;
        }

        boolean hasPendingActions = false;

        long timeSinceActionStart = currentTime - actionsStartTime;
        for (int i = 0; i < motionActions.size(); i++) {
            MotionAction motionAction = motionActions.get(i);
            if(timeSinceActionStart >= motionAction.getDelayStartTime()) {
                motionAction.executeMotion();
            }
            if(motionAction.getStatus() != ActionState.COMPLETE) {
                hasPendingActions = true;
            }
        }

        if (!hasPendingActions) {
            actionState = ActionState.COMPLETE;
        }
    }

    private void generateActions() {
        if (actionType == ActionType.EXTEND_OUTTAKE) {
            generateExtendOuttakeActions();
        } else if (actionType == ActionType.DROPSTONE_AND_RETRACT_OUTTAKE) {
            generateDropStoneAndRetractOuttakeActions();
        } else if (actionType == ActionType.EXTEND_FOUNDATION) {
            generateExtendFoundationActions();
        } else if (actionType == ActionType.RELEASE_FOUNDATION) {
            generateReleaseFoundationActions();
        } else if (actionType == ActionType.START_INTAKE) {
            generateStartIntakeActions();
        } else if (actionType == ActionType.STOP_INTAKE) {
            generateStopIntakeActions();
        }
    }

    private void generateExtendOuttakeActions() {
        motionActions.add(new MotionAction(robot.getIntakePusher(), robot.PUSHER_PUSHED, 0));
        motionActions.add(new MotionAction(robot.getFrontClamp(), robot.FRONTCLAMP_CLAMPED, 500));
        motionActions.add(new MotionAction(robot.getBackClamp(), robot.BACKCLAMP_CLAMPED, 500));
        motionActions.add(new MotionAction(robot.getIntakePusher(), robot.PUSHER_RETRACTED, 650));
        motionActions.add(new MotionAction(robot.getOuttakeExtender(), robot.OUTTAKE_SLIDE_EXTENDED, 1300));
    }

    private void generateDropStoneAndRetractOuttakeActions() {
        motionActions.add(new MotionAction(robot.getBackClamp(), robot.BACKCLAMP_RELEASED, 0));
        motionActions.add(new MotionAction(robot.getFrontClamp(), robot.FRONTCLAMP_RELEASED, 0));
        motionActions.add(new MotionAction(robot.getOuttakeExtender(), robot.OUTTAKE_SLIDE_RETRACTED, 750));
        motionActions.add(new MotionAction(robot.getIntakePusher(), robot.PUSHER_RETRACTED, 750));
    }

    private void generateExtendFoundationActions() {
        motionActions.add(new MotionAction(robot.getLeftFoundation(), robot.LEFTFOUNDATION_EXTENDED, 0));
        motionActions.add(new MotionAction(robot.getRightFoundation(), robot.RIGHTFOUNDATION_EXTENDED, 0));
    }

    private void generateReleaseFoundationActions() {
        motionActions.add(new MotionAction(robot.getLeftFoundation(), robot.LEFTFOUNDATION_RETRACTED, 0));
        motionActions.add(new MotionAction(robot.getRightFoundation(), robot.RIGHTFOUNDATION_RETRACTED, 0));
    }

    private void generateStartIntakeActions() {
        motionActions.add(new MotionAction(robot.getIntakeLeft(), 1, 0));
        motionActions.add(new MotionAction(robot.getIntakeRight(), 1, 0));
    }

    private void generateStopIntakeActions() {
        motionActions.add(new MotionAction(robot.getIntakeLeft(), -1, 0));
        motionActions.add(new MotionAction(robot.getIntakeRight(), -1, 0));
        motionActions.add(new MotionAction(robot.getIntakeLeft(), 1, 500));
        motionActions.add(new MotionAction(robot.getIntakeRight(), 1, 500));
    }

    public Point getActionPoint() {
        return actionPoint;
    }

    public ActionState getActionState() {
        return actionState;
    }

    public boolean isExecuteOnEndOfPath() {
        return isExecuteOnEndOfPath;
    }

    public ActionType getActionType() {
        return actionType;
    }
}