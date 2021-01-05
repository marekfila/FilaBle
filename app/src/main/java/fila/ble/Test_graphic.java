package fila.ble;

import android.os.Bundle;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.widget.ProgressBar;
import android.widget.ImageView;


public class Test_graphic extends Activity{

    ProgressBar prFlow;
    ProgressBar prFilter;
    ProgressBar prBattery;

    ImageView data;

    int MAX_FLOWS = 10;
    int actual_flow = 5;

    public int set_flow(int actual, int max)
    {
        int PROGRESS_MAX = 100;

        return (PROGRESS_MAX/MAX_FLOWS)*actual;
    }

    public Context readContext() {
        return this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_graphic);

        //prFlow = (ProgressBar)findViewById(R.id.progressBarFlow2);
        prFilter = (ProgressBar)findViewById(R.id.progressBarFilter2);
        prBattery = (ProgressBar)findViewById(R.id.progressBarBat2);

        Resources res = getResources();
        //prFlow.setProgressDrawable(res.getDrawable( R.layout.greenprogress));
        prFilter.setProgressDrawable(res.getDrawable( R.layout.orangeprogress));
        prBattery.setProgressDrawable(res.getDrawable( R.layout.redprogress));

        //prFlow.setProgress(/*set_flow(actual_flow, MAX_FLOWS)*/20);
        prFilter.setProgress(50);
        prBattery.setProgress(70);
    }
}
