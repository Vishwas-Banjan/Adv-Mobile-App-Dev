package com.vbanjan.bluetoothdemo;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.MessageQueue;
import android.os.ParcelUuid;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Stack;
import java.util.UUID;

import no.nordicsemi.android.support.v18.scanner.BluetoothLeScannerCompat;
import no.nordicsemi.android.support.v18.scanner.ScanCallback;
import no.nordicsemi.android.support.v18.scanner.ScanFilter;
import no.nordicsemi.android.support.v18.scanner.ScanResult;
import no.nordicsemi.android.support.v18.scanner.ScanSettings;

import static android.bluetooth.BluetoothDevice.BOND_BONDED;
import static android.bluetooth.BluetoothDevice.BOND_BONDING;
import static android.bluetooth.BluetoothDevice.BOND_NONE;
import static android.bluetooth.BluetoothGatt.GATT_SUCCESS;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    TextView textView;
    TextView tempTextView;
    int disconnectCount = 0;
    private static final String DEVICE_ADDRESS = "A8:3E:0E:B4:70:23";
    //        UUID BULB_SERVICE_UUID = UUID.fromString("df178e19-c76d-06fa-8cd7-22c7728c0d6a"); //Nokia
//    UUID BULB_SERVICE_UUID = UUID.fromString("df3ba82c-96c6-ca1b-6667-15a1387df982"); // OP 5
//    UUID BULB_SERVICE_UUID = UUID.fromString("df88904f-f92f-5543-3a01-f475e8b59ca9"); // sukalp
//    UUID BULB_SERVICE_UUID = UUID.fromString("dffb0d6d-2abc-8234-c92e-fec37fd2fa90"); // chester
    UUID BULB_SERVICE_UUID = UUID.fromString("df8186aa-69dc-b3fa-769e-b854b276b922"); // dev
    //    UUID BULB_SERVICE_UUID = UUID.fromString("df12e166-2f80-b799-40dc-6ed8a52ede1f"); //MOTO
    UUID BULB_SWITCH_CHAR_UUID = UUID.fromString("FB959362-F26E-43A9-927C-7E17D8FB2D8D");
    UUID BULB_TEMP_CHAR_UUID = UUID.fromString("0CED9345-B31F-457D-A6A2-B3DB9B03E39A");
    UUID BULB_TEMP_DESCRIPTOR_UUID;
    UUID BULB_BEEP_CHAR_UUID = UUID.fromString("EC958823-F26E-43A9-927C-7E17D8F32A90");
    String TAG = "demo";
    BluetoothAdapter bluetoothAdapter;
    BluetoothManager bluetoothManager;


    Button bulbON, bulbOFF, beepON, connect;

    BluetoothLeScannerCompat scanner = BluetoothLeScannerCompat.getScanner();

    ScanSettings settings = new ScanSettings.Builder()
            .setScanMode(ScanSettings.SCAN_MODE_BALANCED)
//            .setCallbackType(ScanSettings.CALLBACK_TYPE_ALL_MATCHES)
//            .setMatchMode(ScanSettings.MATCH_MODE_AGGRESSIVE)
//            .setNumOfMatches(ScanSettings.MATCH_NUM_ONE_ADVERTISEMENT)
            .setReportDelay(0L)
            .build();

    ScanFilter scanFilter = new ScanFilter.Builder()
            .setServiceUuid(new ParcelUuid(BULB_SERVICE_UUID)).build();

    Handler bleHandler;
    Queue<Runnable> bleReqQueue;
    boolean bleQueueRunning = false;
    int noOfTries = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bleHandler = new Handler();
        enableBluetooth();
        textView = findViewById(R.id.TestTextView);
        tempTextView = findViewById(R.id.tempTextView);
        bulbON = findViewById(R.id.bulbON);
        bulbOFF = findViewById(R.id.bulbOFF);
        beepON = findViewById(R.id.BeepON);
        connect = findViewById(R.id.connect);
        bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();

        bleReqQueue=new LinkedList<>();

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            bleRquestQueue = bleHandler.getLooper().getQueue();
//        }else{
//            bleRquestQueue = Looper.myQueue();
//        }

        //Arrays.asList(scanFilter), settings,
        scanner.startScan(Arrays.asList(scanFilter), settings, mScanCallback);
//        scanner.startScan(testScanCallback);
        bulbON.setOnClickListener(this);
        bulbOFF.setOnClickListener(this);
        beepON.setOnClickListener(this);
    }



    public void enableBluetooth() {
        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivity(enableBtIntent);
        }
    }

    Runnable discoverServicesRunnable;
    BluetoothGatt mGatt, workingGatt;
    BluetoothDevice myDevice;
    BluetoothGattCharacteristic switch_characteristic, beep_characteristic;
    // read write characterstics runnables
    Runnable readSwitch= new Runnable() {
        @Override
        public void run() {
            mGatt.readCharacteristic(switch_characteristic);
        }
    };

    Runnable writeSwitch= new Runnable() {
        @Override
        public void run() {
            mGatt.writeCharacteristic(switch_characteristic);
        }
    };
    Runnable readBeep = new Runnable() {
        @Override
        public void run() {
            mGatt.readCharacteristic(beep_characteristic);
        }
    };
    Runnable writeBeep = new Runnable() {
        @Override
        public void run() {
            mGatt.writeCharacteristic(beep_characteristic);
        }
    };

    BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        public void onConnectionStateChange(final BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);
            Log.d(TAG, "onConnectionStateChange: newstate: "+newState+"   status: "+status);
            if (status == GATT_SUCCESS) {
                if (newState == BluetoothProfile.STATE_CONNECTED) {
                    int bondstate = myDevice.getBondState();
                    // Take action depending on the bond state
                    if (bondstate == BOND_NONE || bondstate == BOND_BONDED) {
                        Log.d(TAG, "onConnectionStateChange: CONNECTED");
                        // Connected to device, now proceed to discover it's services but delay a bit if needed
                        int delayWhenBonded = 0;
                        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.N) {
                            delayWhenBonded = 1000;
                        }

                        final int delay = bondstate == BOND_BONDED ? delayWhenBonded : 0;
                        discoverServicesRunnable = new Runnable() {
                            @Override
                            public void run() {
                                Log.d(TAG, String.format(Locale.ENGLISH, "discovering services of '%s' with delay of %d ms", myDevice.getAddress(), delay));
                                boolean result = gatt.discoverServices();
                                Log.d(TAG, "discoverServices run: "+gatt.getServices().toString());
                                if (!result) {
                                    Log.e(TAG, "discoverServices failed to start");
                                }
                                discoverServicesRunnable = null;
                            }
                        };
                        bleHandler.postDelayed(discoverServicesRunnable, delay);
                    } else if (bondstate == BOND_BONDING) {
                        // Bonding process in progress, let it complete
                        Log.i(TAG, "waiting for bonding to complete");
                    }
                }
            } else {
                // An error happened...figure out what happened!
                if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                    Log.d(TAG, "onConnectionStateChange: ");
                }
                toggleConnectBtn(true);
                gatt.close();
            }
        }

        @Override
        public void onServicesDiscovered(final BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);
            Log.d(TAG, "onServicesDiscovered: ");
            final List<BluetoothGattService> services = gatt.getServices();
            Log.i(TAG, String.format(Locale.ENGLISH, "discovered %d services for '%s'", services.size(), myDevice.getAddress()));

            // Get the TEMP characteristic
            final BluetoothGattCharacteristic temp_characteristic = gatt
                    .getService(BULB_SERVICE_UUID)
                    .getCharacteristic(BULB_TEMP_CHAR_UUID);
            BULB_TEMP_DESCRIPTOR_UUID = UUID.fromString(String.valueOf(temp_characteristic.getDescriptors().get(0).getUuid()));

            switch_characteristic = gatt
                    .getService(BULB_SERVICE_UUID)
                    .getCharacteristic(BULB_SWITCH_CHAR_UUID);

            beep_characteristic = gatt
                    .getService(BULB_SERVICE_UUID)
                    .getCharacteristic(BULB_BEEP_CHAR_UUID);

            workingGatt = gatt;

            BluetoothGattDescriptor temp_descriptor =
                    temp_characteristic.getDescriptor(BULB_TEMP_DESCRIPTOR_UUID);
            temp_descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);

            // setting up notifications
//            gatt.setCharacteristicNotification(temp_characteristic, true);

//            // Enable notifications for this characteristic locally
//            gatt.setCharacteristicNotification(temp_characteristic, true);

            // adding command to the queue
//            bleReqQueue.add(readSwitch);
//            bleReqQueue.add(readBeep);
//            executeNextBleRequest();

//            BluetoothGattDescriptor temp_descriptor =
//                    temp_characteristic.getDescriptor(BULB_TEMP_DESCRIPTOR_UUID);
//            temp_descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);


//            gatt.writeDescriptor(temp_descriptor); //START NOTIFICATION

//            gatt.readCharacteristic(switch_characteristic); //READ BULB STATUS

//            gatt.readCharacteristic(beep_characteristic); //READ BEEP STATUS

            switch_characteristic.setValue(ByteBuffer.allocate(4).putInt(1).array()); //WRITE BULB OFF
            gatt.writeCharacteristic(switch_characteristic);

//            switch_characteristic.setValue(ByteBuffer.allocate(4).putInt(1).array()); //WRITE BULB ON
//            gatt.writeCharacteristic(switch_characteristic);

//            beep_characteristic.setValue("Beeping".getBytes()); //WRITE SOUND ON
//            gatt.writeCharacteristic(beep_characteristic);

//            beep_characteristic.setValue("Not Beeping".getBytes()); //WRITE SOUND OFF
//            gatt.writeCharacteristic(beep_characteristic);

        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            Log.d(TAG, "onCharacteristicWrite: "+ characteristic.getUuid());
//            writeCharacteristics(characteristic);
//            completedBleCommand();
        }

        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor,
                                      int status) {
            if (BULB_TEMP_DESCRIPTOR_UUID.equals(descriptor.getUuid())) {
                Log.d(TAG, "onDescriptorWrite: ");
                BluetoothGattCharacteristic characteristic = gatt
                        .getService(BULB_SERVICE_UUID)
                        .getCharacteristic(BULB_TEMP_CHAR_UUID);
                gatt.readCharacteristic(characteristic);
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic
                characteristic, int status) {
            Log.d(TAG, "onCharacteristicRead: "+ Arrays.toString(characteristic.getValue()));
//            readCharacteristics(characteristic);
//            completedBleCommand();
        }

        private void readCharacteristics(BluetoothGattCharacteristic
                                                 characteristic) {
            if (BULB_TEMP_CHAR_UUID.equals(characteristic.getUuid())) {
                Log.d(TAG, "readCharacteristics: Updating Temp");
                final byte[] data = characteristic.getValue();
                // data manipulation
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        updateTemp(data);
                    }
                });
            } else if (BULB_SWITCH_CHAR_UUID.equals(characteristic.getUuid())) {
                Log.d(TAG, "readCharacteristics: Read Switch Value");
                final byte[] data = characteristic.getValue();
                try {
                    Log.d(TAG, "readCharacteristics: " + new String(data, "ISO-8859-1"));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            } else if (BULB_BEEP_CHAR_UUID.equals(characteristic.getUuid())) {
                Log.d(TAG, "readCharacteristics: Read BEEP Value");
                final byte[] data = characteristic.getValue();
                try {
                    Log.d(TAG, "readCharacteristics: " + new String(data, "ISO-8859-1"));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        }

        private void writeCharacteristics(BluetoothGattCharacteristic characteristic) {
            if (BULB_SWITCH_CHAR_UUID.equals(characteristic.getUuid())) {
                Log.d(TAG, "writeCharacteristics: WRITE SWITCH VALUE "+ ByteBuffer.wrap(characteristic.getValue()).getInt());
            } else if (BULB_BEEP_CHAR_UUID.equals(characteristic.getUuid())) {
                Log.d(TAG, "writeCharacteristics: BEEP UPDATE");
            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic
                characteristic) {
//            readCharacteristics(characteristic);
//            completedBleCommand();
        }


    };

    private void toggleBulb(int bulbState){
        Log.d(TAG, "toggleBulb: BulbState:"+bulbState);
        switch_characteristic.setValue(ByteBuffer.allocate(4).putInt(bulbState).array()); //WRITE BULB OFF
        bleReqQueue.add(writeSwitch);
        // execute next command
        executeNextBleRequest();
        uiElementsEnable(true);
    }

    private void toggleBeep(String beepState){
        beep_characteristic.setValue(beepState.getBytes()); //WRITE SOUND ON
        bleReqQueue.add(writeBeep);
        executeNextBleRequest();
        uiElementsEnable(true);
    }

    private void uiElementsEnable(boolean enabled){
        // set ui elements enable or disable
        bulbON.setEnabled(enabled);
        bulbOFF.setEnabled(enabled);
        beepON.setEnabled(enabled);
    }

    private void toggleConnectBtn(boolean enable){
//        connect.setEnabled(enable);
    }

    void executeNextBleRequest(){
        if (bleQueueRunning){
            Log.d(TAG, "executeNextBleRequest: ble running");
            return;
        }

        if (mGatt==null){
            Log.d(TAG, "executeNextBleRequest: invalid gatt");
            bleReqQueue.clear();
            bleQueueRunning = false;
            return;
        }

        if (bleReqQueue.size()>0){
            Log.d(TAG, "executeNextBleRequest: executing program");
            // run the command in queue
            bleHandler.post(bleReqQueue.poll());
            bleQueueRunning = true;
            noOfTries = 0;
        }


    }

    void completedBleCommand(){
        bleQueueRunning = false;
        bleReqQueue.poll();
        executeNextBleRequest();
    }


    private final ScanCallback mScanCallback = new ScanCallback() {
        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            Log.d(TAG, "onScanResult: "+result.getDevice());
            // We scan with report delay > 0. This will never be called.
            if (result != null) {
//                ScanResult result = results.get(0);
                BluetoothDevice device = result.getDevice();
                final String deviceAddress = device.getAddress();
                textView.setText(result.toString());

                connect.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d(TAG, "scanForBluetoothDevices: stopped scanning");
                        myDevice = bluetoothAdapter.getRemoteDevice(deviceAddress);
                        mGatt = myDevice.connectGatt(getApplicationContext(), false, mGattCallback, BluetoothDevice.TRANSPORT_LE);
                        toggleConnectBtn(false);
                    }
                });


                // Device detected, we can automatically connect to it and stop the scan
                scanner.stopScan(this);
            }
        }

        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        public void onBatchScanResults(List<ScanResult> results) {

        }

        @Override
        public void onScanFailed(int errorCode) {
            Log.d(TAG, "onScanFailed: ");
            // Scan error
        }
    };

    public void updateTemp(byte[] data) {
        try {
            tempTextView.setText("Temperature: " + new String(data, "ISO-8859-1") + " F");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        uiElementsEnable(false);
        switch (v.getId()){
            case R.id.bulbON:{
                toggleBulb(1);
                Log.d(TAG, "onClick: bulb is on");
                break;
            }
            case R.id.bulbOFF:{
                toggleBulb(0);
                Log.d(TAG, "onClick: bulb is off");
                break;
            }
            case R.id.BeepON:{
                toggleBeep("Beeping");
                break;
            }
            default:{
                uiElementsEnable(true);
                break;
            }
        }
    }
}
