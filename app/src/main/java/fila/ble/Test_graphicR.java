package fila.ble;

import android.os.Bundle;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.app.Dialog;

import java.util.ArrayList;

import android.widget.ImageView;

import android.view.WindowManager;
import android.view.Display;


public class Test_graphicR extends Activity{

    ProgressBar prFlow;
    ProgressBar prFilter;
    ProgressBar prBattery;

    ListView listViewR;
    CustomAdapter customAdapterR;

    Button btn_up,btn_down;

    public class table{
        protected String name;
        protected int index;
        protected Drawable image;
        protected String title;

        public table(String n, int i, Drawable d, String t){this.name = n; this.index = i;this.image = d;this.title = t;}

        public void process(int i){;}

    }

    public class language extends table
    {
        language(String name, int index, Drawable image){super(name,index,image,"Language:");}

        @Override
        public void process(int i)
        {
            Toast.makeText(Test_graphicR.this, "Language: " + String.valueOf(i), Toast.LENGTH_SHORT).show();
        }
    }

    public class standard extends table
    {
        private boolean lock;
        private String unit;

        public standard(String n,int i, Drawable d, boolean l, String u)
        {
            super(n,i,d, "Standard:");
            this.lock = l;
            this.unit = u;
        }

        @Override
        public void process(int i)
        {
            Toast.makeText(Test_graphicR.this, "Standard: " + String.valueOf(i), Toast.LENGTH_SHORT).show();
        }
    }

    public static ArrayList<table> arrayOfLanguagesR = new ArrayList<table>();
    public static ArrayList<table> arrayOfStandardR = new ArrayList<table>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sample_list);

        prFlow = (ProgressBar)findViewById(R.id.progressBarFlow);
        prFilter = (ProgressBar)findViewById(R.id.Filter);
        prBattery = (ProgressBar)findViewById(R.id.progressBarBattery);

        prFlow.setProgress(50);
        prFilter.setProgress(50);
        prBattery.setProgress(50);

        Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        float ratioX = ((float) display.getWidth());

        //Toast.makeText(getApplicationContext(),String.valueOf(ratioX), Toast.LENGTH_SHORT).show();

        if(ratioX >= 1200 || MainActivity.flagTestResolutionDisp)
        {
            ImageView imageViewX = (ImageView) findViewById(R.id.imageView);
            RelativeLayout.LayoutParams paramsChangeView = (RelativeLayout.LayoutParams)imageViewX.getLayoutParams();
            //paramsChangeView.width = 300;

            //--------------------------------
            paramsChangeView.leftMargin = 100; ///////////////

            TextView textCBleName = (TextView) findViewById(R.id.bleName);
            RelativeLayout.LayoutParams paramsBle = (RelativeLayout.LayoutParams)textCBleName.getLayoutParams();
            paramsBle.leftMargin = 100;

            TextView textCustom = (TextView) findViewById(R.id.customName);
            RelativeLayout.LayoutParams paramsCustom = (RelativeLayout.LayoutParams)textCustom.getLayoutParams();
            paramsCustom.leftMargin = 100;

            ImageView imageBlue = (ImageView) findViewById(R.id.ivBlue);
            RelativeLayout.LayoutParams paramsBlue = (RelativeLayout.LayoutParams)imageBlue.getLayoutParams();
            paramsBlue.rightMargin = 60;
            //--------------------------------

            //Toast.makeText(getApplicationContext(),"Scale", Toast.LENGTH_SHORT).show();
        }




        /*btn_up = findViewById(R.id.btn_upR);
        btn_down = findViewById(R.id.btn_downR);

        // LANGUAGES
        arrayOfLanguagesR.add(new language("English", 1, getResources().getDrawable(R.drawable.ic_list_uk)));
        arrayOfLanguagesR.add(new language("French", 2, getResources().getDrawable(R.drawable.ic_list_fr)));

        arrayOfStandardR.add(new standard("Hood", 1,getResources().getDrawable(R.drawable.hood), false, "Both"));
        arrayOfStandardR.add(new standard("FullfaceMask", 2, getResources().getDrawable(R.drawable.fullface), false, "Both"));

        prFlow = (ProgressBar)findViewById(R.id.progressBarFlow2);
        prFilter = (ProgressBar)findViewById(R.id.progressBarFilter2);
        prBattery = (ProgressBar)findViewById(R.id.progressBarBat2);

        //RelativeLayout rel = (RelativeLayout) findViewById(R.id.rel1);
        View mainImage = (View) findViewById(R.id.imageViewBars);

        int value = 5;

        Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();

        float ratioX = ((float) display.getWidth());
        float ratioY = ((float) display.getHeight());

        TextView txt = (TextView)findViewById(R.id.textView7);
        txt.setText(String.valueOf((int)ratioX) + "x" +String.valueOf((int)ratioY));

        if(ratioX >= 1200 || MainActivity.flagTestDisp)
        {
            float scale = (float)2.7;

            ((ProgressBar)prFlow).setScaleY(scale);
            ((ProgressBar)prFilter).setScaleY(scale);
            ((ProgressBar)prBattery).setScaleY(scale);

            ((ProgressBar)prFlow).setScaleX(scale);
            ((ProgressBar)prFilter).setScaleX(scale);
            ((ProgressBar)prBattery).setScaleX(scale);

            mainImage.setScaleX(scale);
            mainImage.setScaleY(scale);

            RelativeLayout.LayoutParams paramsImage =(RelativeLayout.LayoutParams)mainImage.getLayoutParams();
            RelativeLayout.LayoutParams paramsFlow = (RelativeLayout.LayoutParams)prFlow.getLayoutParams();
            RelativeLayout.LayoutParams paramsFilter = (RelativeLayout.LayoutParams)prFilter.getLayoutParams();
            RelativeLayout.LayoutParams paramsBattery = (RelativeLayout.LayoutParams)prBattery.getLayoutParams();

            paramsImage.setMarginStart(600);
            paramsImage.topMargin = 300;

            paramsFlow.setMarginEnd(-50);
            paramsFlow.bottomMargin = 485;

            paramsFilter.setMarginEnd(-50);
            paramsFilter.bottomMargin = 190;

            paramsBattery.setMarginEnd(-50);
            paramsBattery.bottomMargin = -110;

            mainImage.setLayoutParams(paramsImage);
            prFlow.setLayoutParams(paramsFlow);
            prFilter.setLayoutParams(paramsFilter);
            prBattery.setLayoutParams(paramsBattery);
        }

        Resources res = getResources();
        prFlow.setProgressDrawable(res.getDrawable( R.layout.greenprogress));
        prFilter.setProgressDrawable(res.getDrawable( R.layout.orangeprogress));
        prBattery.setProgressDrawable(res.getDrawable( R.layout.redprogress));

        prFlow.setProgress(100);
        prFilter.setProgress(50);
        prBattery.setProgress(100);

        //showDialogTable(arrayOfLanguagesR);
        showDialogTable(arrayOfStandardR);

        btn_up.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v) {

                Toast.makeText(Test_graphicR.this, "Click up", Toast.LENGTH_SHORT).show();

            }

        });

        btn_down.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v) {

                Toast.makeText(Test_graphicR.this, "Click down", Toast.LENGTH_SHORT).show();

            }

        });*/

    }



    private void showDialogTable(final ArrayList<table> arrayObject) {

        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.radiobutton_tabler);
        dialog.setTitle(arrayObject.get(0).title);

        listViewR = (ListView)dialog.findViewById(R.id.listViewR);
        customAdapterR = new CustomAdapter(arrayObject);
        listViewR.setAdapter(customAdapterR);

        listViewR.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                (arrayObject).get(i).process(i);
                dialog.cancel();

            }
        });

        dialog.show();
    }

    class CustomAdapter extends BaseAdapter {

        ArrayList<table> myArrayObject;

        CustomAdapter(ArrayList<table> arrayObject)
        {
            myArrayObject = arrayObject;
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
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            convertView = getLayoutInflater().inflate(R.layout.sample_listr, null);

            TextView name = (TextView) convertView.findViewById(R.id.view_name);
            ImageView image = (ImageView) convertView.findViewById(R.id.view_image);

            name.setText(myArrayObject.get(position).name);
            image.setImageDrawable(myArrayObject.get(position).image);

            return convertView;
        }
    }
}
