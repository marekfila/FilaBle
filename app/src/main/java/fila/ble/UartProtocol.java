package fila.ble;

import java.lang.String;

public class UartProtocol {

    private int uBattery = 0;
    private double iMot = 0;
    private int rMot;
    private double flow;
    private double tempBar;
    private double presBar;
    private int warnings = 0;

    private String userString = "not defined";
    private byte[] bUserString = new byte[16];

    private String statusFlow;
    private int statusProgressFlow;
    private  int regulMode; // 0 - Flow, 1 - Rpm
    private int statusFilter;
    private int statusBattery;
    private int cmdStatus;
    private int indexPreset;
    private String runtime;
    private String runtimeService = "- - -";
    private String runtimeTimerFilter;
    private String runtimeSession;
    int presetSetPointIdx = 0;
    private int iBrightness = 5; // Default
    private int iRotation = 4; // Default
    private int iLanguage;
    private int iStandard;
    private int iStandardLock;
    private int defaultTimerFilter = 30;
    private int iFilter;
    boolean flagRx;
    byte bStandardLock;
    int standardCombination[] = new int[7];
    int filterCombination[] = new int[7];

    int arrayPressets[] = new int[10];

    int maxPresetValues;

    String strArray = "";

    public void initGet()
    {
        statusFlow = "";
        statusFilter = 0;
        statusBattery = 0;
        cmdStatus = 0;
    }

    public int[] getArrayOfCombinationStandard()
    {
        return new int[]{ //1,2,3,4,5,7,0};
                standardCombination[0],
                standardCombination[1],
                standardCombination[2],
                standardCombination[3],
                standardCombination[4],
                standardCombination[5],
                standardCombination[6]};
    }

    public int[] getArrayOfCombinationFilter()
    {
        return new int[]{ //1,2,3,4,5,7,0};
                filterCombination[0],
                filterCombination[1],
                filterCombination[2],
                filterCombination[3],
                filterCombination[4],
                filterCombination[5],
                filterCombination[6]};
    }

    public void clearArrayOfCombinationStandatdAndFilter()
    {
        standardCombination[0] = 0;
        standardCombination[1] = 0;
        standardCombination[2] = 0;
        standardCombination[3] = 0;
        standardCombination[4] = 0;
        standardCombination[5] = 0;
        standardCombination[6] = 0;

        filterCombination[0] = 0;
        filterCombination[1] = 0;
        filterCombination[2] = 0;
        filterCombination[3] = 0;
        filterCombination[4] = 0;
        filterCombination[5] = 0;
        filterCombination[6] = 0;
    }

    public int[] getArrayPreset()
    {
        return arrayPressets;
    }

    public int getWarings()
    {
        return warnings;
    }

    public int getDefaultTimerFilter(){return defaultTimerFilter;}

    public String getUserString()
    {
        return userString;
    }

    public int getUBaterry()
    {
        return uBattery;
    }

    public int getRegulMode()
    {
        return regulMode;
    }

    public double getIMot()
    {
        return iMot;
    }

    public int getRmot()
    {
        return rMot;
    }

    public double getFlow()
    {
        return flow;
    }

    public double getPBar()
    {
        return presBar;
    }

    public double getTBar()
    {
        return tempBar;
    }


    public void setCmdInit()
    {
        cmdStatus = 14;
    }
    public int getiFilter()
    {
        return iFilter;
    }
    public int getiStandard()
    {
        return iStandard;
    }

    public String getRotation()
    {
        String str = "-";

        switch(iRotation)
        {
            case 0: str = "0°";break;
            case 1: str = "90°";break;
            case 2: str = "180°";break;
            case 3: str = "270°";break;
            default: str = "-";break;
        }

        return str;
    }

    public String getBrightness()
    {
        String str = "-";

        switch(iBrightness)
        {
            case 1: str = "25%";break;
            case 2: str = "50%";break;
            case 3: str = "75%";break;
            case 4: str = "100%";break;
            default: str = "-";break;
        }

        return str;
    }

    public int getiRotation()
    {
        return iRotation;
    }
    public int getiBrightness()
    {
        return iBrightness;
    }
    public int getiStandardLock()
    {
        return iStandardLock;
    }
    public int getiLanguage()
    {
        return iLanguage;
    }
    public String getStatusFlowRpm()
    {
        return statusFlow;
    }

    public int setProgressStatusFlow(int val)
    {
        statusProgressFlow = (100/maxPresetValues)*val;

        return statusProgressFlow;
    }

    public int getProgressStatusFlow()
    {
        maxPresetValues = 0;

        for(int i = 0; i<10; i++)
        {
            if(arrayPressets[i] > 0){
                maxPresetValues++;
            }
        }

        if(maxPresetValues == 0){return 0;}
        statusProgressFlow = (100/maxPresetValues)*presetSetPointIdx;

        return statusProgressFlow;
    }
    public int getPresetSetPointIdx()
    {
        return presetSetPointIdx;
    }

    public int getmaxPresetValues()
    {
        return maxPresetValues;
    }

    public String getStrArray()
    {
        return strArray;
    }

    public int getStatusFilter()
    {
        return statusFilter;
    }
    public int getStatusBattery()
    {
        return statusBattery;
    }
    public String getRuntime()
    {
        return runtime;
    }
    public String getRuntimeService()
    {
        return runtimeService;
    }
    public String getRuntimeFilter()
    {
        return runtimeTimerFilter;
    }
    public String getRuntimeSession()
    {
        return runtimeSession;
    }
    private String secondsToString(int pTime) {
        int seconds = pTime;

        int p1 = seconds % 60;
        int p2 = seconds / 60;
        int p3 = p2 % 60;
        p2 = p2 / 60;
        return String.format("%02d:%02d:%02d", p2, p3, p1);
    }

    public void set_flow(int actual)
    {
        int acutal_value = (arrayPressets[actual-1]);

        if((acutal_value/10) > 500){statusFlow = String.valueOf(acutal_value) + " rpm";regulMode = 1;}
        else{statusFlow = String.valueOf(acutal_value/10) + " l/min"; regulMode = 0;} // FLOW
    }

    public String process(byte[] data) {

        int crcCalc = 0;
        int crcRx = 0;
        int temp = 0;
        int i = 0;
        String strInfo = "None";

        String c1, c2;

        /*try {
            for (i = 3; i < data.length - 2; i++) {
                temp = data[i];
                temp = temp & 0xFF;
                crcCalc += temp;

                int crcRxL = data[data.length - 2];
                crcRxL = crcRxL & 0xFF;
                int crcRxH = data[data.length - 1];
                crcRxH = crcRxH & 0xFF;
                crcRx = (crcRxH << 8) + crcRxL;

            }

        } catch (Exception e) {
            strInfo = "Format error!";
        }

        try {

            if (crcCalc == crcRx) {

                strInfo = "CRC ok";


                if ((data[4] & 0xFF) == 0x20) // Device status
                {
                    userString = "";

                    int k = 0;

                    while(k < 16)
                    {
                        bUserString[k] = (byte) (data[k+5] & 0xFF);

                        if(bUserString[k] == 0){break;}

                        userString += (char)bUserString[k];

                        k++;
                    }

                }


                if ((data[4] & 0xFF) == 0x22) // Device status
                {
                    statusFilter = data[10] & 0xFF;
                    statusBattery = data[8] & 0xFF;

                    // Are share data available? -----------------------------------------------
                    if (((data[15] & 0xFF) & 0x02) == 0x02) {
                        cmdStatus = 9; // nearby send will be read share data
                    }
                    // -------------------------------------------------------------------------

                    //strInfo = "Stop status";
                }

                if (((data[4] & 0xFF) == 0x61) && ((data[3] & 0xFF) == 0x83)) // Read Tom data
                {
                    //Error = data[5] & 0xFF;
                    //Keyboard = data[6] & 0xFF;
                    //Motor = data[7] & 0xFF;
                    statusBattery = data[8] & 0xFF;
                    //BatteryMinutes = data[9] & 0xFF;
                    statusFilter = data[10] & 0xFF;
                    //u8  Regulator; data[11] & 0xFF;                     // Regulation status
                    //u8  Warning; data[12] & 0xFF;                        // Warning status
                    warnings = (data[12] & 0xFF);//data[12] & 0xFF;
                    //u8  Miscellaneous; data[13] & 0xFF;                   // Miscellaneous status
                    //u8  HWExtras; data[14] & 0xFF;                      // Hardware optional modules
                    //u8  Bluetooth; data[15] & 0xFF;                     // Bluetooth module status

                    // Are share data available? -----------------------------------------------
                    if (((data[15] & 0xFF) & 0x02) == 0x02) {
                        cmdStatus = 9; // nearby send will be read share data
                    }
                    //u8  Reserve[4]; data[16] & 0xFF; 17,18,19,
                    //
                    // 20,21,22,23
                    int timerFilter = (data[20] & 0xFF) + ((data[21] & 0xFF) << 8) + ((data[22] & 0xFF) << 16) + ((data[23] & 0xFF) << 32);

                    if(timerFilter > 0xFFFFF)
                    {
                        runtimeTimerFilter = "-";
                    }
                    else
                    {
                        runtimeTimerFilter = secondsToString(timerFilter);
                    }

                    //u32 RunTimeTotal; data[24] 25 26 27                  // Total device runtime
                    int run = (data[24] & 0xFF) + ((data[25] & 0xFF) << 8) + ((data[26] & 0xFF) << 16) + ((data[27] & 0xFF) << 32);
                    runtime = secondsToString(run);
                    //u32 RunTimeSession; data[28] 29 30 31                // Device runtime since last power on
                    run = (data[28] & 0xFF) + ((data[29] & 0xFF) << 8) + ((data[30] & 0xFF) << 16) + ((data[31] & 0xFF) << 32);
                    runtimeSession = secondsToString(run);
                    //u8  PresetRegulMode[10]; data[32] 33,34,35,36,37,38,39,40,41
                    presetSetPointIdx = (data[42] & 0xFF);
                    //u8  PresetSetPointIdx; data[42]
                    //u16 PresetSetPoint[10]; data[43] 44,45,46,47,48,49,50,51,52,

                    int q = 43;

                    for(int j = 0; j < 10; j++ )
                    {
                        arrayPressets[j] = (data[q++] & 0xFF) + ((data[q++] & 0xFF) << 8);
                    }

                    strArray = "";

                    for(int j = 0; j < 10; j++ ) {
                        strArray +=  String.valueOf(arrayPressets[j]) + ",";
                    }

                    set_flow(presetSetPointIdx);
                }

                if (((data[4] & 0xFF) == 0x62) && ((data[3] & 0xFF) == 0x83)) // Read Tom data
                {
                    //u16 UBat;                              // Battery voltage (mV)
                    uBattery = (data[5] & 0xFF) + ((data[6] & 0xFF) << 8);
                    //u16 IMot;                              // Motor current (mA x 10)
                    iMot = ((double)((data[7] & 0xFF) + ((data[8] & 0xFF) << 8))) / 10;
                    //u16 IReg;                              // Motor current for regulation (mA x 10)
                    // 9 10
                    //u16 RMot;                              // Motor RPM (/min)
                    rMot = (((data[11] & 0xFF) + ((data[12] & 0xFF) << 8)));
                    //u16 Flow;                              // Air flow (l/min x 10)
                    flow = ((double)((data[13] & 0xFF) + ((data[14] & 0xFF) << 8))) / 10;
                    //u16 PWM1;                              // Control PWM on DC-DC converter
                    // 15 16
                    //u16 PWM2;                              // Control PWM on BLDC driver
                    // 17 18
                    //u16 USrc;                              // DC-DC converter output voltage (mV)
                    // 19 20
                    //u16 PBar;                              // Barometric pressure (hPa x 10)
                    presBar = ((double)((data[21] & 0xFF) + ((data[22] & 0xFF) << 8))) / 10;
                    //u16 PMot;                              // Motor power (mW)
                    // 23 24
                    //i16 TBar;                              // "Barometric" temperature (°C x 10)
                    tempBar = ((double)((data[25] & 0xFF) + ((data[26] & 0xFF) << 8))) / 10;
                    //u8  TimerDateDays;                     // 1-31
                    //u8  TimerDateMonths;                   // 1-12
                    //u8  TimerDateYears;                    // 0-99 (0..2000, 99..2099)


                    if((data[29] & 0xFF) == 0x00)
                    {
                        runtimeService = "Not set";
                    }
                    else
                    {
                        runtimeService = "20"+String.valueOf((data[29] & 0xFF)) + "-" + String.valueOf((data[28] & 0xFF)) + "-" + String.valueOf((data[27] & 0xFF));
                    }
                    //u8  Reserve[33];
                }

                if (((data[4] & 0xFF) == 0x60) && ((data[5] & 0xFF) == 0x83)) // Read share data
                {
                    if ((data[6] & 0xFF) == 0x41) { // QuickData
                        iStandardLock = (data[7] & 0xFF);bStandardLock = ((byte)iStandardLock); // int ->byte
                        iStandard = (data[8] & 0xFF);
                        iFilter = (data[9] & 0xFF);
                        defaultTimerFilter = (data[10] & 0xFF)-1;
                        iLanguage = (data[11] & 0xFF);
                        // DispView - 12
                        // Reserved [2] - 13, 14
                        iBrightness = (data[15] & 0xFF);
                        iRotation = (data[16] & 0xFF);
                        // StandardBase - 17
                        int j = 0;
                        for(i = 18; i < 24; i++)
                        {
                            standardCombination[j++] = data[i];
                        }
                        // Standard - 25
                        j = 0;
                        for(i = 26; i < 32; i++)
                        {
                            filterCombination[j++] = data[i];
                        }
                        // Reserved2 [6] - 33, 34, 35, 36, 37, 38
                    }
                }

                if ((data[4] & 0xFF) == 0x21) // Read runtime
                {
                    int run = (data[11] & 0xFF) + ((data[12] & 0xFF) << 8) + ((data[13] & 0xFF) << 16) + ((data[14] & 0xFF) << 32);

                    runtime = secondsToString(run);
                }

                if ((data[4] & 0xFF) == 0x27) // Read index preset
                {
                    indexPreset = (data[5] & 0xFF);
                }
                //data[4] = 0;
                //mBattery.setText(String.format("Batterry: %d", statusBattery));
                //mFilter.setText(String.format("Filter: %d", statusFilter));
            } else // CRC Error
            {
                //strData.append(String.format("H: %d, L: %d, CALC: %d, COUNT: %d, ", crcRxH, crcRxL, crcCalc, crcRx));
                //mWriteData.setText("CRC error!");

                c1 = String.valueOf(crcCalc);
                c2 = String.valueOf(crcRx);
                strInfo = c1 + " " + c2;
            }

        }
        catch (Exception e)
        {
            ;
        }*/

        return strInfo;
    }

    public void dataFlagReceive() {
        flagRx = true;
    }



    public void cmdReadStatus() {
        byte[] status =     {0x6E,0x21,0x02,0x03,0x22,0x25,0x00}; // Device Info
        MainActivity.mBluetoothLeService.writeCharacteristic(BluetoothLeService.mTargetCharacteristic, status);
    }

    public void cmdReadStatusTom() {
        byte[] status =     {0x6E,0x21,0x02,0x03,0x61,0x64,0x00}; // Device Info Tom
        MainActivity.mBluetoothLeService.writeCharacteristic(BluetoothLeService.mTargetCharacteristic, status);
    }

    public void cmdMarekSet() {
        byte[] status = {0x01}; // Device Info Tom
        MainActivity.mBluetoothLeService.writeCharacteristic(BluetoothLeService.mTargetCharacteristic, status);
    }

    public void cmdMarekSetValue(int data) {
        byte[] bdata = {0x00};
        bdata[0] =(byte)((data) & 0xFF);
        MainActivity.mBluetoothLeService.writeCharacteristic(BluetoothLeService.mTargetCharacteristic, bdata);
    }

    public void cmdUpDown(int seq) {



        byte crcSeq = (byte)((0x32+seq) & 0xFF);
        byte[] updown =     {0x6E,0x21,0x04,0x05,0x2D,(byte)(seq & 0xFF),0x00,crcSeq,0x00}; // Device Info Tom
        MainActivity.mBluetoothLeService.writeCharacteristic(BluetoothLeService.mTargetCharacteristic, updown);
    }

    public void cmdSend() {

        // Check standardLock
        // if(bStandardLock != iStandardLock  )

        byte crc;

        byte[] userString = {0x6E,0x21,0x02,0x03,0x20,0x23,0x00};
        byte[] userStringIdx = {0x6E,0x21,0x04,0x05,0x37,0x01,0x00,0x3D,0x00};

        byte crcSetFacRes = (byte) (0xB5 & 0xFF);


        byte[] setFactoryReset = {0x6E,0x21,0x22,0x05,0x60,0x05,0x4A,0x01,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,crcSetFacRes,0x00}; // Standard 1

        // index
        int tempDefaultTimerFilter = 0;
        byte crcSetTimFilterH = (byte) ((0x00) & 0xFF);
        if(defaultTimerFilter < 0xFF){tempDefaultTimerFilter = defaultTimerFilter+1;}
        else{crcSetTimFilterH = 0x01;tempDefaultTimerFilter = 0xFF;}
        byte bDefTimerFilter = (byte) ((tempDefaultTimerFilter) & 0xFF);
        byte crcSetTimFilter = (byte) ((0xA1 + bDefTimerFilter) & 0xFF);

        byte[] setTimerFilter = {0x6E,0x21,0x22,0x05,0x60,0x05,0x37,bDefTimerFilter,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,crcSetTimFilter,crcSetTimFilterH}; // Standard 1
        byte crcReadTimFilter = (byte) (0x9F & 0xFF);
        byte[] readTimerFilter = {0x6E,0x21,0x22,0x05,0x60,0x03,0x37,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,crcReadTimFilter,0x00}; // Standard 1

        byte[] status =     {0x6E,0x21,0x02,0x03,0x22,0x25,0x00}; // Device Info
        byte[] tomasStatus1 =     {0x6E,0x21,0x02,0x03,0x61,0x64,0x00}; // Tom status 1
        byte[] tomasStatus2 =     {0x6E,0x21,0x02,0x03,0x62,0x65,0x00}; // Tom status 2 - Diagnostic
        byte[] motorOn =    {0x6E,0x21,0x04,0x05,0x41,0x01,0x00,0x47,0x00}; // Motor on
        byte[] motorOff =   {0x6E,0x21,0x04,0x05,0x41,0x00,0x00,0x46,0x00}; // Motor off
        byte[] runtime =    {0x6E,0x21,0x02,0x03,0x21,0x24,0x00}; // Read runtime

        byte crcStand1 = (byte) (0x99 & 0xFF);
        byte crcStand2 = (byte) (0x9A & 0xFF);

        byte[] standard1 =  {0x6E,0x21,0x22,0x05,0x60,0x05,0x2E,0x01,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,crcStand1,0x00}; // Standard 1
        byte[] standard2 =  {0x6E,0x21,0x22,0x05,0x60,0x05,0x2E,0x02,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,crcStand2,0x00}; // Standard 2

        crc = (byte) ((0x98+iStandard) & 0xFF);
        byte stand = (byte) (iStandard & 0xFF);
        byte[] standard =  {0x6E,0x21,0x22,0x05,0x60,0x05,0x2E,stand,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,crc,0x00}; // Standard

        byte crcBright = (byte) ((0x9B+iBrightness+iRotation) & 0xFF);
        byte convertBright = (byte) ((iBrightness) & 0xFF);
        byte convertRotation = (byte) ((iRotation) & 0xFF);
        byte[] brightness = {0x6E,0x21,0x22,0x05,0x60,0x05,0x2A,convertBright,0x00,convertRotation,0x01,0x00,0x03,0x00,0x03,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,crcBright,0x00}; // Brightness

        crc = (byte) ((0xD1+iLanguage) & 0xFF);
        byte convertLang = (byte) ((iLanguage) & 0xFF);
        byte[] languages =  {0x6E,0x21,0x22,0x05,0x60,0x05,0x2D,convertLang,0x00,0x3A,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,crc,0x00}; // set language

        byte[] melody =     {0x6E,0x21,0x04,0x05,0x42,0x01,0x00,0x48,0x00}; // Melody on

        crc = (byte) ((0x63) & 0xFF);
        byte[] readShareData = {0x6E,0x21,0x22,0x03,0x60,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x63,0x00};

        crc = (byte) ((0xC5) & 0xFF);
        byte[] readLanguage = {0x6E,0x21,0x22,0x05,0x60,0x03,0x2D,0x30,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,crc,0x00};

        crc = (byte) ((0x94) & 0xFF);
        byte[] readStandardLock = {0x6E,0x21,0x22,0x05,0x60,0x03,0x2C,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,crc,0x00}; // read standard lock

        crc = (byte) ((0x96+bStandardLock) & 0xFF);
        byte[] writeStandardLock = {0x6E,0x21,0x22,0x05,0x60,0x05,0x2C,bStandardLock,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,crc,0x00}; // write standard lock

        crc = (byte) ((0xC6) & 0xFF);
        byte[] readStandard = {0x6E,0x21,0x22,0x05,0x60,0x03,0x2E,0x30,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,crc,0x00}; // read standard

        crc = (byte) ((0x97) & 0xFF);
        byte[] readFilter = {0x6E,0x21,0x22,0x05,0x60,0x03,0x2F,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,crc,0x00}; // read standard

        byte bFilter = (byte) ((iFilter) & 0xFF);
        crc = (byte) ((0x99+bFilter) & 0xFF);
        byte[] writeFilter = {0x6E,0x21,0x22,0x05,0x60,0x05,0x2F,bFilter,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,crc,0x00}; // read standard


        crc = (byte) ((0xA9) & 0xFF);
        byte[] readQuickData = {0x6E,0x21,0x22,0x05,0x60,0x03,0x41,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,crc,0x00}; // read standard

        crc = (byte) ((0x92) & 0xFF);
        byte[] readBrightnessRotation = {0x6E,0x21,0x22,0x05,0x60,0x03,0x2A,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,crc,0x00}; // read standard

        try
        {
            switch(cmdStatus)
            {
                case 1:MainActivity.mBluetoothLeService.writeCharacteristic(BluetoothLeService.mTargetCharacteristic, motorOn);break;
                case 2:MainActivity.mBluetoothLeService.writeCharacteristic(BluetoothLeService.mTargetCharacteristic, motorOff);break;
                case 3:MainActivity.mBluetoothLeService.writeCharacteristic(BluetoothLeService.mTargetCharacteristic, runtime);break;
                case 4:MainActivity.mBluetoothLeService.writeCharacteristic(BluetoothLeService.mTargetCharacteristic, standard1);break;
                case 5:MainActivity.mBluetoothLeService.writeCharacteristic(BluetoothLeService.mTargetCharacteristic, standard2);break;
                case 6:MainActivity.mBluetoothLeService.writeCharacteristic(BluetoothLeService.mTargetCharacteristic, brightness);break;
                case 7:MainActivity.mBluetoothLeService.writeCharacteristic(BluetoothLeService.mTargetCharacteristic, languages);break;
                case 8:MainActivity.mBluetoothLeService.writeCharacteristic(BluetoothLeService.mTargetCharacteristic, melody);break;
                case 9:MainActivity.mBluetoothLeService.writeCharacteristic(BluetoothLeService.mTargetCharacteristic, readShareData);break;
                case 10:MainActivity.mBluetoothLeService.writeCharacteristic(BluetoothLeService.mTargetCharacteristic, readLanguage);break;
                case 11:MainActivity.mBluetoothLeService.writeCharacteristic(BluetoothLeService.mTargetCharacteristic, readStandardLock);break;
                case 12:MainActivity.mBluetoothLeService.writeCharacteristic(BluetoothLeService.mTargetCharacteristic, readStandard);break;
                case 13:MainActivity.mBluetoothLeService.writeCharacteristic(BluetoothLeService.mTargetCharacteristic, readFilter);break;
                case 14:MainActivity.mBluetoothLeService.writeCharacteristic(BluetoothLeService.mTargetCharacteristic, readQuickData);break;
                case 15:MainActivity.mBluetoothLeService.writeCharacteristic(BluetoothLeService.mTargetCharacteristic, writeStandardLock);break;
                case 16:MainActivity.mBluetoothLeService.writeCharacteristic(BluetoothLeService.mTargetCharacteristic, standard);break;
                case 17:MainActivity.mBluetoothLeService.writeCharacteristic(BluetoothLeService.mTargetCharacteristic, readBrightnessRotation);break;
                case 18:MainActivity.mBluetoothLeService.writeCharacteristic(BluetoothLeService.mTargetCharacteristic, writeFilter);break;
                case 19:MainActivity.mBluetoothLeService.writeCharacteristic(BluetoothLeService.mTargetCharacteristic, tomasStatus1);break;
                case 20:MainActivity.mBluetoothLeService.writeCharacteristic(BluetoothLeService.mTargetCharacteristic, tomasStatus2);break;
                case 21:MainActivity.mBluetoothLeService.writeCharacteristic(BluetoothLeService.mTargetCharacteristic, userString);break;
                case 22:MainActivity.mBluetoothLeService.writeCharacteristic(BluetoothLeService.mTargetCharacteristic, userStringIdx);break;
                //case 23:MainActivity.mBluetoothLeService.writeCharacteristic(BluetoothLeService.mTargetCharacteristic, setTimerReset);break;
                case 24:MainActivity.mBluetoothLeService.writeCharacteristic(BluetoothLeService.mTargetCharacteristic, setFactoryReset);break;
                case 25:MainActivity.mBluetoothLeService.writeCharacteristic(BluetoothLeService.mTargetCharacteristic, setTimerFilter);break;
                case 26:MainActivity.mBluetoothLeService.writeCharacteristic(BluetoothLeService.mTargetCharacteristic, readTimerFilter);break;

                default:MainActivity.mBluetoothLeService.writeCharacteristic(BluetoothLeService.mTargetCharacteristic, status);break; //0
          }

          if(flagRx)
          {
              flagRx = false;

              switch (cmdStatus)
              {
                  case 19:  cmdStatus = 20; break;  // TomStatus1 -> TomStatus2
                  case 20: cmdStatus = 14;  break;  // TomStatus2 -> QuickData
                  default: cmdStatus = 19; break;   // "QuickData" -> TomStatus1
              }
          }

        } catch (Exception e){;}
   }

    public void cmdMotorOn()
    {
        cmdStatus = 1;
    }

    public void cmdMotorOff() {
        cmdStatus = 2;
    }

    public void cmdStandard(int s) {
        iStandard = s;
        cmdStatus = 16;
    }

    public void cmdBrightnessRotation(int b,int r)
    {
        cmdStatus = 6;
        iBrightness = b;
        iRotation = r;
    }

    public void cmdLanguage(int i)
    {
        cmdStatus = 7;
        iLanguage = i;
    }

    public void cmdMelody() {
        cmdStatus = 8;
    }

    public void cmdSetStandardLock(byte bSL) {
        bStandardLock = bSL;

        cmdStatus = 15;
    }

    public void cmdSetFilter(int bF) {

        iFilter = bF;
        cmdStatus = 18;
    }

    public void cmdFactoryReset() {

        cmdStatus = 24;
    }

    public void cmdTimerFilter(int dTf)
    {
        defaultTimerFilter = dTf;
        cmdStatus = 25;
    }

    public void test()
    {
        cmdStatus = 26;
    }
}
