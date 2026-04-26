package smarthomeImpl.devices;

import com.zeroc.Ice.Current;
import generated.SmartHome.*;

public class DimmableLightI implements DimmableLight {
    private boolean isOn = false;
    private int brightness=80;
    private Schedule schedule=new Schedule();
    @Override
    public LightStatus getStatus(Current current) {
        return new LightStatus(this.isOn,this.brightness);
    }

    @Override
    public void turnOn(Current current) {
        this.isOn = true;
    }

    @Override
    public void turnOff(Current current) {
        this.isOn = false;

    }

    @Override
    public void setBrightness(int level, Current current) throws InvalidValueException {
        if (level<0 || level >100){
            throw new InvalidValueException("brightness","Brightness should be between 0 and 100");
        }
        this.brightness = level;

    }

    @Override
    public void setSchedule(TimeOfDay turnOnHour, TimeOfDay turnOffHour, Current current) throws InvalidValueException {
        if (turnOnHour.hour<0 || turnOnHour.hour>24 || turnOffHour.hour<0 || turnOffHour.hour>24){
            throw new InvalidValueException("hour","hour must be between 0 and 23");
        }
        if (turnOnHour.minute<0 || turnOnHour.minute>60 || turnOffHour.minute<0 || turnOffHour.minute>60){
            throw new InvalidValueException("minute","minute must be between 0 and 59");
        }
        this.schedule.turnOn = turnOnHour;
        this.schedule.turnOff = turnOffHour;
    }


    @Override
    public void reset(Current current) {
        this.isOn = false;
        this.brightness=80;

    }
}
