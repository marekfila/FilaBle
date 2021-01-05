package fila.ble;

import android.content.Context;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.InputStream;
import java.util.ArrayList;

public class FileHelper{

    public static ArrayList<TypeUnits> arrayOfUnits = new ArrayList<TypeUnits>();

    public static boolean wasRead = false;

         public static String writeToFile(String data,Context context, String fileName) {

             String str = "Write ok";

             try {
                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput(fileName, Context.MODE_PRIVATE));
                outputStreamWriter.write(data);
                outputStreamWriter.close();
            }
            catch (IOException e) {
                str = "File write failed: ";
            }

            return str;
        }


        public static String readFromFile(Context context, String fileName) {

            String ret = "";

            try {
                InputStream inputStream = context.openFileInput(fileName);

                if ( inputStream != null ) {
                    InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                    String receiveString = "";
                    StringBuilder stringBuilder = new StringBuilder();

                    while ( (receiveString = bufferedReader.readLine()) != null ) {
                        stringBuilder.append(receiveString);
                    }

                    inputStream.close();
                    ret = stringBuilder.toString();
                }
            }
            catch (FileNotFoundException e) {
                //Log.e("login activity", "File not found: " + e.toString());
            } catch (IOException e) {
                //Log.e("login activity", "Can not read file: " + e.toString());
            }

            return ret;
        }

        public static ArrayList<TypeUnits> Init(Context context)
        {
            ArrayList arrayUnits = getObejct(readFromFile(context,"listOfUser.txt"));
            return arrayUnits;
        }

        public static ArrayList<TypeUnits> getObejct(String str) {

            String tempCustomName;
            String tempBleName;
            String tempMacAddress;
            String tempDefaultFilterTimer;

            TypeUnits sample;
            ArrayList arrayUnits = new ArrayList<TypeUnits>();

            int i = 0;

            char c = str.charAt(i);

            int length = 10000;

            while(c != 0 && i < length)
            {
                // Init
                tempCustomName = "";
                tempBleName = "";
                tempMacAddress = "";
                tempDefaultFilterTimer = "";

                // CUSTOM NAME ------------------------------------------
                while(c != ',' && i < length)
                {
                    tempCustomName += str.charAt(i++);
                    c = str.charAt(i);
                }

                // BLE NAME ------------------------------------------
                i++;
                c = str.charAt(i);

                while(c != ',' && i < length)
                {
                    tempBleName += str.charAt(i++);
                    c = str.charAt(i);
                }

                // MAC ADDRESS ------------------------------------------
                i++;
                c = str.charAt(i);

                while(c != ',' && i < length)
                {
                    tempMacAddress += str.charAt(i++);
                    c = str.charAt(i);
                }

                // Filter Timer default value ------------------------------------------
                i++;
                c = str.charAt(i);

                while(c != ';' && i < length)
                {
                    tempDefaultFilterTimer += str.charAt(i++);
                    c = str.charAt(i);
                }

                sample = new TypeUnits(tempCustomName,tempBleName,tempMacAddress,tempDefaultFilterTimer);
                arrayUnits.add(sample);

                try
                {
                    i++;
                    c = str.charAt(i);

                } catch (Exception e)
                {
                    c = 0;
                }
            }

            return arrayUnits;
        }

        public static String writeArrayToFile(ArrayList<TypeUnits> arr, Context context)
        {
            int i = 0;
            String str = "";

            while(i < arr.size())
            {
                str += arr.get(i).toString();
                i++;
            }

            str = writeToFile(str,context,"listOfUser.txt");

            return str;

        }
}





