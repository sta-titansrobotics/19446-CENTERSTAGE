package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.CRServo;
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
    private Servo flipper;
    private CRServo outtake;

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

        //Servo Mapping
        frontIntake1 = hardwareMap.get(Servo.class, "frontIntake1");
        frontIntake2 = hardwareMap.get(Servo.class, "frontIntake2");
        flipper = hardwareMap.get(Servo.class, "flipper");
        outtake = hardwareMap.get(CRServo.class, "outtake");

        //Intake Linkage Servo
        double linkageLeftPos;
        double linkageRightPos;

        double linkageLeftMax = 1;
        double linkageLeftMin = 0;
        double linkageRightMax = 1;
        double linkageRightMin = 0;

        //Flipper Variables
        boolean isFlipperOpen = false;
        double flipperPos;
        double open = 2.1;
        double closed = 0.0;

        //Slider Positioning
        double sliderPos;
        double sliderMax;
        double sliderPower;
        sliderMax = 10000;

        //Initial Positions
        linkageLeftPos = 0.0;
        linkageRightPos = 0.0;

        //Boolean variables
        boolean intakeOn = false;

        waitForStart();

        if (isStopRequested())
            return;

        while (opModeIsActive()) {

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
                outtake.setPower(1);
                intakeOn = true;
            }else if(gamepad2.a && intakeOn){
                intakeMotor.setPower(0);
                outtake.setPower(0);
                intakeOn = false;
            }
            
            linkageLeftPos += 0.05 * gamepad2.left_stick_y;
            linkageRightPos += 0.05 * gamepad2.left_stick_y;
            
            // Sets max and min values for the linkage positions
            linkageLeftPos = Range.clip(linkageLeftPos, linkageLeftMin, linkageLeftMax);
            linkageRightPos = Range.clip(linkageRightPos, linkageRightMin, linkageRightMax);

            frontIntake1.setPosition(linkageLeftPos);
            frontIntake2.setPosition(linkageRightPos);

            //Encoder Values for the lift
            if(gamepad1.dpad_up){
                liftLeft.setTargetPosition(35000);
                liftRight.setTargetPosition(35000);
                liftLeft.setPower(1);
                liftRight.setPower(1);
            }else if(gamepad1.dpad_down){
                liftLeft.setTargetPosition(0);
                liftRight.setTargetPosition(0);
                liftLeft.setPower(1);
                liftRight.setPower(1);
            }

            //Pixel Release
            if(gamepad2.right_bumper){
                outtake.setPower(-1);
            }else if(gamepad2.left_bumper){
                outtake.setPower(-1);
            }

            //Slider Control
            sliderPower = -gamepad2.right_stick_y;
            sliderPos = sliderMotor.getCurrentPosition();

            sliderMotor.setPower(sliderPower);

            /*
             * Slider Limiters
            if(sliderPower > 0 && sliderPos > 0){
                sliderMotor.setPower(sliderPower);
            }else if(sliderPower < 0 && sliderPos < sliderMax){
                sliderMotor.setPower(sliderPower);
            }else{
                sliderMotor.setPower(0);
            }
             */


            //Flipper control
            if(sliderPower >  0){
                flipperPos = open;
                isFlipperOpen = true;
            }else if(sliderPower < 0){
                flipperPos = closed;
                isFlipperOpen = false;
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
            telemetry.addData("Intake Left Position: ", frontIntake1.getPosition());
            telemetry.addData("Intake Right Position: ", frontIntake2.getPosition());

            //Outtake telemetry
            telemetry.addData("Outtake Power: ", outtake.getPower());
            telemetry.addData("Flipper position: ", flipper.getPosition());
            telemetry.update();
        }
    }
}
