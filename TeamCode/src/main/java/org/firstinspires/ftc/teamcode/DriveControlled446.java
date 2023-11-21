package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

@TeleOp
public class DriveControlled446 extends LinearOpMode {

    //Primary Motor Defintions
    private DcMotor motorFL;
    private DcMotor motorFR;
    private DcMotor motorBL;
    private DcMotor motorBR;

    //Secondary Motor Definitions
    private DcMotor intakeMotor;
    private DcMotor sliderMotor;
    private DcMotor liftLeft;
    private DcMotor liftRight;

    //Servo Definitions
    private Servo frontIntake1;
    private Servo frontIntake2;
    private Servo outtake;
    private Servo flipper;

    // endregion
    @Override
    public void runOpMode() {

        //Drivetrain DC motors
        motorFL = hardwareMap.get(DcMotor.class, "motorFrontLeft");
        motorBL = hardwareMap.get(DcMotor.class, "motorBackLeft");
        motorFR = hardwareMap.get(DcMotor.class, "motorFrontRight");
        motorBR = hardwareMap.get(DcMotor.class, "motorBackRight");

        //Secondary system DC motors
        intakeMotor = hardwareMap.get(DcMotor.class, "intakeMotor");
        sliderMotor = hardwareMap.get(DcMotor.class, "sliderMotor");
        liftLeft = hardwareMap.get(DcMotor.class, "liftLeft");
        liftRight = hardwareMap.get(DcMotor.class, "liftRight");

        //Reverse left side motors
        motorFL.setDirection(DcMotorSimple.Direction.REVERSE);
        motorBL.setDirection(DcMotorSimple.Direction.REVERSE);

        //Encoder Setup
        sliderMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        liftLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        liftRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        sliderMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        liftLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        liftRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        sliderMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        liftLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        liftRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        // region Servos
        frontIntake1 = hardwareMap.get(Servo.class, "frontIntake1");
        frontIntake2 = hardwareMap.get(Servo.class, "frontIntake2");
        outtake = hardwareMap.get(Servo.class, "outtake");
        flipper = hardwareMap.get(Servo.class, "flipper");
        // endregion

        //Intake Linkage Servo
        double linkageLeftPos;
        double linkageRightPos;

        //Initial Positions
        linkageLeftPos = 0.0;
        linkageRightPos = 0.0;

        //Boolean variables
        boolean intakeOn = false;

        waitForStart();

        if (isStopRequested())
            return;

        while (opModeIsActive()) {

            // region Mecanum Drive
            // Gamepad inputs
            double y = -gamepad1.left_stick_y; // Reverse the y-axis (if needed)
            double x = gamepad1.left_stick_x;
            double rotation = gamepad1.right_stick_x;

            // Calculate motor powers
            double frontLeftPower = y + x + rotation;
            double frontRightPower = y - x - rotation;
            double backLeftPower = y - x + rotation;
            double backRightPower = y + x - rotation;

            // Clip motor powers to ensure they are within the valid range [-1, 1]
            frontLeftPower = Range.clip(frontLeftPower, -1, 1);
            frontRightPower = Range.clip(frontRightPower, -1, 1);
            backLeftPower = Range.clip(backLeftPower, -1, 1);
            backRightPower = Range.clip(backRightPower, -1, 1);

            // Set motor powers
            motorFL.setPower(frontLeftPower);
            motorFR.setPower(frontRightPower);
            motorBL.setPower(backLeftPower);
            motorBR.setPower(backRightPower);
            // endregion

            //Intake Motor Code
            if ((gamepad2.right_trigger > 0.0) && !intakeOn){
                intakeMotor.setPower(1);
            }else if((gamepad2.right_trigger == 0.0) && !intakeOn){
                intakeMotor.setPower(0);
            }

            if ((gamepad2.left_trigger > 0.0) && !intakeOn){
                intakeMotor.setPower(-1);
            }else if((gamepad2.left_trigger == 0.0) && !intakeOn){
                intakeMotor.setPower(0);
            }

            if(gamepad2.a && !intakeOn){
                intakeMotor.setPower(1);
                intakeOn = true;
            }else if(gamepad2.a && intakeOn){
                intakeMotor.setPower(0);
                intakeOn = false;
            }

            //Encoder Values for the lift
            if(gamepad2.dpad_up){
                liftLeft.setTargetPosition(35000);
                rightLift.setTargetPosition(35000);
                liftLeft.setPower(1);
                liftRight.setPower(1);
            }else if(gamepad2.dpad_down){
                liftLeft.setTargetPosition(0);
                rightLift.setTargetPosition(0);
                liftLeft.setPower(1);
                liftRight.setPower(1);
            }
            
            // If you press the left trigger then the intake motor direction is reversed
            boolean intakeDirectionForwards = true;
            if (gamepad1.left_trigger == 1) {
                if (intakeDirectionForwards) {
                    intakeMotor.setDirection(DcMotorSimple.Direction.REVERSE);
                    intakeDirectionForwards = false;
                } else {
                    intakeMotor.setDirection(DcMotorSimple.Direction.FORWARD);
                    intakeDirectionForwards = true;
                }
            }
            
            // Pressing the left trigger will also spin the intake
            double intakePower = gamepad1.right_trigger;
            intakeMotor.setPower(intakePower);

            // Intake servos
            if (gamepad2.dpad_up) {
                frontIntake1.setPosition(1);
                frontIntake2.setPosition(1);
            }
            if (gamepad2.dpad_down) {
                frontIntake1.setPosition(0);
                frontIntake2.setPosition(0);
                outtake.setPosition(0);
            }
            frontIntake1.setPosition(gamepad2.left_stick_y);
            frontIntake2.setPosition(gamepad2.left_stick_y);

            // Flipper
            boolean isFlipperOpen = false;
            if (gamepad2.y) {
                if (!isFlipperOpen) {
                    flipper.setPosition(1);
                    isFlipperOpen = true;
                }
                else {
                    flipper.setPosition(0);
                    isFlipperOpen = false;
                }
            }
            
            // Drivetrain Telemetry
            telemetry.addData("LF Power:", motorFL.getPower());
            telemetry.addData("LB Power:", motorBL.getPower());
            telemetry.addData("RF Power:", motorFR.getPower());
            telemetry.addData("RB Power:", motorBR.getPower());

            //Intake Motor telemetry
            telemetry.addData("Intake Motor Power: ", intakeMotor.getPower());

            //Slider telemetry
            telemetry.addData("Slider Power: ", sliderMotor.getPower());
            telemetry.addData("Slider Position: ", sliderMotor.getCurrentPosition());

            //Lift telemetry
            telemetry.addData("Lift Left Power:", liftLeft.getPower());
            telemetry.addData("Lift Right Power:", liftRight.getPower());
            telemetry.addData("Lift Left Position:", liftLeft.getCurrentPosition());
            telemetry.addData("Lift Right Position:", liftRight.getCurrentPosition());

            //Intake Servo telemetry
            //telemetry.addData("Intake Left Position: ", frontIntake1.getCurrentPosition());
            //telemetry.addData("Intake Right Position: ", frontIntake2.getCurrentPosition());

            //Outtake telemetry
            //telemetry.addData("Outtake Position: ", outtake.getCurrentPosition());
            //telemetry.addData("Flipper position: ", flipper.getCurrentPosition());
            telemetry.update();
            //endregion
        }
    }
}

