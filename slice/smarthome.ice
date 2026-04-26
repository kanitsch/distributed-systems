#ifndef SMARTHOME_ICE
#define SMARTHOME_ICE

module SmartHome
{

  exception InvalidValueException {
      string field;
      string reason;
  };

  exception OperationFailedException {
      string deviceId;
      string reason;
  };

  // THERMOSTAT

  enum ThermostatMode { COOLING, HEATING, OFF };

  struct ThermostatReading {
    double currentTemperature;
    long timestamp;
  };

  struct ThermostatStatus {
    ThermostatReading reading;
    double targetTemperature;
    ThermostatMode mode;
  };

  sequence<ThermostatReading> ReadingHistory;

  interface Thermostat {
    idempotent ThermostatStatus getStatus();
    idempotent void setTargetTemperature(double temperature)
        throws InvalidValueException;
    idempotent ReadingHistory getHistory(int lastN)
	throws InvalidValueException;
    idempotent void reset();
  };


// LIGHTING

  struct LightStatus {
    bool isOn;
    int brightness;
  };

  struct TimeOfDay {
    int hour;    // 0–23
    int minute;  // 0–59
  };

  struct Schedule {
    TimeOfDay turnOn;
    TimeOfDay turnOff;
  };

  interface DimmableLight{
    idempotent LightStatus getStatus();
    idempotent void turnOn();
    idempotent void turnOff();
    idempotent void setBrightness(int level)
        throws InvalidValueException;
    idempotent void setSchedule(TimeOfDay turnOnHour, TimeOfDay turnOffHour)
        throws InvalidValueException;
    void reset();
  };

  struct ColorRGB {
    int r;   // 0–255
    int g;
    int b;
  };

  struct RGBLightStatus {
    LightStatus base;
    ColorRGB color;
  };

  interface RGBLight extends DimmableLight{
    idempotent RGBLightStatus getRGBStatus();
    idempotent void setColor(ColorRGB color)
        throws InvalidValueException;
  };

  // CAMERAS

  struct CameraStatus {
      bool       isRecording;
      int 	 recordedFilesCount;
      long       recordingDurationMs;
  };


  interface FixedCamera {
      idempotent CameraStatus getStatus();

      void startRecording()
          throws OperationFailedException;

      void stopRecording()
          throws OperationFailedException;
  };

  struct PTZPosition {
      float pan;    // -180.0 .. 180.0
      float tilt;   // -90.0  .. 90.0
      int   zoom;   // 1 .. 10
  };

  struct PTZPreset {
      string      name;
      PTZPosition position;
  };

  sequence<PTZPreset> PresetList;

  exception UnknownPresetException {
      string presetName;
      PresetList availablePresets;
  };

  interface PTZCamera extends FixedCamera {
      idempotent PTZPosition getPosition();

      idempotent void moveTo(PTZPosition position)
          throws InvalidValueException;

      void savePreset(PTZPreset preset);
      void removePreset(PTZPreset preset);

      idempotent void goToPreset(string name)
          throws UnknownPresetException;

      idempotent PresetList getPresets();
  };

  // DEVICE REGISTRY
enum DeviceType {
    Thermostat,
    DimmableLight,
    RGBLight,
    FixedCamera,
    PTZCamera
};

  struct DeviceEntry {
      string     identity;
      DeviceType type;
      string     location;
  };

  sequence<DeviceEntry> DeviceList;

  interface DeviceRegistry {
      idempotent DeviceList listDevices();
      idempotent DeviceList listDevicesByType(DeviceType type);
  };

};

#endif
