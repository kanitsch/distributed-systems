package smarthomeImpl;

import com.zeroc.Ice.Communicator;
import com.zeroc.Ice.ObjectAdapter;
import com.zeroc.Ice.Util;
import generated.SmartHome.DeviceEntry;
import generated.SmartHome.DeviceType;
import smarthomeImpl.devices.*;

import java.lang.Exception;

import static com.zeroc.Ice.Util.stringToIdentity;

public class Server1 {
    public void t1(String[] args) {
        int status = 0;
        Communicator communicator = null;

        try {
            // 1. Inicjalizacja ICE - utworzenie communicatora
            communicator = Util.initialize(args);
            ObjectAdapter adapter = communicator.createObjectAdapterWithEndpoints("Adapter1", "tcp -h 0.0.0.0 -p 10000 -z : udp -h 0.0.0.0 -p 10000 -z");


            DeviceRegistryI registry = new DeviceRegistryI();

            ThermostatI thermostat1 = new ThermostatI();
            ThermostatI thermostat2 = new ThermostatI();
            adapter.add(thermostat1, stringToIdentity("thermostat/kitchen"));
            adapter.add(thermostat2, stringToIdentity("thermostat/living_room"));
            registry.register(new DeviceEntry("thermostat/kitchen", DeviceType.Thermostat, "kitchen"));
            registry.register(new DeviceEntry("thermostat/living_room", DeviceType.Thermostat, "living_room"));

            DimmableLightI light1 = new DimmableLightI();
            RGBLightI light2 = new RGBLightI();
            adapter.add(light1, stringToIdentity("light/kitchen"));
            adapter.add(light2, stringToIdentity("light/living_room"));
            registry.register(new DeviceEntry("light/kitchen", DeviceType.DimmableLight, "kitchen"));
            registry.register(new DeviceEntry("light/living_room", DeviceType.RGBLight, "living-room"));

            FixedCameraI camera1 = new FixedCameraI();
            PTZCameraI camera2 = new PTZCameraI();
            adapter.add(camera1, stringToIdentity("camera/hall"));
            adapter.add(camera2, stringToIdentity("camera/living_room"));
            registry.register(new DeviceEntry("camera/hall", DeviceType.FixedCamera, "hall"));
            registry.register(new DeviceEntry("camera/living_room", DeviceType.PTZCamera, "living_room"));

            // 5. Aktywacja adaptera i wejście w pętlę przetwarzania żądań
            adapter.add(registry, stringToIdentity("registry"));
            adapter.activate();

            System.out.println("Server1 - ground floor: Entering event processing loop...");

            communicator.waitForShutdown();

        } catch (Exception e) {
            e.printStackTrace(System.err);
            status = 1;
        }
        if (communicator != null) {
            try {
                communicator.destroy();
            } catch (Exception e) {
                e.printStackTrace(System.err);
                status = 1;
            }
        }
        System.exit(status);
    }


    public static void main(String[] args) {
        Server1 app = new Server1();
        app.t1(args);
    }

}
