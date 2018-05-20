import android.app.Activity;
import android.graphics.Color;
import android.view.View;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.NormalizedColorSensor;
import com.qualcomm.robotcore.hardware.NormalizedRGBA;
import com.qualcomm.robotcore.hardware.SwitchableLight;

@TeleOp(name="Color Sensor Test V1.0", group="Autonomous")
public class ColorSensorTest extends OpMode
{
    private NormalizedColorSensor colorSensor;
    private View relativeLayout;
    private float[] hsvValues;
    private float values[];


    @Override
    public void init()
    {
        colorSensor = hardwareMap.get(NormalizedColorSensor.class, "color_sensor");

        final int relativeLayoutId = hardwareMap.appContext.getResources().getIdentifier("RelativeLayout", "id", hardwareMap.appContext.getPackageName());
        relativeLayout = ((Activity) hardwareMap.appContext).findViewById(relativeLayoutId);

        // values is a reference to the hsvValues array.
        hsvValues = new float[3];
        values = hsvValues;

        //activate color sensor light
        if (colorSensor instanceof SwitchableLight)
        {
            ((SwitchableLight)colorSensor).enableLight(true);
        }
    }

    @Override
    public void loop()
    {
        // If the button is (now) down, then toggle the light
        try
        {
            if (gamepad1.x)
            {
                if (colorSensor instanceof SwitchableLight)
                {
                    SwitchableLight light = (SwitchableLight) colorSensor;
                    light.enableLight(!light.isLightOn());
                }
            }
        }
        catch (Exception ignored) { }

        // Read the sensor
        NormalizedRGBA colors = colorSensor.getNormalizedColors();

        if (colors.red > colors.blue && colors.red > colors.green)
        {
            telemetry.addData("Color", "Red");
        }
        else if (colors.blue > colors.red && colors.blue > colors.green)
        {
            telemetry.addData("Color", "Blue");
        }
        else
        {
            telemetry.addData("Color", "White");
        }

        Color.colorToHSV(colors.toColor(), hsvValues);
        telemetry.addLine()
                .addData("H", "%.3f", hsvValues[0])
                .addData("S", "%.3f", hsvValues[1])
                .addData("V", "%.3f", hsvValues[2]);
        telemetry.addLine()
                .addData("a", "%.3f", colors.alpha)
                .addData("r", "%.3f", colors.red)
                .addData("g", "%.3f", colors.green)
                .addData("b", "%.3f", colors.blue);

        //converts colors to the equivalent Android color int
        int color = colors.toColor();
        telemetry.addLine("raw Android color: ")
                .addData("a", "%02x", Color.alpha(color))
                .addData("r", "%02x", Color.red(color))
                .addData("g", "%02x", Color.green(color))
                .addData("b", "%02x", Color.blue(color));

        //scale the colors based on the maximum relative measured
        float max = Math.max(Math.max(Math.max(colors.red, colors.green), colors.blue), colors.alpha);
        colors.red   /= max;
        colors.green /= max;
        colors.blue  /= max;
        color = colors.toColor();

        telemetry.addLine("normalized color:  ")
                .addData("a", "%02x", Color.alpha(color))
                .addData("r", "%02x", Color.red(color))
                .addData("g", "%02x", Color.green(color))
                .addData("b", "%02x", Color.blue(color));
        telemetry.update();

        // convert the RGB values to HSV values.
        Color.RGBToHSV(Color.red(color), Color.green(color), Color.blue(color), hsvValues);

        //change the background color to match detected color
        relativeLayout.post(new Runnable() {public void run() {relativeLayout.setBackgroundColor(Color.HSVToColor(0xff, values));}});
    }
}
