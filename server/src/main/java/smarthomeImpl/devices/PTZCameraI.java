package smarthomeImpl.devices;

import generated.SmartHome.*;
import com.zeroc.Ice.Current;

import java.util.LinkedHashMap;
import java.util.Map;

public class PTZCameraI extends FixedCameraI implements PTZCamera {
    private PTZPosition currentPosition=new PTZPosition(0,0,5);
    private final Map<String, PTZPosition> presets = new LinkedHashMap<String, PTZPosition>();
    @Override
    public PTZPosition getPosition(Current current) {
        return currentPosition;
    }

    @Override
    public void moveTo(PTZPosition position, Current current) throws InvalidValueException {
        if (position.pan<-180 || position.pan > 180){
            throw new InvalidValueException("position.pan","pan must be between -180.0 and 180.0");
        }
        if (position.tilt<-90.0 || position.tilt>90.0){
            throw new InvalidValueException("position.tilt","tilt must be between -90.0 and 90.0");
        }
        if (position.zoom<0 || position.zoom>10){
            throw new InvalidValueException("position.zoom","zoom must be between 0 and 10");
        }
        currentPosition.pan=position.pan;
        currentPosition.tilt=position.tilt;
        currentPosition.zoom=position.zoom;
    }

    @Override
    public void savePreset(PTZPreset preset, Current current) {
        presets.put(preset.name, preset.position);

    }

    @Override
    public void removePreset(PTZPreset preset, Current current) {
        presets.remove(preset.name);

    }

    @Override
    public void goToPreset(String name, Current current) throws UnknownPresetException {
        PTZPosition pos = presets.get(name);
        if (pos == null) {
            throw new UnknownPresetException(name,
                    getPresets(current));
        }
        this.currentPosition = pos;

    }

    @Override
    public PTZPreset[] getPresets(Current current) {
        return presets.entrySet().stream()
                .map(e -> new PTZPreset(e.getKey(), e.getValue()))
                .toArray(PTZPreset[]::new);
    }


}
