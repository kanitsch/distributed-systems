package smarthomeImpl.devices;

import com.zeroc.Ice.Current;
import generated.SmartHome.DeviceEntry;
import generated.SmartHome.DeviceRegistry;
import generated.SmartHome.DeviceType;

import java.util.ArrayList;
import java.util.List;

public class DeviceRegistryI implements DeviceRegistry {
    private final List<DeviceEntry> entries = new ArrayList<>();

    public void register(DeviceEntry entry) {
        entries.add(entry);
    }
    @Override
    public DeviceEntry[] listDevices(Current current) {
        return entries.toArray(new DeviceEntry[0]);
    }

    @Override
    public DeviceEntry[] listDevicesByType(DeviceType type, Current current) {
        return entries.stream()
                .filter(e -> e.type == type).toArray(DeviceEntry[]::new);
    }
}
