package fila.ble;

public class TypeUnits {

    // Save in file
    private String customName;
    private String bleName;
    private String macAddress;
    private String defaultFilterTimer;

    // Want to get to file
    private boolean top;

    // Values in memory
    private int flow;
    private int filter;
    private String flowLiter;
    private int battery;
    boolean warn;

    // Calling units
    private int counterDataRx;
    private int timerTryConnectAfterOffline;
    boolean online;
    private int connectState;
        // 0 - Disconnect
        // 1 - Connecting
        // 2 - Connected

    // Not use
    private int timerNoRx;
    boolean canSend;

    private int process;
                    // 0 - First start
                    // 1 - Try bind
                    // 2 - Bind ok
                    // 3 - Register
                    // 4 - Try connect
                    // 5 - Connected
                    // 6 - Try notify
                    // 7 - Notify enabled
                    // 8 - Online Rx ok
                    // 9 - Try notify disable
                    // 10 - Notify disabled
                    // 11 - Try disconnect
                    // 12 - Disconnected
                    // 13 - unregister
                    // 14 - unbind
                    // 15 - close

    public TypeUnits (String custom, String ble, String mac, String defFiltTim)
    {
        setCustomName(custom);
        setBleName(ble);
        setMacAddress(mac);
        setDefaultFilterTimer(defFiltTim);

        top = false;
        flowLiter = "- l/min";
        flow = 0;
        filter = 0;
        battery = 0;
        warn = false;
        counterDataRx = 0;
        connectState = 0; // DISCONNECTED
        timerTryConnectAfterOffline = 0;
        process = 0; //PROC_FIRST_START
        online = true;

        // Not use
        canSend = false;
        timerNoRx = 5;
    }

    // GET
    public int getTimerTryConnectAfterOffline() {
        return timerTryConnectAfterOffline;
    }
    public boolean getOnline() { return online;}
    public int getConnectState() { return connectState;}
    public int getProcess() { return process;}
    public int getCounterDataRx() { return counterDataRx;}
    public String getCustomName() { return customName;}
    public String getBleName() {
        return bleName;
    }
    public String getMacAddress() {
        return macAddress;
    }
    public String getDefaultFilterTimer() {
        return defaultFilterTimer;
    }
    public boolean getStatusTop() {
        return top;
    }
    public int getFlow() {
        return flow;
    }
    public boolean getWarn() {
        return warn;
    }
    public int getFilter() {
        return filter;
    }
    public String getFlowLiter() {
        return flowLiter;
    }
    public int getBattery() {
        return battery;
    }
    //Not use
    public int getTimerNoRx() { return timerNoRx;}
    public boolean getCanSend() { return canSend;}


    // SET
    public void setTimerTryConnectAfterOffline(int t){timerTryConnectAfterOffline = t;}
    public void setProcess(int proc) {
        this.process = proc;
    }
    public void setOnline(boolean o) {
        this.online = o;
    }
    public void setConnectState(int con) {
        this.connectState = con;
    }
    public void setCounterDataRx(int c) {
        this.counterDataRx = c;
    }
    public void setCustomName(String str) {
        this.customName = str;
    }
    public void setBleName(String str) {
        this.bleName = str;
    }
    public void setMacAddress(String str) {
        this.macAddress = str;
    }
    public void setDefaultFilterTimer(String str) {
        this.defaultFilterTimer = str;
    }
    public void setStatusTop(boolean b) {
        this.top = b;
    }
    public void setFlow(int f) {
        this.flow = f;
    }
    public void setWarn(boolean b) {
        this.warn = b;
    }
    public void setFilter(int f) {
        this.filter = f;
    }
    public void setFlowLiter(String f) {
        this.flowLiter = f;
    }
    public void setBattery(int b) {
        this.battery = b;
    }
    // Not use
    public void setTimerNoRx(int t){timerNoRx = t;}
    public void setCanSend(boolean b) {
        this.canSend = b;
    }


    @Override
    public String toString() {

        String str = getCustomName() + "," + getBleName() + "," + getMacAddress() + "," + getDefaultFilterTimer() + ";";
        return str;
    }
}

