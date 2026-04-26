package smarthomeImpl.devices;

import com.zeroc.Ice.Current;
import generated.SmartHome.ColorRGB;
import generated.SmartHome.InvalidValueException;
import generated.SmartHome.RGBLight;
import generated.SmartHome.RGBLightStatus;

public class RGBLightI extends DimmableLightI implements RGBLight {
    private int r=0;
    private int g=0;
    private int b=0;

    @Override
    public RGBLightStatus getRGBStatus(Current current) {
        ColorRGB color = new ColorRGB(r,g,b);
        return new RGBLightStatus(super.getStatus(current), color);
    }

    @Override
    public void setColor(ColorRGB color, Current current) throws InvalidValueException {
        if (color.r<0 || color.g<0 || color.b<0){
            throw new InvalidValueException("color", "r,g,b cannot be negative");
        }
        if (color.r>255 || color.g>255 || color.b>255){
            throw new InvalidValueException("color", "r,g,b cannot be greater than 255");
        }
        this.r=color.r;
        this.g=color.g;
        this.b=color.b;


    }
}
