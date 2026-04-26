#!/usr/bin/env python3
import sys
import Ice
import SmartHome

SERVERS = {
    "server1": {"host": "localhost", "port": 10000, "name": "Ground Floor"},
    "server2": {"host": "localhost", "port": 10001, "name": "First Floor"},
}


def get_all_devices_from_servers(communicator):
    all_devices = {}
    for server_key, srv in SERVERS.items():
        endpoint = f"registry:default -h {srv['host']} -p {srv['port']}"
        base = communicator.stringToProxy(endpoint)
        registry = SmartHome.DeviceRegistryPrx.checkedCast(base)        
        if registry:
            devices = registry.listDevices()
            for d in devices:
                all_devices[server_key + "$" + d.identity] = d
    return all_devices

def make_proxy(server_key, communicator, identity, device_type):    
    srv = SERVERS[server_key]
    endpoint = f"{identity}:default -h {srv['host']} -p {srv['port']}"
    base = communicator.stringToProxy(endpoint)
    if device_type == SmartHome.DeviceType.Thermostat:
        proxy = SmartHome.ThermostatPrx.checkedCast(base)
    elif device_type == SmartHome.DeviceType.DimmableLight:
        proxy = SmartHome.DimmableLightPrx.checkedCast(base)
    elif device_type == SmartHome.DeviceType.RGBLight:
        proxy = SmartHome.RGBLightPrx.checkedCast(base)
    elif device_type == SmartHome.DeviceType.FixedCamera:
        proxy = SmartHome.FixedCameraPrx.checkedCast(base)
    elif device_type == SmartHome.DeviceType.PTZCamera:
        proxy = SmartHome.PTZCameraPrx.checkedCast(base)
    else:
        print(f"Unrecognized device type: {device_type}")
        return None
    
    return proxy


def list_all_devices(all_devices):
    print("AVAILABLE DEVICES")
    for i, (name, d) in enumerate(all_devices.items(), 1):
        print(f"{i}. {name.replace('$', ' ')} ({d.type})")
    print()


def thermostat_menu(server_key, communicator, identity):
    proxy = make_proxy(server_key, communicator, identity, SmartHome.DeviceType.Thermostat)
    if not proxy:
        return
    
    while True:
        print(f"\nThermostat: {identity}")
        print("1. Show status")
        print("2. Set target temperature")
        print("3. Show history")
        print("4. Reset")
        print("0. Back\n")
        
        choice = input("Choice: ").strip()
        
        if choice == "1":
            status = proxy.getStatus()
            print(f"Current temperature: {status.reading.currentTemperature:.1f}°C")
            print(f"Target temperature: {status.targetTemperature:.1f}°C")
            print(f"Mode: {status.mode}")
        
        elif choice == "2":
            try:
                temp = float(input("Set temperature [°C]: "))
                proxy.setTargetTemperature(temp)
                print("Temperature set")
            except SmartHome.InvalidValueException as e:
                print(e.reason)
        
        elif choice == "3":
            try:
                n = int(input("Number of recent readings"))
                history = proxy.getHistory(n)
                for r in history:
                    from datetime import datetime
                    ts = datetime.fromtimestamp(r.timestamp / 1000).strftime("%H:%M:%S")
                    print(f"    [{ts}] {r.currentTemperature:.1f}°C")
            except SmartHome.InvalidValueException as e:
                print(e.reason)
        
        elif choice == "4":
            proxy.reset()
            print("Reset executed")
        
        elif choice == "0":
            break


def dimmable_light_menu(server_key, communicator, identity):
    proxy = make_proxy(server_key, communicator, identity, SmartHome.DeviceType.DimmableLight)
    if not proxy:
        return
    
    while True:
        print(f"\nLight (Dimmable): {identity}")
        print("1. Show status")
        print("2. Turn on")
        print("3. Turn off")
        print("4. Set brightness (0-100)")
        print("5. Set schedule")
        print("6. Reset")
        print("0. Back\n")
        
        choice = input("Choice: ").strip()
        
        if choice == "1":
            status = proxy.getStatus()
            print(f"Status: {'ON' if status.isOn else 'OFF'}")
            print(f"Brightness: {status.brightness}%")
        
        elif choice == "2":
            proxy.turnOn()
            print("Turned on")
        
        elif choice == "3":
            proxy.turnOff()
            print("Turned off")
        
        elif choice == "4":
            try:
                level = int(input("Brightness (0-100): "))
                proxy.setBrightness(level)
                print("Brightness set")
            except SmartHome.InvalidValueException as e:
                print(e.reason)
        
        elif choice == "5":
            try:
                on_h = int(input("Turn on hour (0-23): "))
                on_m = int(input("Turn on minute (0-59): "))
                off_h = int(input("Turn off hour (0-23): "))
                off_m = int(input("Turn off minute (0-59): "))
                
                turn_on = SmartHome.TimeOfDay(on_h, on_m)
                turn_off = SmartHome.TimeOfDay(off_h, off_m)
                proxy.setSchedule(turn_on, turn_off)
                print("Schedule set")
            except SmartHome.InvalidValueException as e:
                print(e.reason)
        
        elif choice == "6":
            proxy.reset()
            print("Reset executed")
        
        elif choice == "0":
            break



def rgb_light_menu(server_key, communicator, identity):
    proxy = make_proxy(server_key, communicator, identity, SmartHome.DeviceType.RGBLight)
    if not proxy:
        return
    
    while True:
        print(f"\nLight (RGB): {identity}")
        print("1. Show status")
        print("2. Turn on")
        print("3. Turn off")
        print("4. Set brightness (0-100)")
        print("5. Set schedule")
        print("6. Show RGB status")
        print("7. Set color")
        print("8. Reset")
        print("0. Back\n")
        
        choice = input("Choice: ").strip()

        if choice == "1":
            status = proxy.getStatus()
            print(f"Status: {'ON' if status.isOn else 'OFF'}")
            print(f"Brightness: {status.brightness}%")
        
        elif choice == "2":
            proxy.turnOn()
            print("Turned on")
        
        elif choice == "3":
            proxy.turnOff()
            print("Turned off")
        
        elif choice == "4":
            try:
                level = int(input("Brightness (0-100): "))
                proxy.setBrightness(level)
                print("Brightness set")
            except SmartHome.InvalidValueException as e:
                print(e.reason)
        
        elif choice == "5":
            try:
                on_h = int(input("Turn on hour (0-23): "))
                on_m = int(input("Turn on minute (0-59): "))
                off_h = int(input("Turn off hour (0-23): "))
                off_m = int(input("Turn off minute (0-59): "))
                
                turn_on = SmartHome.TimeOfDay(on_h, on_m)
                turn_off = SmartHome.TimeOfDay(off_h, off_m)
                proxy.setSchedule(turn_on, turn_off)
                print("Schedule set")
            except SmartHome.InvalidValueException as e:
                print(e.reason)
        
        elif choice == "6":
            rgb_status = proxy.getRGBStatus()
            print(f"Status: {'ON' if rgb_status.base.isOn else 'OFF'}")
            print(f"Brightness: {rgb_status.base.brightness}%")
            print(f"Color RGB: ({rgb_status.color.r}, {rgb_status.color.g}, {rgb_status.color.b})")
        
        elif choice == "7":
            try:
                r = int(input("R (0-255): "))
                g = int(input("G (0-255): "))
                b = int(input("B (0-255): "))
                color = SmartHome.ColorRGB(r, g, b)
                proxy.setColor(color)
                print("Color set")
            except SmartHome.InvalidValueException as e:
                print(e.reason)
        
        elif choice == "8":
            proxy.reset()
            print("Reset executed")
        
        elif choice == "0":
            break



def fixed_camera_menu(server_key, communicator, identity):
    proxy = make_proxy(server_key, communicator, identity, SmartHome.DeviceType.FixedCamera)
    if not proxy:
        return
    
    while True:
        print(f"\nCamera (Fixed): {identity}")
        print("1. Show status")
        print("2. Start recording")
        print("3. Stop recording")
        print("0. Back\n")
        
        choice = input("Choice: ").strip()
        
        if choice == "1":
            status = proxy.getStatus()
            print(f"Recording: {'YES' if status.isRecording else 'NO'}")
            print(f"Recording duration: {status.recordingDurationMs / 1000:.1f}s")
            print(f"Files recorded: {status.recordedFilesCount}")
        
        elif choice == "2":
            try:
                proxy.startRecording()
                print("Recording started")
            except SmartHome.OperationFailedException as e:
                print(f"Error: {e.reason}")
        
        elif choice == "3":
            try:
                proxy.stopRecording()
                print("Recording stopped")
            except SmartHome.OperationFailedException as e:
                print(f"Error: {e.reason}")
        
        elif choice == "0":
            break


def ptz_camera_menu(server_key, communicator, identity):
    proxy = make_proxy(server_key, communicator, identity, SmartHome.DeviceType.PTZCamera)
    if not proxy:
        return
    
    while True:
        print(f"\nCamera (PTZ): {identity}")
        print("1. Show status")
        print("2. Show position")
        print("3. Move camera")
        print("4. Save preset")
        print("5. Go to preset")
        print("6. Show presets")
        print("7. Start recording")
        print("8. Stop recording")
        print("0. Back\n")
        
        choice = input("Choice: ").strip()
        
        if choice == "1":
            status = proxy.getStatus()
            print(f"Recording: {'YES' if status.isRecording else 'NO'}")
            print(f"Recording duration: {status.recordingDurationMs / 1000:.1f}s")
            print(f"Files recorded: {status.recordedFilesCount}")
        
        elif choice == "2":
            pos = proxy.getPosition()
            print(f"Pan: {pos.pan:.1f}°  Tilt: {pos.tilt:.1f}°  Zoom: {pos.zoom}x")
        
        elif choice == "3":
            try:
                pan = float(input("Pan (-180 to 180): "))
                tilt = float(input("Tilt (-90 to 90): "))
                zoom = int(input("Zoom (1-10): "))
                position = SmartHome.PTZPosition(pan, tilt, zoom)
                proxy.moveTo(position)
                print("Camera moved")
            except SmartHome.InvalidValueException as e:
                print(f"Error: {e.reason}")
        
        elif choice == "4":
            name = input("Preset name: ").strip()
            pos = proxy.getPosition()
            preset = SmartHome.PTZPreset(name, pos)
            proxy.savePreset(preset)
            print("Preset saved")
        
        elif choice == "5":
            try:
                name = input("Preset name: ").strip()
                proxy.goToPreset(name)
                print("Preset applied")
            except SmartHome.UnknownPresetException as e:
                print(f"Preset '{e.presetName}' not found")
        
        elif choice == "6":
            presets = proxy.getPresets()
            print("Presets:")
            for p in presets:
                print(f"  {p.name}: pan={p.position.pan:.1f}°, tilt={p.position.tilt:.1f}°, zoom={p.position.zoom}x")
        
        elif choice == "7":
            try:
                proxy.startRecording()
                print("Recording started")
            except SmartHome.OperationFailedException as e:
                print(f"Error: {e.reason}")
        
        elif choice == "8":
            try:
                proxy.stopRecording()
                print("Recording stopped")
            except SmartHome.OperationFailedException as e:
                print(f"Error: {e.reason}")
        
        elif choice == "0":
            break


def device_menu(server_key, communicator, identity, device_type):
    if device_type == SmartHome.DeviceType.Thermostat:
        thermostat_menu(server_key, communicator, identity)
    elif device_type == SmartHome.DeviceType.DimmableLight:
        dimmable_light_menu(server_key, communicator, identity)
    elif device_type == SmartHome.DeviceType.RGBLight:
        rgb_light_menu(server_key, communicator, identity)
    elif device_type == SmartHome.DeviceType.FixedCamera:
        fixed_camera_menu(server_key, communicator, identity)
    elif device_type == SmartHome.DeviceType.PTZCamera:
        ptz_camera_menu(server_key, communicator, identity)


def main():
    with Ice.initialize(sys.argv) as communicator:
        print("SmartHome Client")
        all_devices = get_all_devices_from_servers(communicator)
        all_devices_list = list(all_devices.items())
        
        while True:
            print("\nMAIN MENU")
            print("1. Select device")
            print("0. Exit")
            
            choice = input("Choice: ").strip()
            
            if choice == "1":                
                print("\nSelect device:")
                list_all_devices(all_devices)
                
                user_input = input("Enter server and identity (server$identity): ").strip()

                try:
                    idx = int(user_input) - 1
                    if 0 <= idx < len(all_devices_list):
                        name,d = all_devices_list[idx]
                        server_key = name.split("$")[0]
                        device_menu(server_key, communicator, d.identity, d.type)
                    else:
                        print("Invalid selection")
                except ValueError:
                    if user_input in all_devices.keys():
                        d = all_devices[user_input]
                        server_key = user_input.split("$")[0]
                        device_menu(server_key, communicator, d.identity, d.type)
                    else:
                        print("Device not found")

            
            elif choice == "0":
                print("Exitting...")
                break


if __name__ == "__main__":
    main()