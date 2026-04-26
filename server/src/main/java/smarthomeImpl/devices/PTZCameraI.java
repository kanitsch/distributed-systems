package smarthomeImpl.devices;

import com.zeroc.Ice.Current;
import generated.SmartHome.*;

public class PTZCameraI extends FixedCameraI implements PTZCamera {
    @Override
    public PTZPosition getPosition(Current current) {
        return null;
    }

    @Override
    public void moveTo(PTZPosition position, Current current) throws InvalidValueException {

    }

    @Override
    public void savePreset(PTZPreset preset, Current current) {

    }

    @Override
    public void goToPreset(String name, Current current) throws UnknownPresetException {

    }

    @Override
    public PTZPreset[] getPresets(Current current) {
        return new PTZPreset[0];
    }


}
