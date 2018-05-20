import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

public class EncoderCalibration extends OpMode
{
    private final double LEFT_RATE = 1.0;
    private final double RIGHT_RATE = 1.0;
    private final double TARGET = 1.0;

    private DcMotor leftDrive;
    private DcMotor rightDrive;

    @Override
    public void init()
    {
        leftDrive = hardwareMap.get(DcMotor.class, "leftDrive");
        rightDrive = hardwareMap.get(DcMotor.class, "rightDrive");

        leftDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rightDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        leftDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rightDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        leftDrive.setDirection(DcMotorSimple.Direction.FORWARD);
        rightDrive.setDirection(DcMotorSimple.Direction.FORWARD);
    }

    @Override
    public void start()
    {
        leftDrive.setTargetPosition((int) (TARGET * LEFT_RATE));
        rightDrive.setTargetPosition((int) (TARGET * RIGHT_RATE));
    }

    @Override
    public void loop()
    {
        leftDrive.setPower(100);
        rightDrive.setPower(100);

        if (leftDrive.getCurrentPosition() > leftDrive.getTargetPosition())
        {
            leftDrive.setPower(0.0);
        }

        if (rightDrive.getCurrentPosition() > rightDrive.getTargetPosition())
        {
            rightDrive.setPower(0.0);
        }
    }
}
