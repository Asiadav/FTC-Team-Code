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
public class ObstacleAvoidance1 extends OpMode
{
    private NormalizedColorSensor colorSensor;
    private DcMotor leftDrive;
    private DcMotor rightDrive;
    private double distance0;
    private double distance1;
    private double distance2;
    private double distance3;
    private double turn0;
    private double turn1;

    private boolean run0 = false;
    private boolean run1 = false;
    private boolean run2 = false;
    private boolean run3 = false;

    private ElapsedTime runtime = new ElapsedTime();
    private float GoToCheck0;
    private float GoToCheck1;
    private float GoToCheck2;
    private float GoToCheck3;

    private String color;


    //motor ticks per inch
    private final double LEFT_ENCODER_RATE = 73.0; //make smaller
    private final double RIGHT_ENCODER_RATE = 73.5;

    private String getThreeColor(NormalizedRGBA colors)
    {

        if (colors.red > 0.1 && colors.blue > 0.1 && colors.green > 0.1)
        {
            color =  "white";
        }
        else if (colors.red > colors.green && colors.red > colors.blue)
        {
            color =  "red";
        }
        else if (colors.blue > colors.green && colors.blue > colors.red)
        {
            color =  "blue";
        }
        return color;
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

        distance0 = 60; // forward to turn
        distance1 = 60; // pass obstacles
        distance2 = 60; // to red
        distance3 = 60; // to white and blue

        turn0 = 5.1; //45 degrees
        turn1 = 10.2;

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

        leftDrive.setTargetPosition((int) (distance0*LEFT_ENCODER_RATE) + leftDrive.getCurrentPosition());
        rightDrive.setTargetPosition((int) (distance0*RIGHT_ENCODER_RATE) + rightDrive.getCurrentPosition());

        GoToCheck0 = (int)(distance0 * LEFT_ENCODER_RATE) + leftDrive.getCurrentPosition();


        leftDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        rightDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        leftDrive.setPower(1);
        rightDrive.setPower(1);

        runtime.reset();
    }

    @Override
    public void loop()
    {

        if ((Math.abs(leftDrive.getCurrentPosition()) + 50 > Math.abs(GoToCheck0) && (Math.abs(leftDrive.getCurrentPosition())-50 < Math.abs(GoToCheck0))))
        {
            GoToCheck0 = 0;
            run0 = true;
        }
        if ((Math.abs(leftDrive.getCurrentPosition()) + 50 > Math.abs(GoToCheck1) && (Math.abs(leftDrive.getCurrentPosition())-50 < Math.abs(GoToCheck1))))
        {
            GoToCheck1 = 0;
            run1 = true;
        }
        if ((Math.abs(leftDrive.getCurrentPosition()) + 50 > Math.abs(GoToCheck2) && (Math.abs(leftDrive.getCurrentPosition())-50 < Math.abs(GoToCheck2))))
        {
            GoToCheck2 = 0;
            run2 = true;
        }
        if ((Math.abs(leftDrive.getCurrentPosition()) + 50 > Math.abs(GoToCheck3) && (Math.abs(leftDrive.getCurrentPosition())-50 < Math.abs(GoToCheck3))))
        {
            GoToCheck3 = 0;
            run3 = true;
        }

        if (run0) // turns to pass obstacles
        {
            run0 = false;

            if (color == "white")//left
           {
               leftDrive.setTargetPosition((int)(-turn0*LEFT_ENCODER_RATE) + leftDrive.getCurrentPosition());
               rightDrive.setTargetPosition((int)(turn0*RIGHT_ENCODER_RATE) + rightDrive.getCurrentPosition());
               GoToCheck1 = (int)(-turn0*LEFT_ENCODER_RATE) + leftDrive.getCurrentPosition();

               leftDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);
               rightDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);

               leftDrive.setPower(-1);
               rightDrive.setPower(1);
           }
           if (color == "red")//forward
           {
               leftDrive.setTargetPosition((int)(-turn0*LEFT_ENCODER_RATE) + leftDrive.getCurrentPosition());
               rightDrive.setTargetPosition((int)(turn0*RIGHT_ENCODER_RATE) + rightDrive.getCurrentPosition());
               GoToCheck1 = (int)(turn0*LEFT_ENCODER_RATE) + leftDrive.getCurrentPosition();

               leftDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);
               rightDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);

               leftDrive.setPower(-1);
               rightDrive.setPower(1);
           }
           if (color == "blue")//right
           {
               leftDrive.setTargetPosition((int)(turn0*LEFT_ENCODER_RATE) + leftDrive.getCurrentPosition());
               rightDrive.setTargetPosition((int)(-turn0*RIGHT_ENCODER_RATE) + rightDrive.getCurrentPosition());
               GoToCheck1 = (int)(turn0*LEFT_ENCODER_RATE) + leftDrive.getCurrentPosition();

               leftDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);
               rightDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);

               leftDrive.setPower(1);
               rightDrive.setPower(-1);
           }
        }

        if (run1) // passes first obstacles
        {
            run1 = false;

            leftDrive.setTargetPosition((int)(distance1*LEFT_ENCODER_RATE) + leftDrive.getCurrentPosition());
            rightDrive.setTargetPosition((int)(distance1*RIGHT_ENCODER_RATE) + rightDrive.getCurrentPosition());
            GoToCheck1 = (int)(distance1*LEFT_ENCODER_RATE) + leftDrive.getCurrentPosition();

            leftDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            rightDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);

            leftDrive.setPower(1);
            rightDrive.setPower(1);

        }

        if (run2)
        {
            run2 = false;

            if (color == "white")//left
            {
                leftDrive.setTargetPosition((int)(-turn1*LEFT_ENCODER_RATE) + leftDrive.getCurrentPosition());
                rightDrive.setTargetPosition((int)(turn1*RIGHT_ENCODER_RATE) + rightDrive.getCurrentPosition());
                GoToCheck1 = (int)(-turn1*LEFT_ENCODER_RATE) + leftDrive.getCurrentPosition();

                leftDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                rightDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);

                leftDrive.setPower(-1);
                rightDrive.setPower(1);
            }
            if (color == "red")//forward
            {
                leftDrive.setTargetPosition((int)(turn1*LEFT_ENCODER_RATE) + leftDrive.getCurrentPosition());
                rightDrive.setTargetPosition((int)(-turn1*RIGHT_ENCODER_RATE) + rightDrive.getCurrentPosition());
                GoToCheck1 = (int)(turn1*LEFT_ENCODER_RATE) + leftDrive.getCurrentPosition();

                leftDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                rightDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);

                leftDrive.setPower(1);
                rightDrive.setPower(-1);
            }
            if (color == "blue")//right
            {
                leftDrive.setTargetPosition((int)(turn1*LEFT_ENCODER_RATE) + leftDrive.getCurrentPosition());
                rightDrive.setTargetPosition((int)(-turn1*RIGHT_ENCODER_RATE) + rightDrive.getCurrentPosition());
                GoToCheck1 = (int)(turn1*LEFT_ENCODER_RATE) + leftDrive.getCurrentPosition();

                leftDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                rightDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);

                leftDrive.setPower(1);
                rightDrive.setPower(-1);
            }


        }

        if (run3)
        {
            run3 = false;
            if (color == "red")
            {
                leftDrive.setTargetPosition((int)(distance2*LEFT_ENCODER_RATE) + leftDrive.getCurrentPosition());
                rightDrive.setTargetPosition((int)(distance2*RIGHT_ENCODER_RATE) + rightDrive.getCurrentPosition());
                GoToCheck1 = (int)(distance1*LEFT_ENCODER_RATE) + leftDrive.getCurrentPosition();

                leftDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                rightDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);

                leftDrive.setPower(1);
                rightDrive.setPower(1);


            }
            else
                {
                leftDrive.setTargetPosition((int)(distance3*LEFT_ENCODER_RATE) + leftDrive.getCurrentPosition());
                rightDrive.setTargetPosition((int)(distance3*RIGHT_ENCODER_RATE) + rightDrive.getCurrentPosition());
                GoToCheck1 = (int)(distance3*LEFT_ENCODER_RATE) + leftDrive.getCurrentPosition();

                leftDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                rightDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);

                leftDrive.setPower(1);
                rightDrive.setPower(1);
            }


        }

        telemetry.addData("run0:", run0);
        telemetry.addData("run1:", run1);
        telemetry.addData("run2:", run2);
        telemetry.addData("run3:", run3);

        telemetry.update();

    }
}
