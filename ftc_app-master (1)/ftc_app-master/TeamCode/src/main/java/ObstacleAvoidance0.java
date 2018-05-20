import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.NormalizedColorSensor;
import com.qualcomm.robotcore.hardware.NormalizedRGBA;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.SwitchableLight;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.hardware.UltrasonicSensor;


import java.util.Objects;

@TeleOp(name="Obstacle Avoidance v0", group="Corners TeleOp")
public class ObstacleAvoidance0 extends OpMode
{
    private NormalizedColorSensor colorSensor;
    private UltrasonicSensor ultra;  // Hardware Device Object
    private DcMotor leftDrive;
    private DcMotor rightDrive;
    private double distance;
    private double turn;
    private boolean run = false;
    private ElapsedTime runtime = new ElapsedTime();
    private float GoToCheck;
    private boolean obstacle = false;
    private boolean obstacleLeft = false;
    private double obstacleDir = 0.0;
    private double obstacleDirLeft = 90.0;
    private double obstacleDirRight = 90.0;



    //for servo
    private final double INCREMENT   = 0.001;     // amount to slew servo each CYCLE_MS cycle
    private final double MAX_POS     =  1.0;     // Maximum rotational position
    private final double MIN_POS     =  0.0;     // Minimum rotational position
    private Servo servo;
    private double  position = (MAX_POS - MIN_POS) / 2; // Start at halfway position
    private boolean rampUp = true;

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

    private double getObstacleDir ()
    {
        double angle = servo.getPosition() * 180;
        obstacleDir = servo.getPosition();
        if (angle < obstacleDirLeft) obstacleDirLeft = angle;
        if (angle > obstacleDirRight) obstacleDirRight = angle;
        return angle;
    }

    @Override
    public void init()
    {
        colorSensor = hardwareMap.get(NormalizedColorSensor.class, "color_sensor");
        leftDrive = hardwareMap.get(DcMotor.class, "left_drive");
        rightDrive = hardwareMap.get(DcMotor.class, "right_drive");
        servo = hardwareMap.get(Servo.class, "sensor_servo");
        ultra = hardwareMap.ultrasonicSensor.get("ultra");

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
        double sens = ultra.getUltrasonicLevel();
        NormalizedRGBA colors = colorSensor.getNormalizedColors();

        if (10 < sens && sens < 40)
        {
            obstacle = true;
            obstacleDir = this.getObstacleDir();
            if (servo.getPosition() > MAX_POS/2) obstacleLeft = true;
            else obstacleLeft = false;
        }

        if (sens > 40) obstacle = false;

        if (rampUp) {
            // Keep stepping up until we hit the max value.
            position += INCREMENT ;
            if (position >= MAX_POS ) {
                position = MAX_POS;
                rampUp = !rampUp;   // Switch ramp direction
            }
        }
        else {
            // Keep stepping down until we hit the min value.
            position -= INCREMENT ;
            if (position <= MIN_POS ) {
                position = MIN_POS;
                rampUp = !rampUp;  // Switch ramp direction
            }
        }

        servo.setPosition(position);

        if (run && (leftDrive.getPower() == 0 && rightDrive.getPower() == 0))
        {
            this.requestOpModeStop();
            leftDrive.setPower(0);
            rightDrive.setPower(0);
        }


        if (Math.abs(leftDrive.getCurrentPosition()) + 50 > Math.abs(GoToCheck) && (Math.abs(leftDrive.getCurrentPosition())-50 < Math.abs(GoToCheck)) && !run && !obstacle)
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

        if (obstacle && run) //location of obstacle avoidance procedure
        {
            leftDrive.setPower(-1);
            rightDrive.setPower(-1);
        }
        else if (run) {
            leftDrive.setPower(1);
            rightDrive.setPower(1);
        }


        if (Math.abs(leftDrive.getCurrentPosition()) >= Math.abs(leftDrive.getTargetPosition()) && run)
        {
            leftDrive.setPower(0);
            rightDrive.setPower(0);
        }



        telemetry.addData("Current Pos L:", leftDrive.getCurrentPosition());
        telemetry.addData("Current Pos R:", rightDrive.getCurrentPosition());
        telemetry.addData(" L going to:", leftDrive.getTargetPosition());
        telemetry.addData(" R going to:", rightDrive.getTargetPosition());
        telemetry.addData(" Left of obstacle:", obstacleDirLeft);
        telemetry.addData(" Right of obstacle:", obstacleDirRight);

        telemetry.update();

    }
}
