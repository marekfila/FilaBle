package fila.ble;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.support.v4.app.ActivityCompat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.Manifest;

import java.util.ArrayList;
import android.support.v4.content.ContextCompat;

/**
 * Activity for scanning and displaying available Bluetooth LE devices.
 */
public class DeviceScanActivity extends ListActivity {

    int MY_PERMISSIONS_REQUEST_ACCESS_COARSE = 1;

    private final static String TAG = DeviceScanActivity.class.getSimpleName();

    public final static String DEVICE_DATA_AVAILABLE =
            "ghostysoft.bleuuidexplorer.DEVICE_DATA_AVAILABLE";

    String customName = "";

    // [ghosty
    class deviceInfo {
        public String Name;
        public String Address;
        public Integer RSSI;
        public int Type,BondState;
        public byte[] scanRecord;
        public boolean isAdded;
    }

    AlertDialog alertDialog;

    private final static int MaxDeviceCount = 500; //Support Max 500 Bluetooth devices
    // ghosty]
    private LeDeviceListAdapter mLeDeviceListAdapter;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothDevice mBluetoothDevice;
    private boolean mScanning;
    private Handler mHandler;

    private static final int REQUEST_ENABLE_BT = 1;
    // Stops scanning after 10 seconds.
    private static final long SCAN_PERIOD = 5000;

    //ghosty
    private deviceInfo[] scanDevice=new deviceInfo[MaxDeviceCount];
    private Integer scanIndex=0;

    //ArrayList<TypeUnits> arrayOfUnits = new ArrayList<TypeUnits>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "*** onCreate()");
        super.onCreate(savedInstanceState);
        getActionBar().setTitle(R.string.title_activity_device_scan);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        mHandler = new Handler();

        // Use this check to determine whether BLE is supported on the device.  Then you can
        // selectively disable BLE-related features.
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_SHORT).show();
            finish();
        }

        // Initializes a Bluetooth adapter.  For API level 18 and above, get a reference to
        // BluetoothAdapter through BluetoothManager.
        final BluetoothManager bluetoothManager =  (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        // Checks if Bluetooth is supported on the device.
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, R.string.error_bluetooth_not_supported, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Checks if Bluetooth is supported on the device.
        if (mBluetoothAdapter.getState()==BluetoothAdapter.STATE_OFF) {
            Toast.makeText(this, R.string.error_bluetooth_not_enabled, Toast.LENGTH_LONG).show();
            finish();
            return;
        }
    }

     private boolean checkDuplicity(String address)
     {
         ArrayList<TypeUnits> arrayOfUnits3 = new ArrayList<TypeUnits>();

         try {

             arrayOfUnits3 = FileHelper.Init(this);


             for (int i = 0; i < arrayOfUnits3.size(); i++) {
                 if (arrayOfUnits3.get(i).getMacAddress().equals(address)) {
                     return true;
                 }
             }
         }
         catch (Exception e)
         {
             ;
         }

         return false;
     }

    public Context readContext()
    {
        return this;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d(TAG, "*** onCreateOptionsMenu()");
        getMenuInflater().inflate(R.menu.device_scan, menu);

        if (!mScanning) {
            //
             getActionBar().setTitle("Result:");
            menu.findItem(R.id.menu_stop).setVisible(false);
            menu.findItem(R.id.menu_scan).setVisible(true);
            //menu.findItem(R.id.menu_refresh).setActionView(null);
            // after stop scan restart
            //scanIndex = 0; //reset
            //mLeDeviceListAdapter.clear();
            //scanLeDevice(true);

            //Toast.makeText(DeviceScanActivity.this,EXTRAS_DEVICE_NAME.toString(), Toast.LENGTH_SHORT).show();

            /*final Intent intent = new Intent(DeviceScanActivity.this, MainActivity.class);
            //final Intent intent = new Intent(DeviceScanActivity.this, CharacteristicReadWriteActivity.class);
            intent.putExtra(MainActivity.EXTRAS_DEVICE_NAME, mBluetoothDevice.getName());
            intent.putExtra(MainActivity.EXTRAS_DEVICE_ADDRESS, mBluetoothDevice.getAddress());
            startActivity(intent);*/

        } else {
            getActionBar().setTitle("Scanning...");
            menu.findItem(R.id.menu_stop).setVisible(true);
            menu.findItem(R.id.menu_scan).setVisible(false);
            //menu.findItem(R.id.menu_refresh).setActionView(
              //      R.layout.actionbar_indeterminate_progress);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d(TAG, "*** onOptionsItemSelected()");
        switch (item.getItemId()) {
            case R.id.menu_scan:
                scanIndex = 0; //reset
                mLeDeviceListAdapter.clear();
                scanLeDevice(true);
                break;
            case R.id.menu_stop:
                scanLeDevice(false);
                break;

            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return true;
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "*** onResume()");
        super.onResume();

        // Ensures Bluetooth is enabled on the device.  If Bluetooth is not currently enabled,
        // fire an intent to display a dialog asking the user to grant permission to enable it.
        if (!mBluetoothAdapter.isEnabled()) {
            if (!mBluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
        }

        // Initializes list view adapter.
        mLeDeviceListAdapter = new LeDeviceListAdapter();
        setListAdapter(mLeDeviceListAdapter);
        Log.d(TAG, "LeDeviceListAdapter Created");

        if (ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED){
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION)) {
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                        MY_PERMISSIONS_REQUEST_ACCESS_COARSE);
            }
        }

        scanLeDevice(true);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "*** onActivityResult()");
        // User chose not to enable Bluetooth.
        if (requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_CANCELED) {
            finish();
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onPause() {
        Log.d(TAG, "*** onPause()");
        super.onPause();
        scanLeDevice(false);
        mLeDeviceListAdapter.clear();
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        Log.d(TAG, String.format("*** onListItemClick() position %d",position));

        mBluetoothDevice = mLeDeviceListAdapter.getDevice(position);
        if (mBluetoothDevice == null) return;

        if (mScanning) {
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
            mScanning = false;
        }

        BluetoothLeService.IsDualMode = (scanDevice[position].Type == BluetoothDevice.DEVICE_TYPE_DUAL) ? true : false; //Select Single or Dual Mode
        if (BluetoothLeService.IsDualMode) {
            Toast.makeText(getApplication(), "Dual Mode", Toast.LENGTH_LONG).show();
        }

        if(!scanDevice[position].isAdded)
        {
            editCustomName(v);
        }
        else
        {
            Toast.makeText(getApplicationContext(),"User is already added!", Toast.LENGTH_SHORT).show();
        }
    }

    public void editCustomName(final View view)
    {
        // Creating alert Dialog with one Button
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

        // Setting Dialog Title
        alertDialog.setTitle("Custom name");

        // Setting Dialog Message
        alertDialog.setMessage("Insert your custom name:");

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

                        AddUnit(mBluetoothDevice.getName(),mBluetoothDevice.getAddress(),input.getText().toString());

                        Toast.makeText(getApplicationContext(),"User added!", Toast.LENGTH_SHORT).show();


                        final Intent intent = new Intent(DeviceScanActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });

        alertDialog.show();
    }

    public int unitsVerify()
    {
        ArrayList<TypeUnits> arrayOfUnits2 = new ArrayList<TypeUnits>();

        int error = 0;

        arrayOfUnits2 = FileHelper.Init(this);

        if(arrayOfUnits2.size() > 0 && error == 0)
        {
            for (int i = 0; i < arrayOfUnits2.size(); i++)
            {
                if(arrayOfUnits2.get(i).getBleName().equals(FileHelper.arrayOfUnits.get(i).getBleName())){error = 0;}else{error = 1;break;}
                if(arrayOfUnits2.get(i).getCustomName().equals(FileHelper.arrayOfUnits.get(i).getCustomName())){error = 0;}else{error = 1;break;}
                if(arrayOfUnits2.get(i).getMacAddress().equals(FileHelper.arrayOfUnits.get(i).getMacAddress())){error = 0;}else{error = 1;break;}
            }

            if(error == 1)
            {
                Toast.makeText(DeviceScanActivity.this, "Error verify", Toast.LENGTH_SHORT).show();
            }
        }

        return error;
    }

    public void AddUnit(String name, String address,String strCustom )
    {
        TypeUnits unit = new TypeUnits(strCustom,name,address,"100");

        FileHelper.arrayOfUnits.add(unit);

        for(int i = 0; i < 3; i++) {

            FileHelper.writeArrayToFile(FileHelper.arrayOfUnits, this);

            if (unitsVerify() == 1)
            {
                Toast.makeText(DeviceScanActivity.this, "Error try: " +String.valueOf(i), Toast.LENGTH_SHORT).show();
            }
            else{
                    break;
            }
        }
    }

    private void scanLeDevice(final boolean enable) {
        Log.d(TAG, "*** scanLeDevice()");
        if (enable) {
            // Stops scanning after a pre-defined scan period.
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mScanning = false;
                    mBluetoothAdapter.stopLeScan(mLeScanCallback);
                    invalidateOptionsMenu();
                }
            }, SCAN_PERIOD);

            mScanning = true;
            mBluetoothAdapter.startLeScan(mLeScanCallback);
        } else {
            mScanning = false;
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
        }
        invalidateOptionsMenu();
    }

    // Adapter for holding devices found through scanning.
    private class LeDeviceListAdapter extends BaseAdapter {
        private ArrayList<BluetoothDevice> mLeDevices;
        private LayoutInflater mInflator;

        public LeDeviceListAdapter() {
            super();
            Log.d(TAG, "*** LeDeviceListAdapter.LeDeviceListAdapter()");
            mLeDevices = new ArrayList<BluetoothDevice>();
            mInflator = DeviceScanActivity.this.getLayoutInflater();
        }

        public void addDevice(BluetoothDevice device) {
            Log.d(TAG, "*** LeDeviceListAdapter.addDevice()");
            if(!mLeDevices.contains(device)) {
                mLeDevices.add(device);
            }
        }

        public BluetoothDevice getDevice(int position) {
            Log.d(TAG, "*** LeDeviceListAdapter.getDevice()");
            return mLeDevices.get(position);
        }

        public void clear() {
            Log.d(TAG, "*** LeDeviceListAdapter.clear()");
            mLeDevices.clear();
        }

        @Override
        public int getCount() {
            Log.d(TAG, "*** LeDeviceListAdapter.getCount()");
            return mLeDevices.size();
        }

        @Override
        public Object getItem(int i) {
            Log.d(TAG, "*** LeDeviceListAdapter.getItem()");
            return mLeDevices.get(i);
        }

        @Override
        public long getItemId(int i) {
            Log.d(TAG, "*** LeDeviceListAdapter.getItemId()");
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            Log.d(TAG, String.format("*** LeDeviceListAdapter.getView() i=%d",i));
            ViewHolder viewHolder;
            // General ListView optimization code.
            if (view == null) {
                view = mInflator.inflate(R.layout.device_scan, null);
                viewHolder = new ViewHolder();
                viewHolder.deviceAddress = (TextView) view.findViewById(R.id.device_address);
                if(scanDevice[i].isAdded){viewHolder.deviceAddress.setTextColor(Color.GRAY);}else{viewHolder.deviceAddress.setTextColor(Color.GREEN);}
                viewHolder.deviceName = (TextView) view.findViewById(R.id.device_name);
                viewHolder.deviceRSSI = (TextView) view.findViewById(R.id.scan_RSSI);
                //viewHolder.deviceType = (TextView) view.findViewById(R.id.device_Type);
                view.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) view.getTag();
            }

            String tempStringName = scanDevice[i].Name;

            boolean isCac = false;

            /*if(MainActivity.isString3F(scanDevice[i].Name))
            {
                tempStringName = "Chemical 3F Plus";
                isCac = true;
            }

            if(MainActivity.isString2F(scanDevice[i].Name))
            {
                tempStringName = "Chemical 2F Plus";
                isCac = true;
            }*/

            viewHolder.deviceName.setText(MainActivity.getStringNameChemical(scanDevice[i].Name));
            if(MainActivity.isChemical(scanDevice[i].Name)){viewHolder.deviceAddress.setText(scanDevice[i].Name.substring(0,8));}
            else{viewHolder.deviceAddress.setText(scanDevice[i].Address);}
            viewHolder.deviceRSSI.setText(String.format("%d dBm",scanDevice[i].RSSI));


            Resources res = getResources();
            ImageView unit = (ImageView) view.findViewById(R.id.scan_image);

            if (scanDevice[i].RSSI < -90 && scanDevice[i].RSSI >= -100)
            {
                unit.setImageDrawable(res.getDrawable(R.drawable.scan_far));
            }

            if (scanDevice[i].RSSI < -80 && scanDevice[i].RSSI >= -90)
            {
                unit.setImageDrawable(res.getDrawable(R.drawable.scan_middle));
            }

            if (scanDevice[i].RSSI >= -80)
            {
                unit.setImageDrawable(res.getDrawable(R.drawable.scan_close));
            }


            switch (scanDevice[i].Type) {
                case BluetoothDevice.DEVICE_TYPE_CLASSIC:
                    //viewHolder.deviceType.setText("Classic");
                    break;
                case BluetoothDevice.DEVICE_TYPE_LE:
                    //viewHolder.deviceType.setText("BLE");
                    break;
                case BluetoothDevice.DEVICE_TYPE_DUAL:
                    //viewHolder.deviceType.setText("Dual");
                    break;
                case BluetoothDevice.DEVICE_TYPE_UNKNOWN:
                    //viewHolder.deviceType.setText("Ukonown");
                    break;
                default:
                    //viewHolder.deviceType.setText("Not defined");
                    break;
            }

            switch (scanDevice[i].BondState) {
                case BluetoothDevice.BOND_NONE:
                    //viewHolder.deviceBoundState.setText("BondNone");
                    break;
                case BluetoothDevice.BOND_BONDED:
                    //viewHolder.deviceBoundState.setText("Bonded");
                    break;
                case BluetoothDevice.BOND_BONDING:
                    //viewHolder.deviceBoundState.setText("Bonding");
                    break;
            }

            // Show the scan result
             //final StringBuilder strScanResult = new DataManager().byteArrayToHex(scanDevice[i].scanRecord);
             //Log.d(TAG, String.format("scan result %s", strScanResult.toString()));
             //viewHolder.deviceScanResult.setText(strScanResult);

            // Explain the scan  result, see BLUETOOTH SPECIFICATION Version 4.0 [Vol 3]
            // Part C. Generic Access Profile. Section 18 -  APPENDIX C

            final StringBuilder strEIR = new StringBuilder();
            int p=0;

            while (p<scanDevice[i].scanRecord.length) {
                byte fieldLength = scanDevice[i].scanRecord[p];
                byte fieldType =  scanDevice[i].scanRecord[p+1];
                Integer  uuid16;
                Log.d(TAG, String.format(" scan field ptr=%d, length=%d, type = 0x%02X", p, fieldLength, fieldType));
                switch (fieldType) {
                    case (byte)0x01: //Flags
                        byte flags = scanDevice[i].scanRecord[p+2];
                        strEIR.append(String.format("[0x01] Flags: 0x%02X\n",flags));
                        if ((flags & 0x01)>0) {
                            strEIR.append("    b0:LE Limited Discoverable Mode\n");
                        }
                        if ((flags & 0x02)>0) {
                            strEIR.append("     b1:LE General Discoverable Mode\n");
                        }
                        if ((flags & 0x04)>0) {
                            strEIR.append("     b2:BR/EDR Not Supported\n");
                        }
                        if ((flags & 0x08)>0) {
                            strEIR.append("     b3:Controller\n");
                        }
                        if ((flags & 0x10)>0) {
                            strEIR.append("     b4:Host\n");
                        }
                        if ((flags & 0x20)>0) {
                            strEIR.append("     b5:Reserved\n");
                        }
                        if ((flags & 0x40)>0) {
                            strEIR.append("     b6:Reserved\n");
                        }
                        if ((flags & 0x80)>0) {
                            strEIR.append("     b7:Reserved\n");
                        }
                        break;

                    case (byte)0x11: //Security Flags
                        byte secFlags = scanDevice[i].scanRecord[p+2];
                        strEIR.append(String.format("[0x11] Security Manager OOB Flags [0x%02X]\n",secFlags));
                        if ((secFlags & 0x01)>0) {
                            strEIR.append("     b0:OOB data present\n");
                        }
                        if ((secFlags & 0x02)>0) {
                            strEIR.append("     b1:LE supported\n");
                        }
                        if ((secFlags & 0x04)>0) {
                            strEIR.append("     b2:Host\n");
                        }
                        if ((secFlags & 0x08)>0) {
                            strEIR.append("     b3:Random address\n");
                        } else {
                            strEIR.append("     b3:Public address\n");
                        }
                        break;

                    default:; break;
                }
                p += (fieldLength+1);
            }
            return view;
        }
    }

    // Device scan callback.
    private BluetoothAdapter.LeScanCallback mLeScanCallback =
            new BluetoothAdapter.LeScanCallback() {

                @Override
                public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
                    Log.d(TAG,  String.format("*** BluetoothAdapter.LeScanCallback.onLeScan(): *RSSI=%d",rssi));
                    // check if the device is scanned in the device list
                    for (int i=0; i < scanIndex; i++) {
                        if (scanDevice[i].Address.equals(device.getAddress())) {
                            Log.d(TAG,  String.format("*** BluetoothAdapter.LeScanCallback.onLeScan(): * skip address %s",scanDevice[i].Address));
                            return;
                        }
                    }

                    final String deviceName = device.getName();
                    scanDevice[scanIndex]= new deviceInfo();
                    if (deviceName != null && deviceName.length() > 0) {
                        scanDevice[scanIndex].Name = deviceName;
                    }  else {
                        scanDevice[scanIndex].Name = "unknown device";
                    }
                    scanDevice[scanIndex].Address = device.getAddress();
                    scanDevice[scanIndex].RSSI = rssi;
                    scanDevice[scanIndex].Type = device.getType();
                    scanDevice[scanIndex].BondState = device.getBondState();

                    if(checkDuplicity(scanDevice[scanIndex].Address)){scanDevice[scanIndex].isAdded = true;}
                    else{scanDevice[scanIndex].isAdded = false;}


                    if(scanDevice[scanIndex].Type != BluetoothDevice.DEVICE_TYPE_LE){return;}

                    // Search for actual packet length
                    int packetLength=0;
                    while (scanRecord[packetLength]>0 && packetLength<scanRecord.length) {
                        packetLength += scanRecord[packetLength]+1;
                    }
                    scanDevice[scanIndex].scanRecord = new byte[packetLength];
                    System.arraycopy (scanRecord,0,scanDevice[scanIndex].scanRecord,0,packetLength);
                    Log.d(TAG, String.format("*** Scan Index=%d, Name=%s, Address=%s, RSSI=%d, scan result length=%d",
                            scanIndex, scanDevice[scanIndex].Name,scanDevice[scanIndex].Address,scanDevice[scanIndex].RSSI, packetLength ));
                    scanIndex++;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Log.d(TAG, "*** BluetoothAdapter.LeScanCallback.runOnUiThread()");
                            mLeDeviceListAdapter.addDevice(device);
                            mLeDeviceListAdapter.notifyDataSetChanged();
                        }
                    });
                }
            };

    static class ViewHolder {
        TextView deviceName;
        TextView deviceAddress;
        TextView deviceRSSI;
        //TextView deviceType;
    }
}