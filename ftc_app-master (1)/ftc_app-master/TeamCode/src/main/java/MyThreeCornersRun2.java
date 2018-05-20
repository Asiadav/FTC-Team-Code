import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.NormalizedColorSensor;
import com.qualcomm.robotcore.hardware.NormalizedRGBA;
import com.qualcomm.robotcore.hardware.SwitchableLight;
import com.qualcomm.robotcore.util.ElapsedTime;

import java.util.Objects;

@TeleOp(name="Three Corners Runner V2.1", group="Corners TeleOp")
public class MyThreeCornersRun2 extends OpMode
{
    private NormalizedColorSensor colorSensor;
    private DcMotor leftDrive;
    private DcMotor rightDrive;
    private double distance;
    private double turn;
    private boolean run = false;
    private ElapsedTime runtime = new ElapsedTime();
    private float GoToCheck;


    //motor ticks per inch
    private final double LEFT_ENCODER_RATE = 73.0; //make smaller
    private final double RIGHT_ENCODER_RATE = 73.5;

    private String getThreeColor(NormalizedRGBA colors)
    {

        if (colors.red > 0.1 && colors.blue > 0.1 && colors.green > 0.1)
        {
            return "white";
        }
        else if (colors.red > colors.green && colors.red > colors.blue)
        {
            return "red";
        }
        else if (colors.blue > colors.green && colors.blue > colors.red)
        {
            return "blue";
        }
        return "white";
    }

    @Override
    public void init()
    {
        colorSensor = hardwareMap.get(NormalizedColorSensor.class, "color_sensor");
        leftDrive = hardwareMap.get(DcMotor.class, "left_drive");
        rightDrive = hardwareMap.get(DcMotor.class, "right_drive");

        leftDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rightDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        leftDrive.setDirection(DcMotorSimple.Direction.FORWARD);
        rightDrive.setDirection(DcMotorSimple.Direction.REVERSE);

        leftDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rightDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        distance = 0.0;
        turn = 5.1;

        if (colorSensor instanceof SwitchableLight)
        {
            ((SwitchableLight)colorSensor).enableLight(true);
        }
    }

    @Override
    public void init_loop()
    {
        leftDrive.setPower(0.0);
        rightDrive.setPower(0.0);
    }

    @Override
    public void start()
    {
        NormalizedRGBA colors = colorSensor.getNormalizedColors();

        telemetry.addData("COLOR", this.getThreeColor(colors));
        switch (Objects.requireNonNull(this.getThreeColor(colors)))
        {
            case "white": // still moves right for some reason
                //move left
                distance = 132;
                telemetry.addData("Moving", "WHITE");

                leftDrive.setTargetPosition((int)(-turn*LEFT_ENCODER_RATE) + leftDrive.getCurrentPosition());
                rightDrive.setTargetPosition((int)(turn*RIGHT_ENCODER_RATE) + rightDrive.getCurrentPosition());
                GoToCheck = (int)(-turn*LEFT_ENCODER_RATE) + leftDrive.getCurrentPosition();
                leftDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                rightDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);

                leftDrive.setPower(-1);
                rightDrive.setPower(1);
                break;

            case "blue":
                //move right
                distance = 132;
                telemetry.addData("Moving", "BLUE");

                leftDrive.setTargetPosition((int)(turn*LEFT_ENCODER_RATE) + leftDrive.getCurrentPosition());
                rightDrive.setTargetPosition((int)(-turn*RIGHT_ENCODER_RATE) + rightDrive.getCurrentPosition());
                GoToCheck = (int)(turn*LEFT_ENCODER_RATE) + leftDrive.getCurrentPosition();

                leftDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                rightDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);

                leftDrive.setPower(1);
                rightDrive.setPower(-1);
                break;
            case "red":
                //move forward
                distance = 190;
                telemetry.addData("Moving", "RED");
                break;
        }


        runtime.reset();
    }

    @Override
    public void loop()
    {
        NormalizedRGBA colors = colorSensor.getNormalizedColors();


      //  if (runtime.time() > 6)
       // {
       //     this.requestOpModeStop();
       //     leftDrive.setPower(0);
       //     rightDrive.setPower(0);
       // }



        if (run && (leftDrive.getPower() == 0 && rightDrive.getPower() == 0))
        {
            this.requestOpModeStop();
            leftDrive.setPower(0);
            rightDrive.setPower(0);
        }



        if (Math.abs(leftDrive.getCurrentPosition()) + 100 > Math.abs(GoToCheck) && (Math.abs(leftDrive.getCurrentPosition())-100 < Math.abs(GoToCheck) && !run))
        {
            GoToCheck = 0;

            leftDrive.setTargetPosition((int) (distance*LEFT_ENCODER_RATE) + leftDrive.getCurrentPosition());
            rightDrive.setTargetPosition((int) (distance*RIGHT_ENCODER_RATE) + rightDrive.getCurrentPosition());

            leftDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            rightDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);

            leftDrive.setPower(1);
            rightDrive.setPower(1);

            run = true;
        }

        if (Math.abs(leftDrive.getCurrentPosition()) >= Math.abs(leftDrive.getTargetPosition()) && run)
        {
            leftDrive.setPower(0);
            rightDrive.setPower(0);
        }


        if (run) {
            telemetry.addData("Running", 1);

        }


        telemetry.addData("left power:", leftDrive.getPower());
        telemetry.addData("right power:", rightDrive.getPower());


        telemetry.addData("Current Pos L:", leftDrive.getCurrentPosition());
        telemetry.addData("Current Pos R:", rightDrive.getCurrentPosition());
        telemetry.addData(" L going to:", leftDrive.getTargetPosition());
        telemetry.addData(" R going to:", rightDrive.getTargetPosition());


        //telemetry.addData("Target Pos:", distance);
        //telemetry.addData("Target Enc:", "R:" + distance * RIGHT_ENCODER_RATE + "\nL:" + distance * LEFT_ENCODER_RATE);
        telemetry.addData("COLOR", this.getThreeColor(colors));
        telemetry.update();

    }
}
