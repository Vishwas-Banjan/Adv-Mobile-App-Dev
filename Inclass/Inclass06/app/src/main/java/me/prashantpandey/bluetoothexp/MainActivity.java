package me.prashantpandey.bluetoothexp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    final String TAG = this.getClass().getSimpleName();
    Button scanForBluetoothDevicesBtn;
    final String[] bluetoothPersmissions = {Manifest.permission.BLUETOOTH, Manifest.permission.BLUETOOTH_ADMIN, Manifest.permission.ACCESS_FINE_LOCATION};
    final int bluetoothAccessCode = 5;
    BluetoothAdapter bluetoothAdapter;
    Handler handler = new Handler();
    boolean scanningRN = true;
    final long SCAN_PERIOD = 5000;
    RecyclerView availableDevicesRV;


    // TODO set up recycler view
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        scanForBluetoothDevicesBtn = findViewById(R.id.scanForBluetoothDevices);
        availableDevicesRV = findViewById(R.id.availableDevicesRV);
        scanForBluetoothDevicesBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.scanForBluetoothDevices:{
                // checking for the hardware availability
                if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)){
                    Toast.makeText(this, "Your device is incompatible for the BLE transactions", Toast.LENGTH_LONG).show();
                    break;
                }
                // check for permission
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(bluetoothPersmissions[0])== PackageManager.PERMISSION_GRANTED
                        || checkSelfPermission(bluetoothPersmissions[1])== PackageManager.PERMISSION_GRANTED
                        || checkSelfPermission(bluetoothPersmissions[2]) == PackageManager.PERMISSION_GRANTED){
                            // then scan for the devices
                        scanForBluetoothDevices();
                    }else{
                        requestPermissions(bluetoothPersmissions, bluetoothAccessCode);
                    }
                }else{
                    // scan for the devices
                    scanForBluetoothDevices();
                }
                break;
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode==bluetoothAccessCode){
            // if permissions granted then scan for devices
            for (int grantResult=0; grantResult<grantResults.length; grantResult++){
                if (grantResults[grantResult]!=PackageManager.PERMISSION_GRANTED){
                    // ask again for the specific permission
                    requestPermissions(new String[]{permissions[grantResult]}, bluetoothAccessCode);
                }
            }
            // means permissions granted then scan for the devices
            scanForBluetoothDevices();
        }
    }

    private void scanForBluetoothDevices(){
        // getting bluetooth manager
        final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        assert bluetoothManager != null;
        bluetoothAdapter = bluetoothManager.getAdapter();
        if (bluetoothAdapter==null|| !bluetoothAdapter.isEnabled()){
            Intent i = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(i, bluetoothAccessCode);
        }else{
            // working with new bluetooth api api 21 above
            final BluetoothLeScanner bluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();
            if (checkForBluetoothEnabled()){
                // setting scanCallback
                final ScanCallback bluetoothLeScannerStop = new ScanCallback() {
                    @Override
                    public void onScanResult(int callbackType, ScanResult result) {
                        super.onScanResult(callbackType, result);
                        Log.d(TAG, "onScanResult: result: "+result.toString());
                    }
                };
                // scan for devices
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // start scanning for the devices
                        scanningRN = false;
                        // bluetoothLeScannerStop
                        bluetoothLeScanner.stopScan(bluetoothLeScannerStop);
                    }
                }, SCAN_PERIOD);
                scanningRN = true;
                bluetoothLeScanner.startScan(bluetoothLeScannerStop);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: requestCode: "+requestCode+"             resultCode: "+resultCode);
        if (requestCode==bluetoothAccessCode){
            if (requestCode==RESULT_OK){
                // scan for devices
                scanForBluetoothDevices();
            }else{
                checkForBluetoothEnabled();
            }
        }
    }

    private boolean checkForBluetoothEnabled(){
        if (bluetoothAdapter==null|| !bluetoothAdapter.isEnabled()){
            Intent i = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(i, bluetoothAccessCode);
            return false;
        }else{
            return true;
        }
    }
}
