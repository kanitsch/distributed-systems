package smarthomeImpl.devices;
import generated.SmartHome.CameraStatus;
import generated.SmartHome.OperationFailedException;
import com.zeroc.Ice.Current;
import generated.SmartHome.FixedCamera;

public class FixedCameraI implements FixedCamera {
    private boolean isRecording = false;
    private long recordingStartTimestamp = 0;
    private int recordsCount = 0;


    @Override
    public CameraStatus getStatus(Current current) {
        long duration;
        if (this.isRecording) {
            duration = System.currentTimeMillis() - recordingStartTimestamp;
        }
        else {
            duration = 0;
        }
        return new CameraStatus(this.isRecording, this.recordsCount, duration);
    }


    @Override
    public void startRecording(Current current) throws OperationFailedException {
        if (this.isRecording) {
            throw new OperationFailedException(current.id.name, "Camera is already recording");
        }
        else {
            this.recordingStartTimestamp = System.currentTimeMillis();
            isRecording = true;
        }

    }

    @Override
    public void stopRecording(Current current) throws OperationFailedException {
        if (this.isRecording) {
            this.recordingStartTimestamp=0;
            this.isRecording = false;
            this.recordsCount++;
        }
        else {
            throw new OperationFailedException(current.id.name, "Camera is not recording");
        }

    }
}
