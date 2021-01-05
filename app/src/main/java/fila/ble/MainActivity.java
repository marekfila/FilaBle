package fila.ble;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.IBinder;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SimpleExpandableListAdapter;
import android.widget.TextView;
import android.widget.AdapterView.OnItemLongClickListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import android.widget.Toast;
import android.widget.ProgressBar;
import android.content.res.Resources;
import android.widget.Button;
import android.widget.ListView;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.view.KeyEvent;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Collections;
import java.util.Comparator;

public class MainActivity extends Activity {

    // Historie ////////////////////////////////////////////////////////////////////////////////////
    //----------------------------------------------------------------------------------------------
    public static String version = "v1b - (30.1.2020 09:39)"; // Check BUILD GRADLE !!!
    // - SETTINGS ----------------------------------------------------------------------------------
    public static boolean flagTestResolutionDisp = false;
    public boolean debugInfoPanel = false;
    public boolean timerRun = false;
    public int counterVirtual = 1;

    static final int NUMBER_OFFLINE_JUMPS = 2; // When device was offline, apk will try to connect in 2th cycle of calling units
    static final int NUMBER_TRY_CONNECT = 3; // When device is online and attempt to connect is failed, will do next 2 attempts to connect
    // ---------------------------------------------------------------------------------------------

    // Constants state of bluetooth symbol
    static final int BNONE = 0;
    static final int BCONNECTED = 1;
    static final int BDISABLE = 2;
    static final int BSEARCH = 3;

    // Constants state of units
    static final int PROC_FIRST_START = 0;
    static final int PROC_TRY_BIND = 1;
    static final int PROC_BIND_OK = 2;
    static final int PROC_CONNECTED = 3;
    static final int PROC_TRY_NOTIFY = 4;
    static final int PROC_NOTIFY_ENABLED = 5;
    static final int PROC_ONLINE_RXOK = 6;
    static final int PROC_TRY_NOTIFY_DISABLE = 7;
    static final int PROC_NOTIFY_DISABLED = 8;
    static final int PROC_CLOSE = 9;

    // Constants state of connection
    static final int CON_DISCONNECT = 0;
    static final int CON_CONNECTING = 1;
    static final int CON_CONNECTED = 2;

    // Constants of processing
    static final int SKIP_TO_MENU = 0;
    static final int TOP_USER = 1;
    static final int CLEAR_USER = 2;
    static final int SKIP_TO_SCAN = 3;
    static final int SKIP_TO_MODE_USER = 4;

    // Permission mode of user
    static final int USER = 0;
    static final int ADVANCED = 1;
    static final int ADMIN = 2;

/*

v1.12 (15.11.2019) - New process of calling units, it looks great :-)

v1.11 (1.10.2019)  - Showing alerts - problem solved

v1.10 (1.10.2019)  - MainActivity shift symbols BT and Text BLE name and Custom Name
                   - MenuUnit symbols Filter standard refresh after select

v1.09 (31.10.2019)  - BT icons functions in MainActivity
                    - alert transparent in MainActivity


v1.08 (31.10.2019)  - Add address to main empty list
                    - flow invisible in diagnostic
                    - filer layout inicialization


v1.07 (29.10.2019)  - implemented warnings
                    - logic of show unit online/offline

v1.06 (22.10.2019)  - added downloads link
                    - fix problem with disconnect in menu

v1.05 (16.10.2019)  - New graphic in menu gray frames
                    - graphic: unified colors progressbars
                    - graphic: frames fixed in menu, changes symbols and bars
                    - fce for main activity show flows


v1.04 (..2019)      - Sorting users by selecting and save to file
                    - Change custom name in menu od unit
                    - Icons in Standard and Filter dialogs
                    - Added

v1.03 (23.9.2019)   - Increase api version to 28
                    - Menu icons change
                    - User mode settings
                    - Save user mode

v1.02 (19.9.2019)   - Compiled from Api 21 to Api 26 because scan and google play
                        - permission of location for scan ble devices
                    - Add diagnostic data to menu
                    - Clickable layout instead of image button
                    - Release for google play

v1.01 (12.9.2019)   - Complete new project with origin libraries

----------------------------------------------------------------*/

    private Timer timer;
    private int timerProcess = 5;

    // Global variables
    public static String globalIntMarekValue;
    public static String globalDeviceName;
    public static int globalIndexOfActiveUser = 0;
    public static String globalCustomName;
    public static String globalSerial;
    public static String globalDefaultFilterTimer;
    public static int globalUserMode = USER; // 0 - user, 1 - advanced user, 2 - admin
    public static boolean menuWasDisconnect = false;

    //What do do you want? - Processing
    boolean wantToSkipToScan = false;
    boolean wantToSkipToMenu = false;
    boolean wantToClearUser = false;
    boolean wantToTopUser = false;
    boolean wantToModeUser = false;
    int wantedUser = 0;

    boolean flagStillConnect = false;
    boolean flagAddIconMenuVisible = true;
    boolean flagProcessing = false;
    int counterOffline = 0;

    private final static String TAG = MainActivity.class.getSimpleName();

    //public static final String EXTRAS_DEVICE_NAME = "DEVICE_NAME";
    //public static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";

    protected static final int REQUEST_ENABLE_BT = 1;
    public static BluetoothLeService mBluetoothLeService; //Common variable for Read/Write operation


    private TextView mRSSI;
    private ExpandableListView mGattServicesList;
    private static final int LONG_DELAY = 5000;

    private ArrayList<ArrayList<BluetoothGattCharacteristic>> mGattCharacteristics =
            new ArrayList<ArrayList<BluetoothGattCharacteristic>>();

    private BluetoothGattCharacteristic mNotifyCharacteristic, mIndicateCharacteristic; //used for Notification

    private final String LIST_NAME = "NAME";
    private final String LIST_UUID = "UUID";
    private final String LIST_PROPERTIES = "PROPERTIES";
    private final String LIST_PERMISSION = "PERMISSION";
    private final String LIST_WRITETYPE = "WRITETYPE";

    private BluetoothAdapter mBluetoothAdapter;
    AlertDialog CharSelectDialog;
    UartProtocol data1 = new UartProtocol();
    ListView listView;
    CustomAdapter customAdapter;
    RelativeLayout emptyMainListLayout,companyAddressLayout;

    public void connect(int index)
    {
        mBluetoothLeService.connect(FileHelper.arrayOfUnits.get(index).getMacAddress());
        FileHelper.arrayOfUnits.get(index).setConnectState(CON_CONNECTING);
    }

    public void register()
    {
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
    }

    public void unRegister()
    {
        unregisterReceiver(mGattUpdateReceiver);
    }

    public void bind()
    {
        Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
        setActiveUnitState(PROC_TRY_BIND);
        timerProcess = 5;
        //Toast.makeText(MainActivity.this, "Binding", Toast.LENGTH_LONG).show();
    }

    public void unBind()
    {
        try{unbindService(mServiceConnection);}
        catch (Exception e){Toast.makeText(MainActivity.this, "EBind", Toast.LENGTH_SHORT).show();}
    }

    // Code to manage Service lifecycle.
    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
            if (!mBluetoothLeService.initialize()) {
                //Log.e(TAG, "Unable to initialize Bluetooth");
                finish();
            }
            // Automatically connects to the device upon successful start-up initialization.
            //Toast.makeText(MainActivity.this, "Binded", Toast.LENGTH_LONG).show();
            setActiveUnitState(PROC_BIND_OK);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            //Toast.makeText(MainActivity.this, "ServiceDisconnected" + mDeviceAddress, Toast.LENGTH_LONG).show();
            mBluetoothLeService = null;
        }
    };

    // Handles various events fired by the Service.
    // ACTION_GATT_CONNECTED: connected to a GATT server.
    // ACTION_GATT_DISCONNECTED: disconnected from a GATT server.
    // ACTION_GATT_SERVICES_DISCOVERED: discovered GATT services.
    // ACTION_DATA_AVAILABLE: received data from the device.  This can be a result of read
    // or notification operations.

    public void counterRxIncrement()
    {
        FileHelper.arrayOfUnits.get(globalIndexOfActiveUser).setCounterDataRx(FileHelper.arrayOfUnits.get(globalIndexOfActiveUser).getCounterDataRx()+1);
    }

    public void counterRxClear()
    {
        FileHelper.arrayOfUnits.get(globalIndexOfActiveUser).setCounterDataRx(0);
    }

    public int getCounterRx()
    {
        return FileHelper.arrayOfUnits.get(globalIndexOfActiveUser).getCounterDataRx();
    }

    public void setActiveUnitState(int proc)
    {
        FileHelper.arrayOfUnits.get(globalIndexOfActiveUser).setProcess(proc);
        timerProcess = 0;
    }

    public int getActiveUnitState()
    {
        return FileHelper.arrayOfUnits.get(globalIndexOfActiveUser).getProcess();
    }

    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (action.equals(BluetoothLeService.ACTION_GATT_CONNECTED)) {

                setActiveUnitState(PROC_CONNECTED);

                FileHelper.arrayOfUnits.get(globalIndexOfActiveUser).setConnectState(CON_CONNECTED);
                invalidateOptionsMenu();
                //Toast.makeText(MainActivity.this, "Connected", Toast.LENGTH_LONG).show();
                globalCustomName = FileHelper.arrayOfUnits.get(globalIndexOfActiveUser).getCustomName(); // ini custom namo for click list
            }
            else if (action.equals(BluetoothLeService.ACTION_GATT_DISCONNECTED)) {
                Log.i(TAG, "Try to connect?");
                //Toast.makeText(MainActivity.this, "Disconnected", Toast.LENGTH_LONG).show();
                FileHelper.arrayOfUnits.get(globalIndexOfActiveUser).setConnectState(CON_DISCONNECT);

                BluetoothLeService.mTargetCharacteristic = null;
                invalidateOptionsMenu();
                clearUI();
                unRegister();
                unBind();
                mBluetoothLeService.myClose();

                setActiveUnitState(PROC_CLOSE);

            } else if (action.equals(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED)) {
                // Show all the supported services and characteristics on the user interface.

                try {
                    displayGattServices(mBluetoothLeService.getSupportedGattServices());
                    //Toast.makeText(MainActivity.this, "Discovered", Toast.LENGTH_SHORT).show();

                    goThere();


                } catch (Exception e) {
                     Toast.makeText(MainActivity.this, "Error gatt", Toast.LENGTH_SHORT).show();
                }

            } else if (action.equals(DeviceScanActivity.DEVICE_DATA_AVAILABLE)) {
                //mDeviceName = intent.getStringExtra(EXTRAS_DEVICE_NAME);
                //mDeviceAddress = intent.getStringExtra(EXTRAS_DEVICE_ADDRESS);

            } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {

                displayRxData(intent.getByteArrayExtra(BluetoothLeService.EXTRA_DATA));

            } else if (action.equals(BluetoothLeService.RSSI_DATA_AVAILABLE)) {
                try{
                    displayRSSI(intent.getStringExtra(BluetoothLeService.EXTRA_DATA));
                }
                catch (Exception e){;}

            }
            Log.i(TAG, "BroadcastReceiver.onReceive():action=" + action);
        }
    };

    private void displayRxData(byte[] data) {
        if (data != null) {

            //Toast.makeText(MainActivity.this, "hm?", Toast.LENGTH_SHORT).show();
            //final StringBuilder strData = new StringBuilder();

            //data1.process(data);

            //Toast.makeText(MainActivity.this, "Data rx", Toast.LENGTH_SHORT).show();

            setActiveUnitState(PROC_ONLINE_RXOK);
            updateView(globalIndexOfActiveUser);

            // One user in list?
            if(FileHelper.arrayOfUnits.size() == 1){flagStillConnect = true;}

            if((globalUserMode == USER) && (globalIndexOfActiveUser != 0))
            {
                setProcessig(SKIP_TO_MODE_USER,0);
                enableNotify();
            }

            if(!flagStillConnect)
            {
                if(getCounterRx() < 1){counterRxIncrement();}
                else{enableNotify();} // Disable notify!!!
            }

            if(wantToClearUser)
            {
                counterRxIncrement();
                enableNotify();
            }

            if(wantToSkipToMenu)
            {
                if(globalIndexOfActiveUser == wantedUser) {

                    wantToSkipToMenu = false;

                    globalDeviceName = FileHelper.arrayOfUnits.get(globalIndexOfActiveUser).getBleName();
                    globalCustomName = FileHelper.arrayOfUnits.get(globalIndexOfActiveUser).getCustomName();
                    globalSerial = FileHelper.arrayOfUnits.get(globalIndexOfActiveUser).getMacAddress();

                    timerRun = false;
                    //final Intent intent = new Intent(MainActivity.this, MenuUnit.class); //default
                    final Intent intent = new Intent(MainActivity.this, activity_led_control.class); //default
                    startActivity(intent);
                }
            }

            if(wantToSkipToScan)
            {
                counterRxIncrement();
                enableNotify();
            }
        }
    }

    public void goThere() {
        // BluetoothLeService.mTargetCharacteristic.getUuid();
        // final int charaProp = characteristic.getProperties();
        //BluetoothGattCharacteristic mTargetCharacteristic = mGattCharacteristics.get(3).get(0); // Microchip - ISSC
        BluetoothGattCharacteristic mTargetCharacteristic = mGattCharacteristics.get(3).get(0); // ESP32
        BluetoothLeService.mTargetCharacteristic = mTargetCharacteristic;

        //Log.d(TAG, String.format("*** ExpandableListView.OnChildClickListener uuid=%s",mTargetCharacteristic.getUuid()));
        UUID uuid = mTargetCharacteristic.getUuid();
        //Toast.makeText(MainActivity.this, "Go there", Toast.LENGTH_SHORT).show();
        enableNotify();

       // mTargetCharacteristic = mGattCharacteristics.get(2).get(1); // ESP32
        BluetoothLeService.mTargetCharacteristic = mTargetCharacteristic;

        /*if (uuid.equals(UUID.fromString("49535343-1e4d-4bd9-ba61-23c647249616"))) { //TX
            //Toast.makeText(MainActivity.this, "Klik", Toast.LENGTH_SHORT).show();
            //Intent intent = new Intent(MainActivity.this, CharacteristicReadWriteActivity.class); //Generic
            //startActivity(intent);
            //finish();
            Toast.makeText(MainActivity.this, "Uuid Microchip", Toast.LENGTH_SHORT).show();
            enableNotify();
            return;
        }

        if (uuid.equals(UUID.fromString("6e400002-b5a3-f393-e0a9-e50e24dcca9e"))) {//Tx
            //Toast.makeText(MainActivity.this, "Klik", Toast.LENGTH_SHORT).show();
            //Intent intent = new Intent(MainActivity.this, CharacteristicReadWriteActivity.class); //Generic
            //startActivity(intent);
            //finish();
            Toast.makeText(MainActivity.this, "Uuid esp32", Toast.LENGTH_SHORT).show();
            enableNotify();
            return;
        }*/

        //Toast.makeText(MainActivity.this, "Uuid", Toast.LENGTH_SHORT).show();

    }

    // If a given GATT characteristic is selected, check for supported features.  This sample
    // demonstrates 'Read' and 'Notify' features.  See
    // http://d.android.com/reference/android/bluetooth/BluetoothGatt.html for the complete
    // list of supported characteristic features.
    private final ExpandableListView.OnChildClickListener servicesListClickListner =
            new ExpandableListView.OnChildClickListener() {
                @Override
                public boolean onChildClick(ExpandableListView parent, View v, int groupPosition,
                                            int childPosition, long id) {
                    if (mGattCharacteristics != null) {

                        Log.i(TAG, String.format("*** ExpandableListView.OnChildClickListener group=%d, child=%d, id=%d", groupPosition, childPosition, id));

                        //Toast.makeText(MainActivity.this, "Gr: "+groupPosition + " " + "Ch: "+ childPosition + " " + "Id: " +id, Toast.LENGTH_SHORT).show();

                        //mDataField.setText("not received"); //clear
                        BluetoothGattCharacteristic mTargetCharacteristic = mGattCharacteristics.get(groupPosition).get(childPosition);
                        BluetoothLeService.mTargetCharacteristic = mTargetCharacteristic;
                        //  final int charaProp = characteristic.getProperties();

                        Log.i(TAG, String.format("*** ExpandableListView.OnChildClickListener uuid=%s", mTargetCharacteristic.getUuid()));
                        UUID uuid = mTargetCharacteristic.getUuid();
                        //final Intent intent;
                        if (uuid.equals(UUID.fromString("49535343-1e4d-4bd9-ba61-23c647249616"))) {
                            //Toast.makeText(MainActivity.this, "Klik", Toast.LENGTH_SHORT).show();
                            //Intent intent = new Intent(MainActivity.this, CharacteristicReadWriteActivity.class); //Generic
                            //startActivity(intent);
                            //finish();
                        }

                        Log.i(TAG, String.format("*** IsUartCharacteristic(%s)=%d", mTargetCharacteristic.getUuid(), GattAttributes.IsUartCharacteristic(uuid)));
                        if (GattAttributes.IsUartCharacteristic(uuid) >= 0) { //Uart Service
                            //CharacteristicUartActivity.uartIndex = GattAttributes.IsUartCharacteristic(uuid);

                            //
                            //CharSelectDialog.show();
                        } else {
                            //final Intent intent = new Intent(MainActivity.this, CharacteristicReadWriteActivity.class); //default
                            //startActivity(intent);
                        }
                        /*
                        // Notify first, then Read
                        if ((charaProp & BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
                            if (mNotifyCharacteristic == null) {
                                // Enable Notify
                                Toast.makeText(MainActivity.this, "Notify Enabled", Toast.LENGTH_SHORT).show();
                                mNotifyCharacteristic = characteristic;
                                mBluetoothLeService.setCharacteristicNotification(characteristic, true);
                                mBluetoothLeService.readCharacteristic(characteristic);
                            } else {
                                // Disable Notify
                                Toast.makeText(MainActivity.this, "Notify Disabled", Toast.LENGTH_SHORT).show();
                                mBluetoothLeService.setCharacteristicNotification(mNotifyCharacteristic, false);
                                mNotifyCharacteristic = null;
                            }
                        } else if ((charaProp & BluetoothGattCharacteristic.PROPERTY_READ) > 0) {
                            // If there is an active notification on a characteristic, clear
                            // it first so it doesn't update the data field on the user interface.
                            if (mNotifyCharacteristic != null) {
                                mBluetoothLeService.setCharacteristicNotification( mNotifyCharacteristic, false);
                                mNotifyCharacteristic = null;
                            }
                            Toast.makeText(MainActivity.this, "Read", Toast.LENGTH_SHORT).show();
                            mBluetoothLeService.readCharacteristic(characteristic);
                        }
                        */
                        return true;
                    } else {
                        BluetoothLeService.mTargetCharacteristic = null;
                    }
                    return false;
                }
            };

    private void clearUI() {
        mGattServicesList.setAdapter((SimpleExpandableListAdapter) null);
        //mDataField.setText(R.string.no_data);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "Create");
        setContentView(R.layout.activity_main);

        emptyMainListLayout = (RelativeLayout)findViewById(R.id.emptyMainListLayout);
        companyAddressLayout = (RelativeLayout)findViewById(R.id.companyAddressLayout);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            // Device doesn't support Bluetooth
            Toast.makeText(this, R.string.error_bluetooth_not_supported, Toast.LENGTH_SHORT).show();
            finish();
        }

        if (!mBluetoothAdapter.isEnabled()) {
            //Toast.makeText(this, R.string.error_bluetooth_not_supported, Toast.LENGTH_SHORT).show();
            //finish();
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }

        while(!mBluetoothAdapter.isEnabled()){;}

        listView = (ListView) findViewById(R.id.listView);
        customAdapter = new CustomAdapter();

        //mDeviceName = "CleanAIR";
        //Initialization of units array
        if (!FileHelper.wasRead) {
            try {

                FileHelper.arrayOfUnits = FileHelper.Init(this);

                try {
                    FileHelper.wasRead = true;
                    getActionBar().setDisplayHomeAsUpEnabled(true);
                    mainProcess();

                } catch (Exception e) {
                    Toast.makeText(MainActivity.this, "Error Show", Toast.LENGTH_SHORT).show();
                }

            } catch (Exception e) {
                //info.setText("Error init");
                Toast.makeText(MainActivity.this, "List empty!", Toast.LENGTH_SHORT).show();
            }
        }

        // Sets up UI references.
        mGattServicesList = (ExpandableListView) findViewById(R.id.gatt_services_list);
        mGattServicesList.setOnChildClickListener(servicesListClickListner);

        mRSSI = (TextView) findViewById(R.id.control_RSSI);


        Button btn_Add = (Button) findViewById(R.id.buttonAddDevice);

        final String[] items = {"Read/Write", "UART"}; //,"KosoMeter"};
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select I/O Control").setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                CharSelectDialog.hide();
                final Intent intent;
                switch (which) {
                    case 0: //Geeneric IO
                        //intent = new Intent(MainActivity.this, CharacteristicReadWriteActivity.class); //Generic
                        //startActivity(intent);
                        break;
                    case 1: //Uart
                        //intent = new Intent(MainActivity.this, CharacteristicUartActivity.class); //Uart
                        //startActivity(intent);
                        break;
                    /*
                                         case 2: //KosoMeter
                                                intent = new Intent(MainActivity.this, KosoMeterActivity.class); //KosoMeter
                                                startActivity(intent);
                                                break;
                                        */
                }
            }
        });

        CharSelectDialog = builder.create();

        displayDeviceInfo();

        listView.setAdapter(customAdapter);

        listView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });

        btn_Add.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (FileHelper.arrayOfUnits.size() > 4) {
                    Toast.makeText(MainActivity.this, "Maximum number of users reached!", Toast.LENGTH_SHORT).show();
                } else {

                    //stopTimer();
                    Toast.makeText(MainActivity.this, "Ok!", Toast.LENGTH_SHORT).show();

                }
            }
        });

        emptyMainListLayout.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {

                addDevice();
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                setProcessig(SKIP_TO_MENU,i);
            }
        });

        listView.setOnItemLongClickListener(new OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int pos, long id) {

                // TODO Auto-generated method stub
                Log.v("long clicked", "pos: " + pos);
                clearUser(pos);
                return true;
            }
        });


        if (!debugInfoPanel) {
            TextView mConnectionState = (TextView) findViewById(R.id.connection_state);
            mConnectionState.setVisibility(View.GONE);
            mRSSI.setVisibility(View.GONE);
            ((TextView) findViewById(R.id.device_address)).setVisibility(View.GONE);
            ((TextView) findViewById(R.id.tvDeviceAddress)).setVisibility(View.GONE);
            ((TextView) findViewById(R.id.tvState)).setVisibility(View.GONE);
            ((TextView) findViewById(R.id.tvRssi)).setVisibility(View.GONE);
        }

        if(FileHelper.readFromFile(readContext(),"userMode.txt").equals(""))
        {
            Toast.makeText(MainActivity.this, "User mode is empty", Toast.LENGTH_SHORT).show();
            FileHelper.writeToFile(String.valueOf(globalUserMode),readContext(),"userMode.txt");
        }
        else
        {
            globalUserMode = Integer.valueOf(FileHelper.readFromFile(readContext(),"userMode.txt"));
            //Toast.makeText(MainActivity.this, FileHelper.readUserMode(readContext()), Toast.LENGTH_SHORT).show();
        }

        setTitleMode(globalUserMode);
        startTimer();
    }

    public void stopTimer()
    {
        if(timerRun) {
            //timer.purge();
            //timer.cancel();
            timerRun = false;
            //Toast.makeText(MainActivity.this, "Stop", Toast.LENGTH_SHORT).show();
        }
    }

    public Context readContext() {
        return this;
    }

    public void setProcessig(int proc, int position)
    {
        wantToSkipToMenu = false;
        wantToTopUser = false;
        wantToClearUser = false;
        wantToSkipToScan = false;
        wantToModeUser = false;

        switch (proc)
        {
            case SKIP_TO_MENU: wantToSkipToMenu = true; break;
            case TOP_USER: wantToTopUser = true; break;
            case CLEAR_USER: wantToClearUser = true; break;
            case SKIP_TO_SCAN: wantToSkipToScan = true; break;
            case SKIP_TO_MODE_USER: wantToModeUser = true; break;
        }

        flagProcessing = true;

        wantedUser = position;
        Toast.makeText(MainActivity.this, "Wait, processing ...", Toast.LENGTH_SHORT).show();
        invalidateOptionsMenu();
    }

    public void enableNotify() {

        //BluetoothGattCharacteristic mTargetCharacteristic = mGattCharacteristics.get(3).get(0); // Microchip
        BluetoothGattCharacteristic mTargetCharacteristic = mGattCharacteristics.get(3).get(0);
        BluetoothLeService.mTargetCharacteristic = mTargetCharacteristic;

        if (BluetoothLeService.mTargetCharacteristic != null) {
            // if ( MainActivity.mConnected) { //Check connection state before READ

            if (mNotifyCharacteristic == null) {
                setActiveUnitState(PROC_TRY_NOTIFY);
                timerProcess = 5;
                // Enable Notify
                //if (debugToastView)
                //Toast.makeText(MainActivity.this, "Notify Enabled", Toast.LENGTH_SHORT).show();

                mNotifyCharacteristic = BluetoothLeService.mTargetCharacteristic;
                MainActivity.mBluetoothLeService.setCharacteristicNotification(mNotifyCharacteristic, true);
                MainActivity.mBluetoothLeService.readCharacteristic(mNotifyCharacteristic); //read once to start notify?
                //mButtonNotify.setTextColor(Color.GREEN);
                setActiveUnitState(PROC_NOTIFY_ENABLED);
            } else {

                setActiveUnitState(PROC_TRY_NOTIFY_DISABLE);
                timerProcess = 5;
                // Disable Notify
                //if (debugToastView)
                //Toast.makeText(MainActivity.this, "Notify Disabled", Toast.LENGTH_SHORT).show();
                MainActivity.mBluetoothLeService.setCharacteristicNotification(mNotifyCharacteristic, false);
                mNotifyCharacteristic = null;
                //mButtonNotify.setTextColor(Color.BLACK);
                setActiveUnitState(PROC_NOTIFY_DISABLED);
            }
            //}
        } else {
            Toast.makeText(MainActivity.this, "Notify target", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "Resume");

        if ((FileHelper.arrayOfUnits.size() > 0) && FileHelper.arrayOfUnits.get(globalIndexOfActiveUser).getConnectState() == CON_CONNECTED)
        {
            register();
        }

        if(menuWasDisconnect)
        {
            counterRxClear();
            setActiveUnitState(PROC_CLOSE);
        }

        timerRun = true;
        isListEmptyShowInitDisp();
        setTitleMode(globalUserMode);
        flagProcessing = false;
        refreshMenu();
        listView.setAdapter(customAdapter);
    }

    @Override
    protected void onPause() {
        super.onPause();

        Log.i(TAG, "Pause");

        if ((FileHelper.arrayOfUnits.size() > 0)
                && FileHelper.arrayOfUnits.get(globalIndexOfActiveUser).getConnectState() == CON_CONNECTED)
        {
            unRegister();
        }

        timerRun = false;
    }

    @Override
    protected void onDestroy() {
        Log.i(TAG, "Destroy");
        super.onDestroy();
        unBind();
        mBluetoothLeService = null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.device_control, menu);

        menu.findItem(R.id.menu_refresh).setActionView(R.layout.actionbar_indeterminate_progress);

        if(!flagAddIconMenuVisible){
            menu.findItem(R.id.add_unit_menu).setVisible(false);
        }
        else {
            menu.findItem(R.id.add_unit_menu).setVisible(true);
        }

        if(flagProcessing)
        {
            menu.findItem(R.id.menu_refresh).setVisible(true);
        }
        else
        {
            menu.findItem(R.id.menu_refresh).setVisible(false);
        }

        if (!debugInfoPanel) {
            menu.findItem(R.id.test_graphic).setVisible(false);
            menu.findItem(R.id.add_virtual).setVisible(false);
            menu.findItem(R.id.menu_connect).setVisible(false);
            menu.findItem(R.id.menu_disconnect).setVisible(false);
        }

        if(globalUserMode == ADMIN && debugInfoPanel)
        {
            menu.findItem(R.id.add_virtual).setVisible(true);
        }

        return true;
    }

    public void setTitleMode(int m)
    {
        switch (m)
        {
            case 0: getActionBar().setTitle("LSCS - user");     break;
            case 1: getActionBar().setTitle("LSCS - advanced"); break;
            case 2: getActionBar().setTitle("LSCS - admin");    break;
        }
    }

    private void isListEmptyShowInitDisp ()
    {
        if(FileHelper.arrayOfUnits.size() == 0)
        {
            emptyMainListLayout.setVisibility(View.VISIBLE);
            companyAddressLayout.setVisibility(View.VISIBLE);
        }
        else
        {
            emptyMainListLayout.setVisibility(View.GONE);
            companyAddressLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_connect:
                return true;

            case R.id.menu_disconnect:
                return true;

            case R.id.add_unit_menu:
                addDevice();
                return true;

            case R.id.test_graphic:
                final Intent intent = new Intent(MainActivity.this, Test_graphic.class); //default
                startActivity(intent);

                return true;

            case R.id.add_virtual:
                TypeUnits unit = new TypeUnits("Virtual " + String.valueOf(counterVirtual),"51000000" + String.valueOf(counterVirtual) + "Virtual" + String.valueOf(counterVirtual) ,"51:00:00:00:00:0" + String.valueOf(counterVirtual),"100");
                counterVirtual++;
                FileHelper.arrayOfUnits.add(unit);
                listView.setAdapter(customAdapter);
                Toast.makeText(MainActivity.this, "Virtual added", Toast.LENGTH_SHORT).show();
                return true;

            case android.R.id.home:
                onBackPressed();
                return true;

            case R.id.menu_user:
                globalUserMode = USER;
                setProcessig(SKIP_TO_MODE_USER,0);
                flagStillConnect = true;
                FileHelper.writeToFile(String.valueOf(globalUserMode),readContext(),"userMode.txt");
                refreshMenu();
                setTitleMode(globalUserMode);
                listView.setAdapter(customAdapter);
                return true;

            case R.id.menu_advancedUser:
                if(FileHelper.arrayOfUnits.size() == 1) {flagStillConnect = true;}
                else{flagStillConnect = false;}
                globalUserMode = ADVANCED;
                FileHelper.writeToFile(String.valueOf(globalUserMode),readContext(),"userMode.txt");
                setTitleMode(globalUserMode);
                refreshMenu();
                listView.setAdapter(customAdapter);
                return true;

            case R.id.menu_admin:
                if(FileHelper.arrayOfUnits.size() == 1) {flagStillConnect = true;}
                else{flagStillConnect = false;}
                insertPassword();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void updateConnectionState(final int resourceId) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //mConnectionState.setText(resourceId);
            }
        });
    }

    private void addDevice()
    {
        if (FileHelper.arrayOfUnits.size() > 6) {
            Toast.makeText(MainActivity.this, "Maximum number of users reached!", Toast.LENGTH_SHORT).show();
        }
        else {

            if(FileHelper.arrayOfUnits.size() == 0)
            {
                final Intent intent = new Intent(MainActivity.this, DeviceScanActivity.class); //default
                startActivity(intent);
            }
            else
            {
                setProcessig(SKIP_TO_SCAN,0);
            }
        }
    }

    private void displayDeviceInfo() {

        /*if (mDeviceAddress != null) {
            ((TextView) findViewById(R.id.device_address)).setText(mDeviceAddress);
            getActionBar().setTitle(mDeviceName);
        } else {
            ((TextView) findViewById(R.id.device_address)).setText(R.string.no_data);
            getActionBar().setTitle(R.string.no_data);
        }*/
    }

    private void userUnitOffline(int index) {
        View v = listView.getChildAt(index -
                listView.getFirstVisiblePosition());

        //FileHelper.arrayOfUnits.get(index).setWasOffline(true); // Timer offline unit

        if (v == null)
            return;

        updateViewBtSymbol(globalIndexOfActiveUser, BDISABLE);

        FileHelper.arrayOfUnits.get(index).setOnline(false);

        Resources res = getResources();

        ImageView alert = (ImageView) v.findViewById(R.id.ivAlert);
        FileHelper.arrayOfUnits.get(index).setWarn(false);
        //alert.setImageDrawable(res.getDrawable(R.drawable.alertmy));
        alert.setVisibility(View.GONE);


        ProgressBar flow = (ProgressBar) v.findViewById(R.id.progressBarFlow);
        ProgressBar filtr = (ProgressBar) v.findViewById(R.id.Filter);
        ProgressBar battery = (ProgressBar) v.findViewById(R.id.progressBarBattery);
        flow.setVisibility(View.INVISIBLE);
        flow.setProgress(0);
        filtr.setProgress(0);
        battery.setProgress(0);

        FileHelper.arrayOfUnits.get(index).setFlowLiter("");
        FileHelper.arrayOfUnits.get(index).setFlow(0);
        FileHelper.arrayOfUnits.get(index).setFilter(0);
        FileHelper.arrayOfUnits.get(index).setBattery(0);

        TextView flowLiter = v.findViewById(R.id.tv_flow_obvolavani);
        //flowLiter.setTextColor(Color.BLACK);
        flowLiter.setText("");

        ImageView unit = (ImageView) v.findViewById(R.id.imageView);
        ImageView bar = (ImageView) v.findViewById(R.id.imageViewBars);

        if(isString3F(FileHelper.arrayOfUnits.get(index).getBleName()))
        {
            unit.setImageDrawable(res.getDrawable(R.drawable.unit3f_off));
        }

        if(isString2F(FileHelper.arrayOfUnits.get(index).getBleName()))
        {
            unit.setImageDrawable(res.getDrawable(R.drawable.unit2f_off));
        }

        bar.setImageDrawable(res.getDrawable(R.drawable.bars_off));

        //FileHelper.arrayOfUnits.get(index).setStatusOnline(false);

        //sortOnlineDevice();
    }

    private void userUnitOnline(int index) {
        View v = listView.getChildAt(index -
                listView.getFirstVisiblePosition());

        if (v == null)
            return;

        //TextView flowLiter = v.findViewById(R.id.tv_flow_obvolavani);
        //int myColor = getResources().getColor(R.color.greenProgress);
        //flowLiter.setTextColor(myColor);

        Resources res = getResources();
        ImageView unit = (ImageView) v.findViewById(R.id.imageView);

        viewDrawUnit(v,index);

        ImageView bar = (ImageView) v.findViewById(R.id.imageViewBars);

        ProgressBar flow = (ProgressBar) v.findViewById(R.id.progressBarFlow);
        ProgressBar filtr = (ProgressBar) v.findViewById(R.id.Filter);
        ProgressBar battery = (ProgressBar) v.findViewById(R.id.progressBarBattery);
        // flow.setVisibility(View.INVISIBLE);
        flow.setProgress(FileHelper.arrayOfUnits.get(index).getFlow());
        filtr.setProgress(FileHelper.arrayOfUnits.get(index).getFilter());
        battery.setProgress(FileHelper.arrayOfUnits.get(index).getBattery());

        bar.setImageDrawable(res.getDrawable(R.drawable.bars));
    }

    public void vibration()
    {
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        // Vibrate for 500 milliseconds
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            //deprecated in API 26
            v.vibrate(50);
        }
    }

    private void viewDrawUnit(View v, int index)
    {
        Resources res = getResources();
        ImageView unitImage =  (ImageView)v.findViewById(R.id.imageView);


        if(isString3F(FileHelper.arrayOfUnits.get(index).getBleName())) {
            unitImage.setImageDrawable(res.getDrawable(R.drawable.unit3f));
            return;
        }

        if(isString2F(FileHelper.arrayOfUnits.get(index).getBleName()))
        {
            unitImage.setImageDrawable(res.getDrawable(R.drawable.unit2f));
        }
        else
        {
            // Generali device
            unitImage.setImageDrawable(res.getDrawable(R.drawable.ble));
        }
    }

    private int updateViewBtSymbol(int index, int state)
    {
        View v = listView.getChildAt(index -
                listView.getFirstVisiblePosition());

        if (v == null)
            return 0;

        Resources res = getResources();
        ImageView bt =  (ImageView)v.findViewById(R.id.ivBlue);

        switch(state)
        {
            case BNONE: bt.setImageDrawable(res.getDrawable(R.drawable.base));break;
            case BCONNECTED: bt.setImageDrawable(res.getDrawable(R.drawable.base_conected));break;
            case BDISABLE: bt.setImageDrawable(res.getDrawable(R.drawable.base_disable));break;
            case BSEARCH: bt.setImageDrawable(res.getDrawable(R.drawable.base_search));break;
        }

       return 0;
    }

    private int updateView(int index) {
        View v = listView.getChildAt(index -
                listView.getFirstVisiblePosition());

        if (v == null)
            return 50;

        // Was offline?
        if(!FileHelper.arrayOfUnits.get(globalIndexOfActiveUser).getOnline())
        {
            userUnitOnline(globalIndexOfActiveUser);
        }

        FileHelper.arrayOfUnits.get(index).setOnline(true);

        TextView flow = (TextView)v.findViewById(R.id.tv_flow_obvolavani);

        viewDrawUnit(v,index);

        ProgressBar prFlow = (ProgressBar) v.findViewById(R.id.progressBarFlow);
        ProgressBar prFilter = (ProgressBar) v.findViewById(R.id.Filter);
        ProgressBar prBattery = (ProgressBar) v.findViewById(R.id.progressBarBattery);
       // flow.setVisibility(View.INVISIBLE);
        TextView user = (TextView) v.findViewById(R.id.customName);

        //user.setTextColor(Color.GREEN);
        //user.setBackgroundColor(Color.GREEN);

        updateViewBtSymbol(index,BCONNECTED);

        FileHelper.arrayOfUnits.get(index).setFlowLiter(data1.getStatusFlowRpm());
        FileHelper.arrayOfUnits.get(index).setFlow(data1.getProgressStatusFlow());
        FileHelper.arrayOfUnits.get(index).setFilter(data1.getStatusFilter());
        FileHelper.arrayOfUnits.get(index).setBattery(data1.getStatusBattery());

        flow.setText(data1.getStatusFlowRpm());
        prFlow.setProgress(data1.getProgressStatusFlow());
        prFilter.setProgress(data1.getStatusFilter());
        prBattery.setProgress(data1.getStatusBattery());


        ImageView unitAlert =  (ImageView)v.findViewById(R.id.ivAlert);

        if(data1.getWarings() > 0)
        {
            FileHelper.arrayOfUnits.get(index).setWarn(true);
        }
        else
        {
            FileHelper.arrayOfUnits.get(index).setWarn(false);
        }

        if(FileHelper.arrayOfUnits.get(index).getWarn()/*;data1.getWarings() > 0*/)
        {
            //unitImage.setImageDrawable(res.getDrawable(R.drawable.alarm));
            unitAlert.setVisibility(View.VISIBLE);
            //FileHelper.arrayOfUnits.get(index).setWarn(true);
            //Toast.makeText(MainActivity.this, String.valueOf(data1.getWarings()), Toast.LENGTH_LONG).show();
            vibration();
        }
        else
        {
            unitAlert.setVisibility(View.GONE);
            //FileHelper.arrayOfUnits.get(index).setWarn(true);
        }

        return data1.getStatusFilter();
    }

    private void displayRSSI(String data) {
        try {
            if (data != null) {
                mRSSI.setText(data + " dBm");

               // data1.cmdMarekSet();
               //data1.cmdMarekSetValue(0x00);
                //Toast.makeText(getApplicationContext(),"Data send", Toast.LENGTH_SHORT).show();

            } else {
                mRSSI.setText(R.string.no_data);
            }
        }
        catch (Exception e){;}
    }

    // Demonstrates how to iterate through the supported GATT Services/Characteristics.
    // In this sample, we populate the data structure that is bound to the ExpandableListView
    // on the UI.
    private void displayGattServices(List<BluetoothGattService> gattServices) {
        if (gattServices == null) return;
        String uuid = null;
        String unknownServiceString = getResources().getString(R.string.unknown_service);
        String unknownCharaString = getResources().getString(R.string.unknown_characteristic);
        ArrayList<HashMap<String, String>> gattServiceData = new ArrayList<HashMap<String, String>>();
        ArrayList<ArrayList<HashMap<String, String>>> gattCharacteristicData
                = new ArrayList<ArrayList<HashMap<String, String>>>();
        mGattCharacteristics = new ArrayList<ArrayList<BluetoothGattCharacteristic>>();

        // Loops through available GATT Services.
        for (BluetoothGattService gattService : gattServices) {
            HashMap<String, String> currentServiceData = new HashMap<String, String>();
            uuid = gattService.getUuid().toString();
            currentServiceData.put(
                    LIST_NAME, GattAttributes.lookup(uuid, unknownServiceString));
            currentServiceData.put(LIST_UUID, uuid);
            gattServiceData.add(currentServiceData);

            ArrayList<HashMap<String, String>> gattCharacteristicGroupData =
                    new ArrayList<HashMap<String, String>>();
            List<BluetoothGattCharacteristic> gattCharacteristics =
                    gattService.getCharacteristics();
            ArrayList<BluetoothGattCharacteristic> charas =
                    new ArrayList<BluetoothGattCharacteristic>();

            // Loops through available Characteristics.
            for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {

                charas.add(gattCharacteristic);

                HashMap<String, String> currentCharaData = new HashMap<String, String>();

                uuid = gattCharacteristic.getUuid().toString();

                currentCharaData.put(
                        LIST_NAME, GattAttributes.lookup(uuid, unknownCharaString));
                currentCharaData.put(LIST_UUID, uuid);

                final int charaProp = gattCharacteristic.getProperties();
                String strProperties = String.format("[%04X]", charaProp);
                if ((charaProp & BluetoothGattCharacteristic.PROPERTY_BROADCAST) > 0) {
                    strProperties += " Broadcast";
                }
                if ((charaProp & BluetoothGattCharacteristic.PROPERTY_INDICATE) > 0) {
                    strProperties += " Indicate";
                }
                if ((charaProp & BluetoothGattCharacteristic.PROPERTY_EXTENDED_PROPS) > 0) {
                    strProperties += " ExtendedProps";
                }
                if ((charaProp & BluetoothGattCharacteristic.PROPERTY_READ) > 0) {
                    strProperties += " Read";
                }
                if ((charaProp & BluetoothGattCharacteristic.PROPERTY_WRITE) > 0) {
                    strProperties += " Write";
                }
                if ((charaProp & BluetoothGattCharacteristic.PROPERTY_SIGNED_WRITE) > 0) {
                    strProperties += " SignedWrite";
                }
                if ((charaProp & BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE) > 0) {
                    strProperties += " WriteNoResponse";
                }
                if ((charaProp & BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
                    strProperties += " Notify";
                }
                currentCharaData.put(LIST_PROPERTIES, strProperties);

                final int charaPerm = gattCharacteristic.getPermissions();
                String strPermission = String.format("[%04X]", charaPerm);
                if ((charaPerm & BluetoothGattCharacteristic.PERMISSION_READ) > 0) {
                    strPermission += " Read";
                }
                if ((charaPerm & BluetoothGattCharacteristic.PERMISSION_READ_ENCRYPTED) > 0) {
                    strPermission += " ReadEncrypted";
                }
                if ((charaPerm & BluetoothGattCharacteristic.PERMISSION_READ_ENCRYPTED_MITM) > 0) {
                    strPermission += " ReadEncryptedMitm";
                }
                if ((charaPerm & BluetoothGattCharacteristic.PERMISSION_WRITE) > 0) {
                    strPermission += " Write";
                }
                if ((charaPerm & BluetoothGattCharacteristic.PERMISSION_WRITE_ENCRYPTED) > 0) {
                    strPermission += " WriteEncrypted";
                }
                if ((charaPerm & BluetoothGattCharacteristic.PERMISSION_WRITE_ENCRYPTED_MITM) > 0) {
                    strPermission += "WriteEncryptedMitm";
                }
                if ((charaPerm & BluetoothGattCharacteristic.PERMISSION_WRITE_SIGNED) > 0) {
                    strPermission += " WriteSigned";
                }
                if ((charaPerm & BluetoothGattCharacteristic.PERMISSION_WRITE_SIGNED_MITM) > 0) {
                    strPermission += " WriteSignedMitm";
                }
                currentCharaData.put(LIST_PERMISSION, strPermission);

                final int charaWritetype = gattCharacteristic.getWriteType();
                String strWritetype = String.format("[%04X]", charaWritetype);
                if ((charaWritetype & BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT) > 0) {
                    strWritetype += "WriteTypeDefault";
                }
                if ((charaWritetype & BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE) > 0) {
                    strWritetype += "WriteTypeNoRespons";
                }
                if ((charaWritetype & BluetoothGattCharacteristic.WRITE_TYPE_SIGNED) > 0) {
                    strWritetype += "WriteTypeSogned";
                }
                currentCharaData.put(LIST_WRITETYPE, strWritetype);

                // finally add to group
                gattCharacteristicGroupData.add(currentCharaData);
            }
            mGattCharacteristics.add(charas);
            gattCharacteristicData.add(gattCharacteristicGroupData);
        }

        SimpleExpandableListAdapter gattServiceAdapter = new SimpleExpandableListAdapter(
                this,
                gattServiceData,
                android.R.layout.simple_expandable_list_item_2,
                new String[]{LIST_NAME, LIST_UUID},
                new int[]{android.R.id.text1, android.R.id.text2},
                gattCharacteristicData,
                R.layout.characteristics_item_view,
                new String[]{LIST_NAME, LIST_UUID, LIST_PROPERTIES, LIST_PERMISSION, LIST_WRITETYPE},
                new int[]{R.id.characteristic_name, R.id.characteristic_uuid, R.id.characteristic_properties, R.id.characteristic_permission, R.id.characteristic_writetype}
        );
        //mGattServicesList.setAdapter(gattServiceAdapter); // Show() Info list
    }

    class CustomAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            if(globalUserMode == USER)
            {
                if(FileHelper.arrayOfUnits.size() == 0){return 0;}
                else{return 1;}
            }
            else {
                return FileHelper.arrayOfUnits.size();
            }
        }

        @Override
        public Object getItem(int position) {
            return FileHelper.arrayOfUnits.get(position);//defaultArray[position];
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = getLayoutInflater().inflate(R.layout.sample_list, null);

            Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
            float ratioX = ((float) display.getWidth());

            //Toast.makeText(getApplicationContext(),String.valueOf(ratioX), Toast.LENGTH_SHORT).show();
            if(ratioX >= 1200 || flagTestResolutionDisp)
            {
                ImageView imageViewX = (ImageView) convertView.findViewById(R.id.imageView);
                RelativeLayout.LayoutParams paramsChangeView = (RelativeLayout.LayoutParams)imageViewX.getLayoutParams();
                //paramsChangeView.width = 300;

                //--------------------------------
                paramsChangeView.leftMargin = 100; /////////////// CONVERT !!!

                TextView textCBleName = (TextView) convertView.findViewById(R.id.bleName);
                RelativeLayout.LayoutParams paramsBle = (RelativeLayout.LayoutParams)textCBleName.getLayoutParams();
                paramsBle.leftMargin = 100;

                TextView textCustom = (TextView) convertView.findViewById(R.id.customName);
                RelativeLayout.LayoutParams paramsCustom = (RelativeLayout.LayoutParams)textCustom.getLayoutParams();
                paramsCustom.leftMargin = 100;

                ImageView imageBlue = (ImageView) convertView.findViewById(R.id.ivBlue);
                RelativeLayout.LayoutParams paramsBlue = (RelativeLayout.LayoutParams)imageBlue.getLayoutParams();
                paramsBlue.rightMargin = 60;
                //--------------------------------

                //Toast.makeText(getApplicationContext(),"Scale", Toast.LENGTH_SHORT).show();
            }

            TextView textViewBleName = (TextView) convertView.findViewById(R.id.bleName);
            TextView textViewCustomName = (TextView) convertView.findViewById(R.id.customName);
            TextView textViewSerial = (TextView) convertView.findViewById(R.id.serial);
            TextView textViewFlow = (TextView) convertView.findViewById(R.id.tv_flow_obvolavani);

            if(!isString2F(FileHelper.arrayOfUnits.get(position).getBleName())) {

                Resources res = getResources();
                ImageView imageView = (ImageView) convertView.findViewById(R.id.imageView);

                viewDrawUnit(convertView,position);
            }

            ProgressBar prFlow = (ProgressBar) convertView.findViewById(R.id.progressBarFlow);
            ProgressBar prFilter = (ProgressBar) convertView.findViewById(R.id.Filter);
            ProgressBar prBattery = (ProgressBar) convertView.findViewById(R.id.progressBarBattery);

            prFlow.setVisibility(View.INVISIBLE);
            prFilter.setVisibility(View.INVISIBLE);
            prBattery.setVisibility(View.INVISIBLE);

            String bleName = FileHelper.arrayOfUnits.get(position).getBleName();

            textViewBleName.setText(getStringNameChemical(bleName));
            textViewCustomName.setText(FileHelper.arrayOfUnits.get(position).getCustomName());

            if(isChemical(bleName)){textViewSerial.setText("S/N: "+ getStringSerialChemical(bleName));}
            else{textViewSerial.setText(FileHelper.arrayOfUnits.get(position).getMacAddress());}


            Resources res = getResources();
            //prFlow.setProgressDrawable(res.getDrawable(R.layout.greenprogress));
            //prFilter.setProgressDrawable(res.getDrawable(R.layout.orangeprogress));
            //prBattery.setProgressDrawable(res.getDrawable(R.layout.redprogress));
            //prBattery.setProgressDrawable(res.getDrawable(R.layout.redprogress));

            ImageView unit = (ImageView) convertView.findViewById(R.id.imageView);
            ImageView bar = (ImageView) convertView.findViewById(R.id.imageViewBars);

            if(FileHelper.arrayOfUnits.get(position).getOnline())
            {
                viewDrawUnit(convertView,position);
                bar.setImageDrawable(res.getDrawable(R.drawable.bars));
            }
            else
            {
                if(isString3F(FileHelper.arrayOfUnits.get(position).getBleName()))
                {
                    unit.setImageDrawable(res.getDrawable(R.drawable.unit3f_off));
                }

                if(isString2F(FileHelper.arrayOfUnits.get(position).getBleName()))
                {
                    unit.setImageDrawable(res.getDrawable(R.drawable.unit2f_off));
                }

                bar.setImageDrawable(res.getDrawable(R.drawable.bars_off));
            }

            textViewFlow.setText(FileHelper.arrayOfUnits.get(position).getFlowLiter());
            prFlow.setProgress(FileHelper.arrayOfUnits.get(position).getFlow());
            prFilter.setProgress(FileHelper.arrayOfUnits.get(position).getFilter());
            prBattery.setProgress(FileHelper.arrayOfUnits.get(position).getBattery());

            ImageView unitAlert =  (ImageView)convertView.findViewById(R.id.ivAlert);

            if(FileHelper.arrayOfUnits.get(position).getWarn()/*;data1.getWarings() > 0*/)
            {
                //unitImage.setImageDrawable(res.getDrawable(R.drawable.alarm));
                unitAlert.setVisibility(View.VISIBLE);
                //FileHelper.arrayOfUnits.get(index).setWarn(true);
                //Toast.makeText(MainActivity.this, String.valueOf(data1.getWarings()), Toast.LENGTH_LONG).show();
                //vibration();
            }
            else
            {
                unitAlert.setVisibility(View.GONE);
                //FileHelper.arrayOfUnits.get(index).setWarn(true);
            }

            return convertView;
        }
    }

    static public boolean isString2F(String str)
    {
        if(str.length() > 2) {

            if (str.substring(0, 2).equals("51")) {
                return true;
            }
        }

        return false;
    }

    static public boolean isString3F(String str)
    {
        if(str.length() > 2) {

            if (str.substring(0, 2).equals("52")) {
                return true;
            }
        }

        return false;
    }

    static public boolean isChemical(String str)
    {
        if(str.length() > 2)
        {
            if(isString2F(str))
            {
                return true;
            }

            if(isString3F(str))
            {
                return true;
            }
        }

        return false;
    }

    static public String getStringSerialChemical(String str)
    {
        String name = str;

        if(isChemical(str))
        {
            name = str.substring(0,8);
        }

        return name;
    }


    static public String getStringNameChemical(String str)
    {
        String name = str;

        if(isString2F(str))
        {
            name = "Chemical 2F";
        }

        if(isString3F(str))
        {
            name = "Chemical 3F";
        }

        return name;
    }

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(DeviceScanActivity.DEVICE_DATA_AVAILABLE); //added by ghosty
        intentFilter.addAction(BluetoothLeService.RSSI_DATA_AVAILABLE); //added by ghosty
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }

    public void clearUser(final int pos) {
        // Creating alert Dialog with one Button
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

        // Setting Dialog Title
        alertDialog.setTitle("Settings:");

        // Setting Dialog Message
        alertDialog.setMessage("Name: " + FileHelper.arrayOfUnits.get(pos).getCustomName());
        // Setting Icon to Dialog

        alertDialog.setIcon(R.drawable.tile);

        // Setting Positive "Yes" Button
        alertDialog.setPositiveButton("Clear user",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        setProcessig(CLEAR_USER,pos);
                        dialog.cancel();
                    }
                });
        //Setting Negative "NO" Button
        /*alertDialog.setNegativeButton("Disconnect",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Write your code here to execute after dialog
                        wantConnect = false;
                        if(mConnected)disconnectNotify();
                        dialog.cancel();
                    }
                });*/

        alertDialog.setNegativeButton("Top",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        setProcessig(TOP_USER,pos);
                        dialog.cancel();
                    }
                });

        // closed

        // Showing Alert Message
        alertDialog.show();
    }

    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if (keyCode == KeyEvent.KEYCODE_BACK)
        {
            //if(mConnected){disconnectNotify();}

            moveTaskToBack(true);
            return true; // return
        }

        return false;
    }

    public class unitTopUsersComparator implements Comparator<TypeUnits>
    {
        private final boolean trueLow;

        public unitTopUsersComparator(boolean trueLow) {
            this.trueLow = trueLow;
        }

        @Override
        public int compare(TypeUnits o1, TypeUnits o2) {
            return (o1.getStatusTop() ^ o2.getStatusTop()) ? ((o1.getStatusTop() ^ this.trueLow) ? 1 : -1) : 0;
        }
    }

    public void sortTopUsers()
    {
        Collections.sort(FileHelper.arrayOfUnits, new unitTopUsersComparator(true));
        listView.setAdapter(customAdapter);
        globalIndexOfActiveUser = 0;
        FileHelper.writeArrayToFile(FileHelper.arrayOfUnits, this);
    }


    public void startTimer() {
        timerRun = true;
        timer = new Timer();
        TimerTask timerTask = new TimerTask() {

            @Override
            public void run() {
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        obvolavani();
                    }
                });
            }
        };
        timer.scheduleAtFixedRate(timerTask, 0, 1000);
    }

    public void setGlobalTitleAdmin()
    {
        globalUserMode = ADMIN;
        FileHelper.writeToFile(String.valueOf(globalUserMode),readContext(),"userMode.txt");
        setTitleMode(globalUserMode);
        refreshMenu();
        listView.setAdapter(customAdapter);
    }

    public void insertPassword()
    {
        // Creating alert Dialog with one Button
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

        // Setting Dialog Title
        alertDialog.setTitle("Admin mode");

        // Setting Dialog Message
        alertDialog.setMessage("Insert password:");

        final EditText input = new EditText(this);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams
                (
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT
                );

        input.setLayoutParams(lp);
        alertDialog.setView(input); // uncomment this line

        // Setting Icon to Dialog
        alertDialog.setIcon(R.drawable.abc_ic_menu_paste_mtrl_am_alpha);

        // Setting Positive "Yes" Button
        alertDialog.setPositiveButton("Ok",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int which) {
                        // Write your code here to execute after dialog
                        //Toast.makeText(getApplicationContext(),"Device added", Toast.LENGTH_SHORT).show();
                        //Intent myIntent1 = new Intent(view.getContext(), MainActivity.class);
                        //startActivityForResult(myIntent1, 0);
                        //Toast.makeText(getApplicationContext(),input.getText().toString(), Toast.LENGTH_SHORT).show();

                        if(input.getText().toString().equals("ma123lina"))
                        {
                            setGlobalTitleAdmin();
                            Toast.makeText(getApplicationContext(),"Ok", Toast.LENGTH_SHORT).show();

                        }
                        else
                        {
                            Toast.makeText(getApplicationContext(),"Wrong password!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        alertDialog.show();
    }

    public void refreshMenu()
    {
       if((FileHelper.arrayOfUnits.size()) > 0 && (globalUserMode == USER))
        {
            flagAddIconMenuVisible = false;
            invalidateOptionsMenu();
        }
        else
       {
           flagAddIconMenuVisible = true;
           invalidateOptionsMenu();
       }

        listView.setAdapter(customAdapter);

    }

    public void obvolavani() {

        if(timerProcess > 0)
        {
            timerProcess--;
        }
        else
        {
            if(timerRun)mainProcess();
            timerProcess = 5;
        }

        //Toast.makeText(MainActivity.this, "T: " + timerProcess, Toast.LENGTH_SHORT).show();
    }

    public void nextAvailableUser()
    {
        if((globalIndexOfActiveUser++) < FileHelper.arrayOfUnits.size()-1){;}
        else{globalIndexOfActiveUser = 0;}
    }

    public void checkNextUser()
    {
       nextAvailableUser();

        boolean flagRepeat = true;

        // Finding online device, or device with timer offline left
        while(flagRepeat) {
            // Next device is offline?
           if (!FileHelper.arrayOfUnits.get(globalIndexOfActiveUser).getOnline()) {
                int statusTimer = FileHelper.arrayOfUnits.get(globalIndexOfActiveUser).getTimerTryConnectAfterOffline();

                if (++statusTimer < NUMBER_OFFLINE_JUMPS) {
                    FileHelper.arrayOfUnits.get(globalIndexOfActiveUser).setTimerTryConnectAfterOffline(statusTimer);
                    nextAvailableUser();
                } else {
                    FileHelper.arrayOfUnits.get(globalIndexOfActiveUser).setTimerTryConnectAfterOffline(0);
                    flagRepeat = false;
                }
            } else // Next device is online? - Nothing to do
            {
                FileHelper.arrayOfUnits.get(globalIndexOfActiveUser).setTimerTryConnectAfterOffline(0);
                flagRepeat = false;
            }
        }
    }

    public void mainProcess()
    {
        if (FileHelper.arrayOfUnits.size() > 0)
        {
            //Toast.makeText(MainActivity.this, String.valueOf(getActiveUnitState()), Toast.LENGTH_SHORT).show();

            switch(getActiveUnitState())
            {
                case PROC_FIRST_START:
                        bind();
                        break;

                case PROC_TRY_BIND: /**/  break;
                case PROC_BIND_OK:
                        counterRxClear();
                        updateViewBtSymbol(globalIndexOfActiveUser, BSEARCH);
                        register();
                        connect(globalIndexOfActiveUser);
                        break;

                case PROC_CONNECTED: /* Discovered - active notify enable */
                    break;

                case PROC_NOTIFY_ENABLED:
                    break;

                case PROC_NOTIFY_DISABLED:

                    mBluetoothLeService.disconnect();

                    break;

                case PROC_ONLINE_RXOK:

                break;

                case PROC_CLOSE:

                        if(getCounterRx() == 0)
                        {
                            if((++counterOffline) == NUMBER_TRY_CONNECT)
                            {
                                userUnitOffline(globalIndexOfActiveUser);
                                counterOffline = 0;
                            }
                            else
                            {
                                if(menuWasDisconnect)
                                {
                                    userUnitOffline(globalIndexOfActiveUser);
                                    menuWasDisconnect = false;

                                    BluetoothLeService.mTargetCharacteristic = null;
                                    invalidateOptionsMenu();
                                    clearUI();
                                    unRegister();
                                    unBind();
                                    mBluetoothLeService.myClose();
                                }


                                if(!FileHelper.arrayOfUnits.get(globalIndexOfActiveUser).getOnline())
                                {
                                    counterOffline = 0;
                                    updateViewBtSymbol(globalIndexOfActiveUser,BDISABLE);
                                }
                                //Toast.makeText(MainActivity.this, "Increment", Toast.LENGTH_SHORT).show();
                            }
                        }
                        else
                        {
                            counterOffline = 0;
                            updateViewBtSymbol(globalIndexOfActiveUser, BNONE);
                            setActiveUnitState(PROC_FIRST_START);
                        }

                        if(flagProcessing) //-------------------------------------------------------
                        {
                            if(wantToModeUser)
                            {
                                wantToModeUser = false;
                                globalIndexOfActiveUser = wantedUser;
                                bind();
                                updateViewBtSymbol(globalIndexOfActiveUser, BSEARCH);
                                flagProcessing = false;
                                invalidateOptionsMenu();
                            }

                            if(wantToSkipToMenu)
                            {
                                if(FileHelper.arrayOfUnits.get(globalIndexOfActiveUser).getOnline())
                                {
                                    updateViewBtSymbol(globalIndexOfActiveUser, BNONE);
                                }
                                else
                                {
                                    updateViewBtSymbol(globalIndexOfActiveUser, BDISABLE);
                                }

                                globalIndexOfActiveUser = wantedUser;
                                bind();
                                updateViewBtSymbol(globalIndexOfActiveUser, BSEARCH);
                            }

                            if(wantToSkipToScan)
                            {
                                wantToSkipToScan = false;
                                timerRun = false;
                                final Intent intent = new Intent(MainActivity.this, DeviceScanActivity.class); //default
                                startActivity(intent);
                            }

                            if(wantToTopUser) //----------------------------------------------------
                            {
                                wantToTopUser = false;
                                int i = 0;

                                // clear all top
                                while(i < FileHelper.arrayOfUnits.size())
                                {
                                    FileHelper.arrayOfUnits.get(i).setStatusTop(false);
                                    i++;
                                }

                                //set top
                                FileHelper.arrayOfUnits.get(wantedUser).setStatusTop(true);
                                sortTopUsers();
                                FileHelper.writeArrayToFile(FileHelper.arrayOfUnits, MainActivity.this);

                                flagProcessing = false;
                                invalidateOptionsMenu();

                                if(FileHelper.arrayOfUnits.size() > 0)
                                {
                                    setActiveUnitState(PROC_FIRST_START);
                                    globalIndexOfActiveUser = 0;
                                    bind();
                                    updateViewBtSymbol(0, BSEARCH);
                                }
                            }

                            if(wantToClearUser)//---------------------------------------------------
                            {
                                wantToClearUser = false;
                                FileHelper.arrayOfUnits.remove(wantedUser);
                                listView.setAdapter(customAdapter);
                                FileHelper.writeArrayToFile(FileHelper.arrayOfUnits, MainActivity.this);
                                isListEmptyShowInitDisp();

                                flagProcessing = false;
                                invalidateOptionsMenu();

                                if(FileHelper.arrayOfUnits.size() > 0)
                                {
                                    globalIndexOfActiveUser = 0;
                                    setActiveUnitState(PROC_FIRST_START);
                                    bind();
                                    updateViewBtSymbol(0, BSEARCH);
                                }
                            }
                        }
                        else // Not processing -----------------------------------------------------
                        {
                            if(!flagStillConnect)
                            {
                                if(counterOffline == 0)
                                {
                                    checkNextUser();
                                }
                            }
                            else
                            {
                                globalIndexOfActiveUser = 0;
                            }

                            setActiveUnitState(PROC_FIRST_START);
                            bind();
                            updateViewBtSymbol(globalIndexOfActiveUser, BSEARCH);
                        }
                break;
            }
        }
    }
}
