package smarthomeImpl;

import com.zeroc.Ice.Communicator;
import com.zeroc.Ice.ObjectAdapter;
import com.zeroc.Ice.Util;
import generated.SmartHome.DeviceEntry;
import generated.SmartHome.DeviceType;
import smarthomeImpl.devices.*;

import java.lang.Exception;

import static com.zeroc.Ice.Util.stringToIdentity;

public class Server2 {
    public void t1(String[] args) {
        int status = 0;
        Communicator communicator = null;

        try {
            // 1. Inicjalizacja ICE - utworzenie communicatora
            communicator = Util.initialize(args);
            ObjectAdapter adapter = communicator.createObjectAdapterWithEndpoints("Adapter2", "tcp -h 0.0.0.0 -p 10001 -z : udp -h 0.0.0.0 -p 10001 -z");


            DeviceRegistryI registry = new DeviceRegistryI();

            ThermostatI thermostat1 = new ThermostatI();
            adapter.add(thermostat1, stringToIdentity("thermostat/bedroom"));
            registry.register(new DeviceEntry("thermostat/bedroom", DeviceType.Thermostat, "bedroom"));

            DimmableLightI light1 = new DimmableLightI();
            RGBLightI light2 = new RGBLightI();
            adapter.add(light1, stringToIdentity("light/office"));
            adapter.add(light2, stringToIdentity("light/bedroom"));
            registry.register(new DeviceEntry("light/office", DeviceType.DimmableLight, "office"));
            registry.register(new DeviceEntry("light/bedroom", DeviceType.RGBLight, "bedroom"));

            PTZCameraI camera1 = new PTZCameraI();
            adapter.add(camera1, stringToIdentity("camera/office"));
            registry.register(new DeviceEntry("camera/office", DeviceType.PTZCamera, "office"));

            // 5. Aktywacja adaptera i wejście w pętlę przetwarzania żądań
            adapter.add(registry, stringToIdentity("registry"));
            adapter.activate();

            System.out.println("Server2 - first floor: Entering event processing loop...");

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
        Server2 app = new Server2();
        app.t1(args);
    }

}
