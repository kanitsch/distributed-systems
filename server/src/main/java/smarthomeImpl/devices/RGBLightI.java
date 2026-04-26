package smarthomeImpl.devices;

import com.zeroc.Ice.Current;
import generated.SmartHome.ColorRGB;
import generated.SmartHome.InvalidValueException;
import generated.SmartHome.RGBLight;
import generated.SmartHome.RGBLightStatus;

public class RGBLightI extends DimmableLightI implements RGBLight {
    private ColorRGB color=new ColorRGB(0,0,0);

    @Override
    public RGBLightStatus getRGBStatus(Current current) {
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
        this.color=color;


    }
    @Override
    public void reset(Current current){
        super.reset(current);
        color=new ColorRGB(0,0,0);


    }
}
