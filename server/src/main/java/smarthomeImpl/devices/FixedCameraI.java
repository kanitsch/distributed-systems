package smarthomeImpl.devices;
import com.zeroc.Ice.Current;
import generated.SmartHome.CameraStatus;
import generated.SmartHome.FixedCamera;
import generated.SmartHome.OperationFailedException;
import generated.SmartHome.SnapshotMetadata;

public class FixedCameraI implements FixedCamera {
    @Override
    public CameraStatus getStatus(Current current) {
        return null;
    }

    @Override
    public SnapshotMetadata getSnapshot(Current current) throws OperationFailedException {
        return null;
    }

    @Override
    public void startRecording(Current current) throws OperationFailedException {

    }

    @Override
    public void stopRecording(Current current) throws OperationFailedException {

    }
}
