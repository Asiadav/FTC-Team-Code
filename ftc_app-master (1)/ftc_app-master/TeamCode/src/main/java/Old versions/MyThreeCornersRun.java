import android.graphics.Color;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.NormalizedColorSensor;
import com.qualcomm.robotcore.hardware.NormalizedRGBA;
import com.qualcomm.robotcore.hardware.SwitchableLight;

import java.util.Objects;

@TeleOp(name="Three Corners Runner V1.2", group="Corners TeleOp")
public class MyThreeCornersRun extends OpMode
{
    private NormalizedColorSensor colorSensor;
    private DcMotor leftDrive;
    private DcMotor rightDrive;
    private double distance;

    // these values should equal one inch of travel
    private final double LEFT_ENCODER_RATE = 1.0;
    private final double RIGHT_ENCODER_RATE = 1.0;

    private String getThreeColor(NormalizedRGBA color)
    {
        if (color.red > color.blue)
        {
            return "red";
        }
        else if (color.blue > color.red)
        {
            return "blue";
        }
        return "white";
    }

    @Override

    //sets up the two motors and color sensor
    public void init()
    {
        colorSensor = hardwareMap.get(NormalizedColorSensor.class, "color_sensor");
        leftDrive = hardwareMap.get(DcMotor.class, "left_drive");
        rightDrive = hardwareMap.get(DcMotor.class, "right_drive");

        leftDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rightDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        leftDrive.setDirection(DcMotorSimple.Direction.FORWARD);
        rightDrive.setDirection(DcMotorSimple.Direction.FORWARD);

        leftDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rightDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        distance = 0.0;

        if (colorSensor instanceof SwitchableLight)
        {
            ((SwitchableLight)colorSensor).enableLight(true);
        }
    }

    @Override

    //both motors at a stopped value
    public void init_loop()
    {
        leftDrive.setPower(0.0);
        rightDrive.setPower(0.0);
        distance = 0.0;
    }



    @Override

    public void start()
    {

        NormalizedRGBA colors = colorSensor.getNormalizedColors();


        telemetry.addData("COLOR", this.getThreeColor(colors));
        switch (Objects.requireNonNull(this.getThreeColor(colors)))
        {
            case "red":
                //move left
                distance = 144;
                telemetry.addData("Moving", "RED");
            case "blue":
                //move right
                distance = 144;
                telemetry.addData("Moving", "BLUE");
            case "white":
                //move forward
                distance = 200;
                telemetry.addData("Moving", "WHITE");
        }
        telemetry.addData("DISTANCE", distance);
        telemetry.update();


        leftDrive.setTargetPosition((int) (distance * LEFT_ENCODER_RATE));
        leftDrive.setTargetPosition((int) (distance * RIGHT_ENCODER_RATE));

        leftDrive.setPower(100);
        rightDrive.setPower(100);
        leftDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        rightDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);





    }
    @Override
    public void loop()
    {
        NormalizedRGBA colors = colorSensor.getNormalizedColors();

        telemetry.addData("Target Pos:", distance);
        telemetry.addData("Target Motor:", (int) (distance * (LEFT_ENCODER_RATE + RIGHT_ENCODER_RATE) / 2));
        telemetry.addData("Distance Left:", (distance - (int)(distance * (LEFT_ENCODER_RATE + RIGHT_ENCODER_RATE) / 2)));
        telemetry.addData("Red:", colors);

        telemetry.update();
    }
}
