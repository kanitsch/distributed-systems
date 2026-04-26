package smarthomeImpl.devices;

import com.zeroc.Ice.Current;
import generated.SmartHome.InvalidValueException;
import generated.SmartHome.Thermostat;
import generated.SmartHome.ThermostatReading;
import generated.SmartHome.ThermostatStatus;
import generated.SmartHome.ThermostatMode;

import java.util.*;

public class ThermostatI implements Thermostat {
    private static final int HISTORY_CAPACITY = 100;
    private static final double MIN_TEMP = 5.0;
    private static final double MAX_TEMP = 30.0;
    private double currentTemperature=20;
    private double targetTemperature=20;
    private ThermostatMode mode=ThermostatMode.OFF;
    private Deque<ThermostatReading> history=new ArrayDeque<>();


    @Override
    public ThermostatStatus getStatus(Current current) {
        ThermostatReading reading = new ThermostatReading(this.currentTemperature, System.currentTimeMillis());
        recordReading();
        return new ThermostatStatus(reading, this.targetTemperature, this.mode);
    }

    @Override
    public void setTargetTemperature(double temperature, Current current) throws InvalidValueException {
        if (temperature < MIN_TEMP || temperature > MAX_TEMP) {
            throw new InvalidValueException("targetTemperature", "temperature must be set between 5 and 30 degrees");
        }
        this.targetTemperature = temperature;
        if (this.currentTemperature > targetTemperature) {
            this.mode=ThermostatMode.COOLING;
        }
        else if (this.currentTemperature < targetTemperature) {
            this.mode=ThermostatMode.HEATING;
        }
        else {
            this.mode=ThermostatMode.OFF;
        }
    }

    @Override
    public ThermostatReading[] getHistory(int lastN, Current current) throws InvalidValueException {
        if (lastN > HISTORY_CAPACITY) {
            throw new InvalidValueException("lastN", "lastN cannot be greater than HISTORY_CAPACITY");
        }
        List<ThermostatReading> list = new ArrayList<>(history);
        int from = Math.max(0, list.size() - lastN);
        return list.subList(from, list.size()).toArray(new ThermostatReading[0]);
    }

    @Override
    public void reset(Current current) {
        this.targetTemperature=20;
        this.mode=ThermostatMode.OFF;
        this.history.clear();
    }

    private void recordReading() {
        if (history.size() >= HISTORY_CAPACITY) {
            history.pollFirst();
        }
        history.addLast(new ThermostatReading(currentTemperature, System.currentTimeMillis()));
    }
}
