package fila.ble;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.bluetooth.BluetoothGattCharacteristic;
import android.widget.Toast;
import android.widget.ImageButton;
import android.view.Window;
import android.view.ViewGroup;
import java.util.List;
import android.app.Dialog;
import android.widget.RadioGroup.OnCheckedChangeListener;
import java.util.ArrayList;
import android.widget.RadioGroup;
import android.widget.RadioButton;
import android.widget.ImageView;
import android.os.Vibrator;
import android.os.Build;
import android.os.VibrationEffect;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.AdapterView;
import android.widget.SeekBar;

import android.net.Uri;


public class activity_led_control extends Activity {

    boolean debugFlagPanelInfo = false;
    boolean debugFlagMotorView = false;

    boolean wasFlagProcessing = false;
    boolean flagProcessing = false;

    private final static String TAG = activity_led_control.class.getSimpleName();
    //private Button mButtonNotify, mButtonMotorOn,mButtonMotorOff;
    //private ImageView butDown,butUp;
    //private TextView tvMac,tvUBat,tvIMot,tvRmot,tvFlow,diagFlow,tvTBar,tvPBar,mRSSI,mCmd,mBleName,mCustomName,mSerial;//tvBarStandard,tvBarFilter;
    //ImageButton ibTimerFilterReset;
    //private TextView tvAirFlowRange,mSession,mCumulative,textFlowSettings,textBrightness,textRotation,tvFlowRpm,tvBarFlow,tvDefaultFilterTimer;
    private boolean mNotify;
    //LinearLayout cmdDevider,lockStandardLayout,lockStandardLayoutDevider,standardLayout,motorDevider,filterLayout,languageLayout,flowRpmLayout,flowRpmLayoutDevider;
    //LinearLayout rotationLayout, resetLayout, brightnessLayout,brightnessLayoutDevider,textChangeCustomNameLayout,timerFilterLayout,timerFilterLayout2,alarmLayout,diagnosticLayout;
    //LinearLayout manualLayout,datasheetlayout,declarationLayout,signupLayout;
    //private TextView textStandard, textFilter, textLanguage,textLockStandard,tvFilterTimer,textTimerFilter,tvNextService;

    // Warning
    //LinearLayout warnDeviderLayout, warnBatteryLayout, warnRegulationOlLayout,warnServiceLayout,warnDateLayout,warnFilterTimerLayout,warnAllLayout;
    //LinearLayout warnCriticalLayout, warnBatteryOffLayout;

    private BluetoothGattCharacteristic mNotifyCharacteristic, mIndicateCharacteristic; //used for Notification
    private ImageView imageViewBars;

    UartProtocol data1 = new UartProtocol();

    ProgressBar prFlow;
    ProgressBar prFilter;
    ProgressBar prBattery;

    boolean flagStart;

    SeekBar brightness;
    TextView lumn;
    TextView spotreba_okamzita;
    Button btnOn, btnOff, btnDis, tlac5, tlac6, tlac7,tlac8,tlac9;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, String.format("*** onCreate()"));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_led_control);

        Resources res = getResources();
        ImageView unit = (ImageView) findViewById(R.id.imageViewUnit);

        if (MainActivity.isString3F(MainActivity.globalDeviceName))
        {unit.setImageDrawable(res.getDrawable(R.drawable.unit3f));}


        //call the widgtes
        tlac5 = (Button)findViewById(R.id.button5);
        tlac6 = (Button)findViewById(R.id.button6);
        tlac7 = (Button)findViewById(R.id.button7);
        tlac8 = (Button)findViewById(R.id.button8);
        tlac9 = (Button)findViewById(R.id.button9);
        btnOn = (Button)findViewById(R.id.button2);
        btnOff = (Button)findViewById(R.id.button3);
        btnDis = (Button)findViewById(R.id.button4);
        brightness = (SeekBar)findViewById(R.id.seekBar);
        lumn = (TextView)findViewById(R.id.lumn);
        spotreba_okamzita = (TextView)findViewById(R.id.spotreba_okamzita);



        //commands to be sent to bluetooth
        tlac5.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //turnOnLed();      //method to turn on
                data1.cmdMarekSetValue(0x33);
                brightness.setProgress(0x33);
            }
        });
        tlac6.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //turnOnLed();      //method to turn on
                data1.cmdMarekSetValue(0x66);
                brightness.setProgress(0x66);
            }
        });
        tlac7.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //turnOnLed();      //method to turn on
                data1.cmdMarekSetValue(0x9a);
                brightness.setProgress(0x9a);

            }
        });
        tlac8.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //turnOnLed();      //method to turn on
                data1.cmdMarekSetValue(0xcd);
                brightness.setProgress(0xcd);
            }
        });
        tlac9.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //turnOnLed();      //method to turn on
                data1.cmdMarekSetValue(0xfe);
                brightness.setProgress(0xfe);
            }
        });



        btnOn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //turnOnLed();      //method to turn on
                data1.cmdMarekSetValue(0xfe);
            }
        });

        btnOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                //turnOffLed();   //method to turn off
                data1.cmdMarekSetValue(0x00);
            }
        });

        brightness.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {


                if (fromUser==true)
                {

                   // lumn.setText(Integer.toHexString(progress));
                    try
                    {
                        //btSocket.getOutputStream().write(String.valueOf(progress).getBytes());
                        //Toast.makeText(activity_led_control.this, String.valueOf(progress), Toast.LENGTH_SHORT).show();
                        data1.cmdMarekSetValue(progress);

                    }
                    catch (Exception e)
                    {

                    }
                }
            }


            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }



        });



        /*warnCriticalLayout = (LinearLayout) findViewById(R.id.warnCriticalLayout);
        warnBatteryOffLayout = (LinearLayout) findViewById(R.id.warnBatteryOffLayout);
        warnDeviderLayout = (LinearLayout) findViewById(R.id.warnDeviderLayout);
        warnAllLayout = (LinearLayout) findViewById(R.id.warnAllLayout);
        warnBatteryLayout = (LinearLayout) findViewById(R.id.warnBatteryLayout);
        warnRegulationOlLayout = (LinearLayout) findViewById(R.id.warnRegulationOlLayout);
        warnFilterTimerLayout = (LinearLayout) findViewById(R.id.warnFilterTimerLayout);
        warnServiceLayout = (LinearLayout) findViewById(R.id.warnServiceLayout);
        warnDateLayout = (LinearLayout) findViewById(R.id.warnDateLayout);*/

        // BAR ----------------------------------------------------

        //tvBarStandard = (TextView)findViewById(R.id.tvBarStandard);
        //tvBarFilter = (TextView)findViewById(R.id.tvBarFilter);

        // DIAGNOSTIC ---------------------------------------------
        /*tvMac = (TextView) findViewById(R.id.tvMac);
        tvUBat = (TextView) findViewById(R.id.tvUBat);
        tvIMot = (TextView) findViewById(R.id.tvImot);
        tvRmot = (TextView) findViewById(R.id.tvRmot);
        tvFlow = (TextView) findViewById(R.id.tvFlow);
        diagFlow = (TextView) findViewById(R.id.diagFlow);
        tvTBar = (TextView) findViewById(R.id.tvTBar);
        tvPBar = (TextView) findViewById(R.id.tvPBar);

        tvFlow.setVisibility(View.GONE);
        diagFlow.setVisibility(View.GONE);*/
        //---------------------------------------------------------

        /*mRSSI = (TextView) findViewById(R.id.CharRW_RSSI);
        mCmd = (TextView) findViewById(R.id.tvCmd);

        mBleName = (TextView) findViewById(R.id.textViewBleName);
        mCustomName = (TextView) findViewById(R.id.textViewCustomName);
        mSerial = (TextView) findViewById(R.id.textViewSerial);

        tvAirFlowRange = (TextView) findViewById(R.id.tvAirFlowRange);
        mCumulative = (TextView) findViewById(R.id.tvCumulativeValue);
        mSession = (TextView) findViewById(R.id.tvSessionValue);
        tvFilterTimer = (TextView) findViewById(R.id.tvFilterTimer);
        textTimerFilter = (TextView) findViewById(R.id.textTimerFilter);
        tvNextService = (TextView) findViewById(R.id.tvNextService);

        tvDefaultFilterTimer = (TextView) findViewById(R.id.tvDefaultFilterTimer);

        mBleName.setText(MainActivity.getStringNameChemical(MainActivity.globalDeviceName));
        mCustomName.setText(MainActivity.globalCustomName);
        mSerial.setText("S/N: " + MainActivity.getStringSerialChemical(MainActivity.globalDeviceName));

        mButtonNotify = (Button) findViewById(R.id.CharRW_btnNotify);
        mButtonMotorOn = (Button) findViewById(R.id.motorOn);
        mButtonMotorOff = (Button) findViewById(R.id.motorOff);

        butDown = (ImageView) findViewById(R.id.btn_down);
        butUp = (ImageView) findViewById(R.id.btn_up);*/
        //imageViewBars = (ImageView) findViewById(R.id.imageViewBars);

        /*ibTimerFilterReset = (ImageButton) findViewById(R.id.ibTimerFilterReset);
        manualLayout = (LinearLayout) findViewById(R.id.manualLayout);
        datasheetlayout = (LinearLayout) findViewById(R.id.datasheetlayout);
        declarationLayout = (LinearLayout) findViewById(R.id.declarationLayout);
        signupLayout = (LinearLayout) findViewById(R.id.signupLayout);

        tvBarFlow = (TextView) findViewById(R.id.tvBarFlow);
        textStandard = (TextView) findViewById(R.id.textStandard);
        textLockStandard = (TextView) findViewById(R.id.textLockStandard);
        textFilter = (TextView) findViewById(R.id.textFilter);
        tvFlowRpm = (TextView) findViewById(R.id.tvFlowRpm);
        textLanguage = (TextView) findViewById(R.id.textLanguage);
        textFlowSettings = (TextView) findViewById(R.id.tvFlowSettings);
        textLockStandard = (TextView) findViewById(R.id.textLockStandard);

        textBrightness = (TextView) findViewById(R.id.textBrightness);
        textRotation = (TextView) findViewById(R.id.textRotation);*/

        mNotify = false;

        /*cmdDevider = (LinearLayout) findViewById(R.id.cmdDevider);
        resetLayout = (LinearLayout) findViewById(R.id.resetLayout);
        lockStandardLayout = (LinearLayout) findViewById(R.id.lockStandardLayout);
        lockStandardLayoutDevider = (LinearLayout) findViewById(R.id.lockStandardLayoutDevider);
        standardLayout = (LinearLayout) findViewById(R.id.standardLayout);
        filterLayout = (LinearLayout) findViewById(R.id.filterLayout);
        flowRpmLayout = (LinearLayout) findViewById(R.id.flowRpmLayout);
        flowRpmLayoutDevider = (LinearLayout) findViewById(R.id.flowRpmLayoutDevider);
        diagnosticLayout = (LinearLayout) findViewById(R.id.diagnosticLayout);
        alarmLayout = (LinearLayout) findViewById(R.id.alarmLayout);

        languageLayout = (LinearLayout) findViewById(R.id.languageLayout);
        rotationLayout = (LinearLayout) findViewById(R.id.rotationLayout);
        brightnessLayout = (LinearLayout) findViewById(R.id.brightnessLayout);
        brightnessLayoutDevider = (LinearLayout) findViewById(R.id.brightnessLayoutDevider);
        textChangeCustomNameLayout = (LinearLayout) findViewById(R.id.textChangeCustomNameLayout);
        timerFilterLayout = (LinearLayout) findViewById(R.id.timerFilterLayout);
        timerFilterLayout2 = (LinearLayout) findViewById(R.id.filterTimerLayout2);
        motorDevider = (LinearLayout) findViewById(R.id.motorDevider);*/

        /*if (!debugFlagPanelInfo) {
            ((TextView) findViewById(R.id.textView6)).setVisibility(View.GONE);
            mRSSI.setVisibility(View.GONE);
            mCmd.setVisibility(View.GONE);
            cmdDevider.setVisibility(View.GONE);
        }*/

        //mButtonNotify.setVisibility(View.GONE);

        // Warnings
        /*warnCriticalLayout.setVisibility(View.GONE);
        warnBatteryOffLayout.setVisibility(View.GONE);
        warnDeviderLayout.setVisibility(View.GONE);
        warnAllLayout.setVisibility(View.GONE);
        warnBatteryLayout.setVisibility(View.GONE);
        warnRegulationOlLayout.setVisibility(View.GONE);
        warnFilterTimerLayout.setVisibility(View.GONE);
        warnServiceLayout.setVisibility(View.GONE);
        warnDateLayout.setVisibility(View.GONE);*/

        /*if(MainActivity.globalUserMode != 2){

            // invisible:
            lockStandardLayout.setVisibility(View.GONE);
            lockStandardLayoutDevider.setVisibility(View.GONE);
        }*/

        /*if(MainActivity.globalUserMode == 0){
            diagnosticLayout.setVisibility(View.GONE);
        }*/

        /*flowRpmLayoutDevider.setVisibility(View.GONE);
        flowRpmLayout.setVisibility(View.GONE);

        if(!debugFlagMotorView && (MainActivity.globalUserMode != 2))
        {
            mButtonMotorOn.setVisibility(View.GONE);
            mButtonMotorOff.setVisibility(View.GONE);
            motorDevider.setVisibility(View.GONE);
        }*/

        MainActivity.mBluetoothLeService.readCharacteristic(BluetoothLeService.mTargetCharacteristic);

        try
        {
            final int charaProp = BluetoothLeService.mTargetCharacteristic.getProperties();
            Log.d(TAG,String.format("charaProp=%08x",charaProp));

            //Toast.makeText(MenuUnit.this, "Object error", Toast.LENGTH_SHORT).show();

            if ((charaProp & BluetoothGattCharacteristic.PROPERTY_READ) > 0) {
                //mButtonRead.setEnabled(true);
            /*mButtonRead.setOnClickListener(new Button.OnClickListener(){
                @Override
                public void onClick(View v) {
                    Toast.makeText(MenuUnit.this, "Read", Toast.LENGTH_SHORT).show();
                    if ( MainActivity.mConnected) { //Check connection state before READ
                        MainActivity.mBluetoothLeService.readCharacteristic(BluetoothLeService.mTargetCharacteristic);
                    }
                }
            });*/
            } else {
                //mButtonRead.setEnabled(false);
            } // READ

            /*if ((charaProp & BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
                mButtonNotify.setEnabled(true);
                mButtonNotify.setOnClickListener(new Button.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        enableNotify();
                    }

                });
            } else {
                mButtonNotify.setEnabled(false);
            } //NOTIFY
*/


            if ((charaProp & BluetoothGattCharacteristic.PROPERTY_INDICATE) > 0) {
            /*mButtonIndicate.setEnabled(true);
            mButtonIndicate.setOnClickListener(new Button.OnClickListener(){
                @Override
                public void onClick(View v) {
                    if (BluetoothLeService.mTargetCharacteristic != null) {
                        // if ( MainActivity.mConnected) { //Check connection state before READ
                        if (mIndicateCharacteristic == null) {
                            // Enable Notify
                            Toast.makeText(MenuUnit.this, "Indicate Enabled", Toast.LENGTH_SHORT).show();
                            mIndicateCharacteristic = BluetoothLeService.mTargetCharacteristic;
                            MainActivity.mBluetoothLeService.setCharacteristicIndication(mIndicateCharacteristic, true);
                            MainActivity.mBluetoothLeService.readCharacteristic(mIndicateCharacteristic); //read once to start indicate?
                            mButtonIndicate.setTextColor(Color.RED);
                            mNotify = true;
                            //mButtonRead.setEnabled(false);
                        } else {
                            // Disable Notify
                            Toast.makeText(MenuUnit.this, "Indicate Disabled", Toast.LENGTH_SHORT).show();
                            MainActivity.mBluetoothLeService.setCharacteristicNotification(mIndicateCharacteristic, false);
                            mIndicateCharacteristic = null;
                            mButtonIndicate.setTextColor(Color.BLACK);
                            mIndicate = false;
                            //mButtonRead.setEnabled(true);
                        }
                        //}
                    }
                }

            });*/
            } else {
                //mButtonIndicate.setEnabled(false);
            } //Indicate

            if (((charaProp & BluetoothGattCharacteristic.PROPERTY_WRITE) > 0) ||
                    (charaProp & BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE) > 0) {

                /*mButtonMotorOn.setOnClickListener(new Button.OnClickListener(){
                    @Override
                    public void onClick(View v) {

                        data1.cmdMotorOn();
                        //flagSend = true;


                    }

                });

                mButtonMotorOff.setOnClickListener(new Button.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        data1.cmdMotorOff();
                    }

                });

                butDown.setOnClickListener(new Button.OnClickListener(){
                    @Override
                    public void onClick(View v) {

                        int val;

                        if(data1.getmaxPresetValues() > 0)
                        {
                            if(data1.getPresetSetPointIdx() >1 )
                            {
                                val = data1.getPresetSetPointIdx();
                                val--;
                                setAndDisplayRpmFlow(val);
                                vibration();
                            }
                        }
                    }

                });

                butUp.setOnClickListener(new Button.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        int val;

                        if(data1.getmaxPresetValues() > 0)
                        {
                            if(data1.getPresetSetPointIdx() < data1.getmaxPresetValues())
                            {
                                val = data1.getPresetSetPointIdx();
                                val++;
                                setAndDisplayRpmFlow(val);
                                vibration();
                            }
                        }
                    }

                });

                resetLayout.setOnClickListener(new Button.OnClickListener(){
                    @Override
                    public void onClick(View v) {

                    factoryReset();

                    }

                });

                standardLayout.setOnClickListener(new Button.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        if(checkInitStandardLock()) {
                            showDialogTableStandard(arrayOfStandard);
                        }
                        else
                        {
                            Toast.makeText(MenuUnit.this, "Wait for init", Toast.LENGTH_SHORT).show();
                        }
                    }

                });

                lockStandardLayout.setOnClickListener(new Button.OnClickListener(){
                    @Override
                    public void onClick(View v) {

                        if(data1.getiRotation() > 3)
                        {
                            Toast.makeText(MenuUnit.this, "Wait for init", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            showDialogTableStandardLock(arrayOfStandard);
                        }
                    }

                });

                filterLayout.setOnClickListener(new Button.OnClickListener(){
                    @Override
                    public void onClick(View v) {

                        if(checkInitFilterLock()) {
                            showDialogTableFilter(arrayOfFilter);
                        }
                        else
                        {
                            Toast.makeText(MenuUnit.this, "Wait for init", Toast.LENGTH_SHORT).show();
                        }

                    }

                });

                tvBarFlow.setOnClickListener(new Button.OnClickListener(){
                    @Override
                    public void onClick(View v) {

                        if(data1.getiRotation() > 3)
                        {
                            Toast.makeText(MenuUnit.this, "Wait for init", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            //Toast.makeText(MenuUnit.this, data1.getStrArray(), Toast.LENGTH_SHORT).show();
                            showRadioButtonDialogFlowRpm();
                            vibration();
                        }

                    }

                });

                languageLayout.setOnClickListener(new Button.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        if(data1.getiRotation() > 3)
                        {
                            Toast.makeText(MenuUnit.this, "Wait for init", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            showDialogTableLanguage(arrayOfLanguages);
                        }

                    }

                });

                rotationLayout.setOnClickListener(new Button.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        if(data1.getiRotation() > 3)
                        {
                            Toast.makeText(MenuUnit.this, "Wait for init", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            showRadioButtonRotation();
                        }
                    }

                });

                alarmLayout.setOnClickListener(new Button.OnClickListener(){
                    @Override
                    public void onClick(View v) {

                        if(data1.getiRotation() > 3)
                        {
                            Toast.makeText(MenuUnit.this, "Wait for init", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            data1.cmdMelody();
                        }

                    }

                });

                brightnessLayout.setOnClickListener(new Button.OnClickListener(){
                    @Override
                    public void onClick(View v) {

                        if(data1.getiRotation() > 3)
                        {
                            Toast.makeText(MenuUnit.this, "Wait for init", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            showRadioButtonBrightness();
                        }

                    }

                });

                textChangeCustomNameLayout.setOnClickListener(new Button.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        editCustomName(MainActivity.globalSerial);
                    }

                });

                timerFilterLayout.setOnClickListener(new Button.OnClickListener(){
                    @Override
                    public void onClick(View v) {

                        if(data1.getiRotation() > 3)
                        {
                            Toast.makeText(MenuUnit.this, "Wait for init", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            showDialogTableTimerFilter();
                        }

                    }

                });

                timerFilterLayout2.setOnClickListener(new Button.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        if(data1.getiRotation() > 3)
                        {
                            Toast.makeText(MenuUnit.this, "Wait for init", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            showDialogTableTimerFilter();
                        }

                    }

                });



                ibTimerFilterReset.setOnClickListener(new Button.OnClickListener(){
                    @Override
                    public void onClick(View v) {

                        if(data1.getDefaultTimerFilter() != 30 )
                        {
                            timerReset();
                        }
                        else
                        {
                            Toast.makeText(MenuUnit.this, "Wait for init", Toast.LENGTH_SHORT).show();
                        }
                    }

                });

                manualLayout.setOnClickListener(new Button.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        openWebURL("https://www.clean-air.cz/wp-content/uploads/2019/09/User-manual-CNA-051-R03-CleanAIR-Chemical-2F-DIGI.pdf");
                    }

                });

                datasheetlayout.setOnClickListener(new Button.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        openWebURL("https://www.clean-air.cz/wp-content/uploads/2019/09/DA-002-51-00-00FCA-Chemical-2F-Plus-EN.pdf");
                    }

                });

                declarationLayout.setOnClickListener(new Button.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        openWebURL("https://www.clean-air.cz/wp-content/uploads/2018/09/PS-510000-1-R0-Chemical-2F-Plus.pdf");

                    }

                });

                signupLayout.setOnClickListener(new Button.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(MenuUnit.this, "Not yet implemented", Toast.LENGTH_SHORT).show();
                    }

                });*/

            }

            // Auto turn on read data from BL modul
            /*if(mButtonNotify.isEnabled()) {
                enableNotify();
            }*/

            flagStart = true;
        }
        catch (Exception e)
        {
            finish();
        }

        /*prFlow = (ProgressBar)findViewById(R.id.progressBarFlow2);
        prFilter = (ProgressBar)findViewById(R.id.progressBarFilter2);
        prBattery = (ProgressBar)findViewById(R.id.progressBarBat2);

        //Resources res = getResources();
        prFlow.setProgressDrawable(res.getDrawable( R.layout.greenprogress));
        prFilter.setProgressDrawable(res.getDrawable( R.layout.orangeprogress));
        prBattery.setProgressDrawable(res.getDrawable( R.layout.redprogress));

        //tvBarFlow.setText(FileHelper.arrayOfUnits.get(MainActivity.globalIndexOfActiveUser).getFlowLiter());
        prFlow.setProgress(FileHelper.arrayOfUnits.get(MainActivity.globalIndexOfActiveUser).getFlow());
        prFilter.setProgress(FileHelper.arrayOfUnits.get(MainActivity.globalIndexOfActiveUser).getFilter());
        prBattery.setProgress(FileHelper.arrayOfUnits.get(MainActivity.globalIndexOfActiveUser).getBattery());*/

        switch(MainActivity.globalUserMode) {
            case 0:
                setTitle("Menu - user");
                //mySetVisible(0);
                break;
            case 1:
                setTitle("Menu - advanced user");
                //mySetVisible(1);
                break;
            case 2:
                setTitle("Menu - admin");
                //mySetVisible(2);
                break;
        }

        Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        float ratioX = ((float) display.getWidth());

        //Toast.makeText(getApplicationContext(),String.valueOf(ratioX), Toast.LENGTH_SHORT).show();
        if(ratioX >= 1200 || MainActivity.flagTestResolutionDisp)
        {
            RelativeLayout.LayoutParams paramsBut = (RelativeLayout.LayoutParams)imageViewBars.getLayoutParams();
            paramsBut.leftMargin = 330;

            //LinearLayout.LayoutParams paramsChangeText = (LinearLayout.LayoutParams)textChangeCustomNameLayout.getLayoutParams();
            //paramsChangeText.width = 500;

            //Toast.makeText(getApplicationContext(),"Scale", Toast.LENGTH_SHORT).show();
        }

        getActionBar().setDisplayHomeAsUpEnabled(true);
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


    public void openWebURL(String url) {
        Intent browse = new Intent( Intent.ACTION_VIEW , Uri.parse( url ) );
        startActivity( browse );
        //Toast.makeText(MenuUnit.this, "Is internet available?", Toast.LENGTH_SHORT).show();
    }

    public void setAndDisplayRpmFlow(int val)
    {
        data1.cmdUpDown(val);
        data1.set_flow(val);
        //tvBarFlow.setText(String.valueOf(data1.getStatusFlowRpm()));
        //if(data1.getRegulMode() == 1){tvBarFlow.setTextSize(getResources().getDimension(R.dimen.rpmtextsize));}
        //else{tvBarFlow.setTextSize(getResources().getDimension(R.dimen.flowtextsize));}
    }

    public Context readContext()
    {
        return this;
    }

    public void timerReset() {
        // Creating alert Dialog with one Button
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

        // Setting Dialog Title
        alertDialog.setTitle("Timer Reset");

        // Setting Dialog Message
        alertDialog.setMessage("Confirm timer reset?");
        // Setting Icon to Dialog

        alertDialog.setIcon(R.drawable.ic_alert);

        // Setting Positive "Yes" Button
        alertDialog.setPositiveButton("CANCEL",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {


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

        alertDialog.setNegativeButton("CONFIRM",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        data1.cmdTimerFilter(data1.getDefaultTimerFilter());


                        //data1.test();
                        dialog.cancel();
                    }
                });

        // closed

        // Showing Alert Message
        alertDialog.show();
    }

    public void factoryReset() {
        // Creating alert Dialog with one Button
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

        // Setting Dialog Title
        alertDialog.setTitle("Device factory reset");

        // Setting Dialog Message
        alertDialog.setMessage("Confirm device factory reset?");
        // Setting Icon to Dialog

        alertDialog.setIcon(R.drawable.ic_alert);

        // Setting Positive "Yes" Button
        alertDialog.setPositiveButton("CANCEL",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.cancel();
                    }
                });

        alertDialog.setNegativeButton("CONFIRM",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        data1.cmdFactoryReset();
                        dialog.cancel();
                    }
                });

        alertDialog.show();
    }

    public void editCustomName(final String serial)
    {
        // Creating alert Dialog with one Button
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

        // Setting Dialog Title
        alertDialog.setTitle("Custom name");

        // Setting Dialog Message
        alertDialog.setMessage("Change custom name:");

        final EditText input = new EditText(this);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams
                (
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT
                );

        input.setLayoutParams(lp);
        input.setText(MainActivity.globalCustomName);
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

                        int i = 0;
                        while(i < FileHelper.arrayOfUnits.size())
                        {
                            if(FileHelper.arrayOfUnits.get(i).getMacAddress().equals(serial))
                            {
                                if(input.length()>32)
                                {
                                    Toast.makeText(getApplicationContext(),"Too long - Max 32 chars!", Toast.LENGTH_SHORT).show();
                                    break;
                                }

                                FileHelper.arrayOfUnits.get(i).setCustomName(input.getText().toString());
                                FileHelper.writeArrayToFile(FileHelper.arrayOfUnits,readContext());
                                Toast.makeText(getApplicationContext(),"Name changed", Toast.LENGTH_SHORT).show();
                                MainActivity.globalCustomName = FileHelper.arrayOfUnits.get(i).getCustomName();
                                //mCustomName.setText(MainActivity.globalCustomName);

                                        break;
                            }

                            i++;
                        }


                    }
                });

        alertDialog.show();
    }

    /*public void initFilterLock()
    {
        int pole_filter[] = data1.getArrayOfCombinationFilter();
        int j = 0;
        for(table i: arrayOfFilter)
        {
            arrayOfFilter.get(j).lock = false;

            for (int q = 0; q < pole_filter.length; q++) {
                if (i.index == pole_filter[q]) {
                    arrayOfFilter.get(j).lock = true;
                }
            }
            j++;
        }
    }*/

    /*public void initStandardLock()
    {
        int pole_standard[] = data1.getArrayOfCombinationStandard();
        int j = 0;

        for(table i: arrayOfStandard)
        {
            arrayOfStandard.get(j).lock = false;

            for (int q = 0; q < pole_standard.length; q++) {
                if (i.index == pole_standard[q]) {
                    arrayOfStandard.get(j).lock = true;
                }
            }
            j++;
        }
    }

    public boolean checkInitStandardLock()
    {
        initStandardLock();

        for (table i: arrayOfStandard) {
            if(i.lock == true){return true;}
        }

        return false;
    }

    public boolean checkInitFilterLock()
    {
        initFilterLock();

        for (table i: arrayOfFilter) {
            if(i.lock == true){return true;}
        }

        return false;
    }

    public void setStnadard(String name) {

        data1.clearArrayOfCombinationStandatdAndFilter();

        textFilter.setText(arrayOfFilter.get(0).name);

        for (int x = 0; x < arrayOfStandard.size(); x++) {
            if (name.equals(arrayOfStandard.get(x).name)) {
                if (arrayOfStandard.get(x).lock) {
                    //Toast.makeText(MenuUnit.this, String.valueOf(arrayOfStandard.get(x).index), Toast.LENGTH_SHORT).show();
                    textStandard.setText(arrayOfStandard.get(x).name);

                    ImageView imageViewStand = (ImageView)findViewById(R.id.imageStandard);
                    imageViewStand.setImageDrawable(arrayOfStandard.get(x).image);

                    data1.cmdStandard((byte) arrayOfStandard.get(x).index);
                    return;
                }
            }
        }
    }

    public void setStnadardLock(String name) {

        // clear data from last check lock
        data1.clearArrayOfCombinationStandatdAndFilter();

        for (int x = 0; x < arrayOfStandard.size(); x++) {
            if (name.equals(arrayOfStandard.get(x).name)) {

                Toast.makeText(MenuUnit.this, String.valueOf(arrayOfStandard.get(x).name), Toast.LENGTH_SHORT).show();
                textLockStandard.setText(arrayOfStandard.get(x).name);

                ImageView imageViewLockS = (ImageView)findViewById(R.id.imageLockStandard);
                imageViewLockS.setImageDrawable(arrayOfStandard.get(x).image);

                data1.cmdSetStandardLock((byte)arrayOfStandard.get(x).index);
                return;
            }
        }
    }

    public void setBrightnessRotation(int b, int r)
    {
        data1.cmdBrightnessRotation(b,r);
    }

    public void mySetVisible(int mode)
    {
        switch(mode)
        {
            case 0:
                brightnessLayout.setVisibility(View.GONE);
                brightnessLayoutDevider.setVisibility(View.GONE);
                break;

            case 1:
                brightnessLayout.setVisibility(View.VISIBLE);
                brightnessLayoutDevider.setVisibility(View.VISIBLE);
                break;

            case 2:
                brightnessLayout.setVisibility(View.VISIBLE);
                brightnessLayoutDevider.setVisibility(View.VISIBLE);
                break;
        }
    }

    public void setFilter(int i)
    {
        if (arrayOfFilter.get(i).lock) {
            textFilter.setText(arrayOfFilter.get(i).name);

            ImageView imageViewFilter = (ImageView)findViewById(R.id.imageFilter);
            imageViewFilter.setImageDrawable(arrayOfFilter.get(i).image);

            data1.cmdSetFilter(i+1);
            }
    }

    public void setLanguages(int i)
    {
        textLanguage.setText(arrayOfLanguages.get(i).name);
        ImageView imageViewFlags = (ImageView)findViewById(R.id.ivFlags);
        imageViewFlags.setImageDrawable(arrayOfLanguages.get(i).image);
        data1.cmdLanguage(i+1);
    }

    public void setTimerFilter(int i)
    {
        data1.cmdTimerFilter(i);
        //Toast.makeText(MenuUnit.this, "Data send, but not implemented!", Toast.LENGTH_SHORT).show();
    }



    private void showRadioButtonBrightness() {

        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.radiobutton_tabler);
        dialog.setTitle("Brightness:");

        ArrayList<table> arrayObject = new ArrayList<table>();

        for(int i=1; i < 5; i++)
        {
            arrayObject.add(new table(String.valueOf(i*25)+"%",i,null,false,null));
        }

        listView = (ListView)dialog.findViewById(R.id.listViewR);
        customAdapter = new CustomAdapter(arrayObject,"TimerFilter");
        listView.setAdapter(customAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                setBrightnessRotation(i+1,data1.getiRotation());
                textBrightness.setText(String.valueOf((i+1)*25) + "%");
                dialog.cancel();

            }
        });

        dialog.show();
    }

    private void showRadioButtonRotation (){

         final Dialog dialog = new Dialog(this);
         dialog.setContentView(R.layout.radiobutton_tabler);
         dialog.setTitle("Rotation:");

         ArrayList<table> arrayObject = new ArrayList<table>();

         for(int i=0; i < 4; i++)
         {
            arrayObject.add(new table(String.valueOf(i*90)+"°",i,null,false,null));
         }

         listView = (ListView)dialog.findViewById(R.id.listViewR);
         customAdapter = new CustomAdapter(arrayObject,"TimerFilter");
         listView.setAdapter(customAdapter);

         listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                    setBrightnessRotation(data1.getiBrightness(),i);
                    textRotation.setText(data1.getRotation());
                    dialog.cancel();

                }
         });

         dialog.show();
    }

    public String chooseFlowRpm(int data)
    {
        String str = "";

        if(data > 5000)
        {str = String.valueOf(data)+ " rpm";}
        else{str = String.valueOf(data/10)+ " l/min";}

        return str;
    }

    private void showRadioButtonDialogFlowRpm() {

        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.radiobutton_tabler);
        dialog.setTitle("Set flow/rpm:");

        ArrayList<table> arrayObject = new ArrayList<table>();

        int array[] = new int[10];
        array = data1.getArrayPreset();

        for(int i=0; i < 10; i++)
        {
            if(array[i] > 0)
            {
                arrayObject.add(new table(chooseFlowRpm(array[i]),i,null,false,null));
            }

        }

        for(int i = 0; i < dataFilterTimer.length; i++)
        {

        }

        listView = (ListView)dialog.findViewById(R.id.listViewR);
        customAdapter = new CustomAdapter(arrayObject,"TimerFilter");
        listView.setAdapter(customAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                setAndDisplayRpmFlow(i+1);
                dialog.cancel();

            }
        });

        dialog.show();
    }

    public int crcCals(byte[] array)
    {
        int crc = 0;

        for (int i = 0; i < (array.length) - 2; i++)
        {
            crc += array[i];
        }

        return crc;
    }

    public void setLaguage(int pos)
    {
        byte[] value = {0x6E,0x21,0x02,0x03,0x22,0x00,0x00};

        value[2] = (byte)pos;

        int crc = crcCals(value);
        int delka = value.length;
        int crcL = crc * 0xFF;
        int crcH = (crc>>8) * 0xFF;
        value[delka - 2] = (byte)crcL;
        value[delka-1] = (byte)crcH;

        MainActivity.mBluetoothLeService.writeCharacteristic(BluetoothLeService.mTargetCharacteristic, value);
    }*/

    public void enableNotify()
    {
        if (BluetoothLeService.mTargetCharacteristic != null) {
            // if ( MainActivity.mConnected) { //Check connection state before READ
            if (mNotifyCharacteristic == null) {
                // Enable Notify
                //Toast.makeText(CharacteristicReadWriteActivity.this, "Notify Enabled", Toast.LENGTH_SHORT).show();
                mNotifyCharacteristic = BluetoothLeService.mTargetCharacteristic;
                MainActivity.mBluetoothLeService.setCharacteristicNotification(mNotifyCharacteristic, true);
                MainActivity.mBluetoothLeService.readCharacteristic(mNotifyCharacteristic); //read once to start notify?
                //mButtonNotify.setTextColor(Color.GREEN);
                mNotify = true;
            } else {
                // Disable Notify
                Toast.makeText(activity_led_control.this, "Notify Disabled", Toast.LENGTH_SHORT).show();
                MainActivity.mBluetoothLeService.setCharacteristicNotification(mNotifyCharacteristic, false);
                mNotifyCharacteristic = null;
                //mButtonNotify.setTextColor(Color.BLACK);
                mNotify = false;

            }
            //}
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        if (MainActivity.mBluetoothLeService != null) {
            final boolean result = MainActivity.mBluetoothLeService.connect(MainActivity.globalSerial);
            Log.d(TAG, "Connect request result=" + result);
        }

        flagStart = true;
        //data1.cmdReadStandard();
    }

    /*public void initDisp()
    {
        prFlow.setProgress(10);
        prFilter.setProgress(data1.getStatusFilter());
        prBattery.setProgress(data1.getStatusBattery());

        mCumulative.setText(data1.getRuntime());
        mSession.setText(data1.getRuntimeSession());

        Toast.makeText(MenuUnit.this, "Init disp", Toast.LENGTH_SHORT).show();
    }*/

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mGattUpdateReceiver);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
       // Inflate the menu; this adds items to the action bar if it is present
        Log.d(TAG, String.format("*** onCreateOptionMenu()"));
       getMenuInflater().inflate(R.menu.led_control_menu, menu);

        //menu.findItem(R.id.menu_connect).setActionView(R.layout.actionbar_indeterminate_progress);

     /*   if(flagProcessing)
        {
            menu.findItem(R.id.menu_refresh).setVisible(true);
        }
        else
        {
            menu.findItem(R.id.menu_refresh).setVisible(false);
        }*/

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        Log.d(TAG, String.format("*** onOptionsItemSelected %d",id));

        switch (item.getItemId()) {

            case android.R.id.home:
               // onBackPressed();
                finish();
                return true;

            case R.id.menu_led_spotreba:
                  dialogSpotreba();
                //Toast.makeText(MenuUnit.this, MainActivity.version, Toast.LENGTH_LONG).show();

                return true;
            /*case R.id.menu_user:
                userMode = 0;
                setTitle("Menu - user");
                mySetVisible(userMode);
                lockStandardLayout.setVisibility(View.GONE);
                return true;

            case R.id.menu_advancedUser:
                userMode = 1;
                setTitle("Menu - advanced user");
                mySetVisible(userMode);
                lockStandardLayout.setVisibility(View.GONE);
                return true;

            case R.id.menu_admin:
                userMode = 2;
                setTitle("Menu - admin");
                mySetVisible(userMode);
                lockStandardLayout.setVisibility(View.VISIBLE);
                return true;*/
        }

        return super.onOptionsItemSelected(item);
    }

               //dialog vložení spotřeby

    public void dialogSpotreba()
    {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

        // Setting Dialog Title
        alertDialog.setTitle("Spotřeba svítidla");

        // Setting Dialog Message
        alertDialog.setMessage("Zadejte spotřebu svítidla:");

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
                        Toast.makeText(getApplicationContext(),"Spotřeba zadána!"+ " "+input.getText() , Toast.LENGTH_SHORT).show();
                        spotreba_okamzita.setText(input.getText().toString());
                    }
                });

        alertDialog.show();



    }

    // Handles various events fired by the Service.
    // ACTION_GATT_CONNECTED: connected to a GATT server.
    // ACTION_GATT_DISCONNECTED: disconnected from a GATT server.
    // ACTION_GATT_SERVICES_DISCOVERED: discovered GATT services.
    // ACTION_DATA_AVAILABLE: received data from the device.  This can be a result of read
    //                        or notification operations.
    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
                MainActivity.menuWasDisconnect = true;
                finish();
            } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
                displayRxData(intent.getByteArrayExtra(BluetoothLeService.EXTRA_DATA));
            } else if (BluetoothLeService.RSSI_DATA_AVAILABLE.equals(action)) {
                displayRSSI(intent.getStringExtra(BluetoothLeService.EXTRA_DATA));
            }
            Log.d(TAG, "BroadcastReceiver.onReceive():action="+action);
        }
    };

    /*private void showWarnings(int warn)
    {
        switch(warn)
        {
            case 0: warnDeviderLayout.setVisibility(View.GONE);
                    warnAllLayout.setVisibility(View.GONE);
                    break;

            default:    warnDeviderLayout.setVisibility(View.VISIBLE);
                        warnAllLayout.setVisibility(View.VISIBLE);

                        if((warn & 1) == 1){warnCriticalLayout.setVisibility(View.VISIBLE);} // 0 - Critical Device Error
                        else{warnCriticalLayout.setVisibility(View.GONE);}

                        if((warn & 2) == 2){warnBatteryOffLayout.setVisibility(View.VISIBLE);} // 1 - Battery Off
                        else{warnBatteryOffLayout.setVisibility(View.GONE);}

                        if((warn & 4) == 4){warnBatteryLayout.setVisibility(View.VISIBLE);} // 2
                        else{warnBatteryLayout.setVisibility(View.GONE);}

                        if((warn & 8) == 8){warnRegulationOlLayout.setVisibility(View.VISIBLE);} // 3
                        else{warnRegulationOlLayout.setVisibility(View.GONE);}

                        if((warn & 16) == 16){;} // 4 - Reserved
                        else{;}

                        if((warn & 32) == 32){warnDateLayout.setVisibility(View.VISIBLE);} // 5
                        else{warnDateLayout.setVisibility(View.GONE);}

                        if((warn & 64) == 64){warnFilterTimerLayout.setVisibility(View.VISIBLE);} // 6
                        else{warnFilterTimerLayout.setVisibility(View.GONE);}

                        if((warn & 128) == 128){warnServiceLayout.setVisibility(View.VISIBLE);} // 7
                        else{warnServiceLayout.setVisibility(View.GONE);}

                        //Toast.makeText(MenuUnit.this, String.valueOf(warn), Toast.LENGTH_SHORT).show();
                        vibration();

                        break;
        }
    }*/

    private void displayRxData(byte[] data) {
        if (data != null) {

            final StringBuilder strData = new StringBuilder();

            String readProcessData = data1.process(data);

            if(readProcessData.equals("CRC ok")) {

                try {

                    data1.dataFlagReceive();
                    //Log.d(TAG, "RxData format HEX");
                    for (byte byteChar : data)
                        strData.append(String.format("%02X ", byteChar));
                    //Log.d(TAG, "RxData format HEX");
                    //for(byte byteChar : data)

                    //if(data[4] == 0x60)
                    //if((data[6] & 0xFF) == 0x41)
                    //mCmd.setText(strData);

                    if(data1.getProgressStatusFlow()>0)
                    {
                        prFlow.setProgress(data1.getProgressStatusFlow());
                        prFilter.setProgress(data1.getStatusFilter());
                        prBattery.setProgress(data1.getStatusBattery());
                    }

                    //mCumulative.setText(data1.getRuntime());
                    //mSession.setText(data1.getRuntimeSession());
                    //tvFilterTimer.setText(data1.getRuntimeFilter());

                    if(data1.getWarings() == 0)
                    {
                        FileHelper.arrayOfUnits.get(MainActivity.globalIndexOfActiveUser).setWarn(false);
                    }
                    else
                    {
                        FileHelper.arrayOfUnits.get(MainActivity.globalIndexOfActiveUser).setWarn(true);
                    }

                    /*showWarnings(data1.getWarings());

                    if(data1.getDefaultTimerFilter() == 0)
                    {
                        MainActivity.globalDefaultFilterTimer = dataFilterTimer[(data1.getDefaultTimerFilter())];
                        tvDefaultFilterTimer.setText(dataFilterTimer[(data1.getDefaultTimerFilter())]);
                        textTimerFilter.setText(MainActivity.globalDefaultFilterTimer);
                    }
                    else
                    {
                        if(data1.getDefaultTimerFilter() != 30)
                        {
                            MainActivity.globalDefaultFilterTimer = dataFilterTimer[(data1.getDefaultTimerFilter())] + "h";
                            tvDefaultFilterTimer.setText(dataFilterTimer[(data1.getDefaultTimerFilter())]+"h");
                            textTimerFilter.setText(MainActivity.globalDefaultFilterTimer);
                        }
                   }

                    tvNextService.setText(data1.getRuntimeService());

                    int array[] = new int[10];
                    array = data1.getArrayPreset();

                    if(data1.getmaxPresetValues() > 0)
                    {
                        tvAirFlowRange.setText(chooseFlowRpm(array[0]) + " - " + chooseFlowRpm(array[data1.getmaxPresetValues()-1]));
                    }

                    textFlowSettings.setText(String.valueOf(data1.getStatusFlowRpm()));
                    if(!(data1.getStatusFlowRpm()).isEmpty())
                    {tvBarFlow.setText(String.valueOf(data1.getStatusFlowRpm()));}

                    textStandard.setText(arrayOfStandard.get(data1.getiStandard()-1).name);
                    //tvBarStandard.setText(arrayOfStandard.get(data1.getiStandard()-1).name);

                    textLockStandard.setText(arrayOfStandard.get(data1.getiStandardLock()-1).name);
                    textLanguage.setText(arrayOfLanguages.get(data1.getiLanguage()-1).name);

                    ImageView imageViewFlags = (ImageView)findViewById(R.id.ivFlags);
                    imageViewFlags.setImageDrawable(arrayOfLanguages.get(data1.getiLanguage()-1).image);


                    ImageView imageViewLockS = (ImageView)findViewById(R.id.imageLockStandard);
                    imageViewLockS.setImageDrawable(arrayOfStandard.get(data1.getiStandardLock()-1).image);

                    ImageView imageViewStand = (ImageView)findViewById(R.id.imageStandard);
                    imageViewStand.setImageDrawable(arrayOfStandard.get(data1.getiStandard()-1).image);

                    ImageView imageViewFilter = (ImageView)findViewById(R.id.imageFilter);
                    imageViewFilter.setImageDrawable(arrayOfFilter.get(data1.getiFilter()-1).image);


                    textFilter.setText(arrayOfFilter.get(data1.getiFilter()-1).name);
                    //tvBarFilter.setText(arrayOfFilter.get(data1.getiFilter()-1).name);

                    if(data1.getRegulMode() == 1){tvFlowRpm.setText("Rpm:");}
                    else{tvFlowRpm.setText("Flow:");}

                    //Display
                    textBrightness.setText(data1.getBrightness());
                    textRotation.setText(data1.getRotation());

                    // Diagnostic
                    tvMac.setText(String.valueOf(MainActivity.globalSerial));
                    tvUBat.setText(String.valueOf(data1.getUBaterry()) + " mV");
                    tvIMot.setText(String.valueOf(data1.getIMot()) + " mA");
                    tvRmot.setText(String.valueOf(data1.getRmot()) + " rpm");
                    tvFlow.setText(String.valueOf(data1.getFlow()) + " l/min");
                    tvTBar.setText(String.valueOf(data1.getTBar()) + " °C");
                    tvPBar.setText(String.valueOf(data1.getPBar()) + " hPa");*/
                } catch (Exception e) {
                    //e.printStackTrace();
                    //finish();
                }
            }
            else {;}
        }
        else
        {
            //mRxData.setText("");
        }
    }

    /*public void checkWaiting()
    {
        if(data1.getiRotation() < 4) {flagProcessing = false;}
        else{flagProcessing = true;if(!wasFlagProcessing){invalidateOptionsMenu();wasFlagProcessing = true;}return;}

        if(checkInitFilterLock()) {flagProcessing = false;}
        else{flagProcessing = true;if(!wasFlagProcessing){invalidateOptionsMenu();wasFlagProcessing = true;}return;}

        if(checkInitStandardLock()){flagProcessing = false;}
        else{flagProcessing = true;if(!wasFlagProcessing){invalidateOptionsMenu();wasFlagProcessing = true;}return;}

        wasFlagProcessing = false;
        invalidateOptionsMenu();
    }*/

    private void displayRSSI(String data) {

        if (data != null) {
            //mRSSI.setText(" " + data + " dBm");

            //if(flagStart){data1.setCmdInit();flagStart = false;}

            //data1.cmdSend();

            //checkWaiting();

        } else {
            //mRSSI.setText(R.string.no_data);
        }
    }

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        //intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        //intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        intentFilter.addAction(BluetoothLeService.RSSI_DATA_AVAILABLE); //ghosty
        return intentFilter;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if (keyCode == KeyEvent.KEYCODE_BACK)
        {
            //Toast.makeText(MenuUnit.this, "Finish", Toast.LENGTH_SHORT).show();
            finish();
        }

        return super.onKeyDown(keyCode, event);
    }

    /*private void showDialogTableTimerFilter() {

        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.radiobutton_tabler);
        dialog.setTitle("Set timer filter [hour]:");

        ArrayList<table> arrayObject = new ArrayList<table>();

        for(int i = 0; i < dataFilterTimer.length; i++)
        {
            arrayObject.add(new table(dataFilterTimer[i],i,null,false,null));
        }

        listView = (ListView)dialog.findViewById(R.id.listViewR);
        customAdapter = new CustomAdapter(arrayObject,"TimerFilter");
        listView.setAdapter(customAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {


                if(i == 0)
                {
                    tvDefaultFilterTimer.setText(dataFilterTimer[i]);
                }
                else
                {
                    tvDefaultFilterTimer.setText(dataFilterTimer[i] + "h");
                }

                MainActivity.globalDefaultFilterTimer = dataFilterTimer[i];
                setTimerFilter(i);
                dialog.cancel();

            }
        });

        dialog.show();
    }

    private void showDialogTableLanguage(final ArrayList<table> arrayObject) {

        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.radiobutton_tabler);
        dialog.setTitle("Language:");

        listView = (ListView)dialog.findViewById(R.id.listViewR);
        customAdapter = new CustomAdapter(arrayObject,"Language");
        listView.setAdapter(customAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                  setLanguages(i);
                dialog.cancel();

            }
        });

        dialog.show();
    }

    private void showDialogTableStandardLock(final ArrayList<table> arrayObject) {

        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.radiobutton_tabler);
        dialog.setTitle("StandardLock:");

        listView = (ListView)dialog.findViewById(R.id.listViewR);
        customAdapter = new CustomAdapter(arrayObject,"StandardLock");
        listView.setAdapter(customAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                setStnadardLock(customAdapter.myArrayObject.get(i).name);
                dialog.cancel();

            }
        });

        dialog.show();
    }

    private void showDialogTableStandard(final ArrayList<table> arrayObject) {

        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.radiobutton_tabler);
        dialog.setTitle("Standard:");

        listView = (ListView)dialog.findViewById(R.id.listViewR);
        customAdapter = new CustomAdapter(arrayObject,"Standard");
        listView.setAdapter(customAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                setStnadard(arrayObject.get(i).name);
                dialog.cancel();

            }
        });

        dialog.show();
    }

    private void showRadioButtonDialogStandard() {

        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        //TextView title = (TextView)dialog.findViewById(R.id.radioTextTile);
        //title.setText("Standard:");

        //ImageView icon = (ImageView)dialog.findViewById(R.id.radioImageIcon);
        //Resources res = getResources();
        //icon.setImageDrawable(res.getDrawable(R.drawable.set_ico_bar));

        //dialog.setFeatureDrawableResource(Window.FEATURE_LEFT_ICON, R.drawable.add_ico_bar);
        dialog.setContentView(R.layout.radiobutton_standard);
        //if(userMode == 2){dialog.setTitle("StandardLock:");}
        //else{dialog.setTitle("Standard:");}
        //dialog.setTitle("Standard:");


        final List<String> stringList=new ArrayList<>();  // here is list

        if(MainActivity.isString2F(MainActivity.globalDeviceName))
        {
            for(table i: arrayOfStandard)
            {
                if(i.unit.equals("2F") ||  i.unit.equals("Both"))
                    stringList.add(i.name);
            }
        }
        else // 3F
        {
            for(table i: arrayOfStandard)
            {
                if(i.unit.equals("3F") ||  i.unit.equals("Both"))
                    stringList.add(i.name);
            }
        }

        RadioGroup rg = (RadioGroup) dialog.findViewById(R.id.radio_group);

        for(int i=0;i<stringList.size();i++){
            RadioButton rb = new RadioButton(this); // dynamically creating RadioButton and adding to RadioGroup.
            rb.setText(stringList.get(i));

            for(int x = 0; x < arrayOfStandard.size(); x++)
            {
                if(stringList.get(i).equals(arrayOfStandard.get(x).name))
                {
                    if(!arrayOfStandard.get(x).lock) {rb.setTextColor(Color.GRAY);}
                }
            }

            rg.addView(rb);
        }

        // Select first choice
        //RadioButton r = (RadioButton) rg.getChildAt(rg.getChildCount()-1);
        //r.setChecked(true);

        rg.setOnCheckedChangeListener(new OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                for (int i = 0; i < group.getChildCount(); i++) {
                    if (group.getChildAt(i).getId() == checkedId) {
                        setStnadard(stringList.get(i));
                    }
                }
                dialog.cancel();
            }
        });

        dialog.show();
    }

    private void showDialogTableFilter(final ArrayList<table> arrayObject) {

        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.radiobutton_tabler);
        dialog.setTitle("Filter:");

        listView = (ListView)dialog.findViewById(R.id.listViewR);
        customAdapter = new CustomAdapter(arrayObject,"Filter");
        listView.setAdapter(customAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                setFilter(i);
                dialog.cancel();

            }
        });

        dialog.show();
    }*/


    /*class CustomAdapter extends BaseAdapter {

        ArrayList<table> myArrayObject = new ArrayList<table>();
        String modeMain;

        CustomAdapter(ArrayList<table> arrayObject,String mode)
        {
            modeMain = mode;

            if(mode.equals("StandardLock") || mode.equals("Standard"))//----------------------------
                {
                for(int i = 0; i < arrayObject.size(); i++) {

                    if (MainActivity.isString2F(MainActivity.globalDeviceName)) {

                        //Toast.makeText(MenuUnit.this, mBleName.getText().toString(), Toast.LENGTH_SHORT).show();

                        if (arrayOfStandard.get(i).unit.equals("2F") || arrayOfStandard.get(i).unit.equals("Both")) {
                            myArrayObject.add(arrayObject.get(i));
                            //Toast.makeText(MenuUnit.this, "2F", Toast.LENGTH_SHORT).show();
                        }
                    } else // 3F
                    {
                        if (arrayOfStandard.get(i).unit.equals("3F") || arrayOfStandard.get(i).unit.equals("Both")) {
                            myArrayObject.add(arrayObject.get(i));
                            //Toast.makeText(MenuUnit.this, "3F", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }

            if(mode.equals("Language") || mode.equals("Filter") || mode.equals("") || modeMain.equals("TimerFilter"))//----------------------------------
            {
                myArrayObject = arrayObject;
            }
        }

        @Override
        public int getCount() {
            return myArrayObject.size();
        }

        @Override
        public Object getItem(int position) {
            return myArrayObject.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }*/

        /*@Override
        public View getView(int position, View convertView, ViewGroup parent) {

            convertView = getLayoutInflater().inflate(R.layout.sample_listr, null);

            TextView name = (TextView) convertView.findViewById(R.id.view_name);
            ImageView image = (ImageView) convertView.findViewById(R.id.view_image);

            name.setText(myArrayObject.get(position).name);
            image.setImageDrawable(myArrayObject.get(position).image);

            if(modeMain.equals("Standard"))
            {
                if(!myArrayObject.get(position).lock)
                {
                    name.setTextColor(Color.GRAY);
                }
            }

            if(modeMain.equals("Filter"))
            {
                if(!myArrayObject.get(position).lock)
                {
                    name.setTextColor(Color.GRAY);
                }
            }

            return convertView;
        }
    }*/
}

