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
public class Adjustment extends LinearOpMode {

    //Secondary Motor Definitions
    private DcMotor sliderMotor;
    private DcMotor liftLeft;
    private DcMotor liftRight;

    //Servo Definitions
    private Servo flipper;

    @Override
    public void runOpMode() {
        //Secondary system DC motors
        sliderMotor = hardwareMap.get(DcMotor.class, "sliderMotor");
        liftLeft = hardwareMap.get(DcMotor.class, "liftLeft");
        liftRight = hardwareMap.get(DcMotor.class, "liftRight");

        //Reverse left side motors and slider Motor
        sliderMotor.setDirection(DcMotorSimple.Direction.REVERSE);

        //Encoder Setup
        sliderMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        liftLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        liftRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        sliderMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        liftLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        liftRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        sliderMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        liftLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        liftRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        //Servo Mapping
        flipper = hardwareMap.get(Servo.class, "flipper");
        //outtake = hardwareMap.get(CRServo.class, "outtake");

        //Flipper Variables
        double flipperPos = 0;

        //Slider Positioning
        double sliderPos;
        double sliderPower;
        
        waitForStart();

        if (isStopRequested())
            return;

        while (opModeIsActive()) {
            //Flipper
            if(gamepad1.left_stick_y < 0){
                flipperPos += 0.05;
            }else if(gamepad1.left_stick_y > 0){
                flipperPos -= 0.05;
            }

            flipper.setPosition(flipperPos);

            //Linear Actuators
            if(gamepad2.right_stick_y < 0){
                liftRight.setPower(1);
            }else if(gamepad2.right_stick_y > 0){
                liftRight.setPower(-1);
            }else if(gamepad2.left_stick_y < 0){
                liftLeft.setPower(1);
            }else if(gamepad2.left_stick_y > 0){
                liftLeft.setPower(-1);
            }

            //Slider
            if(gamepad1.right_stick_y < 0){
                
            }

            //Telemetry
            telemetry.addData("Slider Power: ", sliderMotor.getPower());
            telemetry.addData("Slider Position: ", sliderMotor.getCurrentPosition());
            telemetry.addData("Lift Left Power:", liftLeft.getPower());
            telemetry.addData("Lift Right Power:", liftRight.getPower());
            telemetry.addData("Lift Left Position:", liftLeft.getCurrentPosition());
            telemetry.addData("Lift Right Position:", liftRight.getCurrentPosition());
            telemetry.addData("Flipper position: ", flipper.getPosition());
            telemetry.update();
        }
    }
}
