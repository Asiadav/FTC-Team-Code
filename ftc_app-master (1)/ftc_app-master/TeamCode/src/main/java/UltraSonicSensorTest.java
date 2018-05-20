

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.UltrasonicSensor;


import java.util.Objects;

@TeleOp(name="Ultra Sonic Sensor Test", group="Corners TeleOp")

public class UltraSonicSensorTest extends LinearOpMode {

    UltrasonicSensor ultra;  // Hardware Device Object


    @Override
    public void runOpMode() {


        ultra = hardwareMap.ultrasonicSensor.get("ultra");

        // wait for the start button to be pressed.
        waitForStart();
        double vis = 0;

        // Note we use opModeIsActive() as our loop condition because it is an interuptible method.
        while (opModeIsActive())  {

            double sens = ultra.getUltrasonicLevel();
            if (sens > 0) vis = sens;
            // send the info back to driver station using telemetry function.

            telemetry.addData("Clear", vis);
            telemetry.update();
        }
    }
}