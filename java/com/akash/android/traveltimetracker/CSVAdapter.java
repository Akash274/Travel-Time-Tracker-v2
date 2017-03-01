package com.akash.android.traveltimetracker;

import android.support.design.widget.Snackbar;
import android.util.Log;

import com.firebase.client.Firebase;
import com.firebase.client.core.Context;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by akash on 13/01/17.
 */

public class CSVAdapter {

    public static void upload(){

        String line =
                "1,19.007872,73.016258,NRI," +
                        "2,19.009383,73.015974,Sai Krupa Society," +
                        "3,19.011569,73.014987,Sai Dham Society," +
                        "4,19.010869,73.01636,Sai Sangam/Asthagandha Society," +
                        "5,19.013147,73.017814,Jai Gajanand Society," +
                        "6,19.014171,73.016526,Sector 48/Banubai Ganpat Tandel Market," +
                        "7,19.015338,73.014981,SS High School," +
                        "8,19.017062,73.013388,Satyam Hights," +
                        "9,19.017934,73.012358,Shree Ganesh Maidan," +
                        "10,19.020404,73.011231,Karave Gaon," +
                        "11,19.02009,73.014273,Kendriya Vihar Sector 38," +
                        "12,19.01871,73.015813,Seawood D-Mart," +
                        "13,19.017579,73.017105,Nakwa Chowk," +
                        "14,19.024008,73.022827,Sterling Collage," +
                        "15,19.030151,73.018246,Nerul Police Station," +
                        "16,19.032127,73.018842,Dakshata Society," +
                        "17,19.033131,73.01881,Nerul Railway Station East," +
                        "18,19.034353,73.019529,Bikaner," +
                        "19,19.036914,73.019653,Nerul Sector 11-15," +
                        "20,19.039881,73.020323,Nerul Bus Depot," +
                        "21,19.039881,73.020323,Parsik Bank ," +
                        "22,19.042944,73.022207,SIES College," +
                        "23,19.044404,73.023124,Shivaji Chowk ," +
                        "24,19.046207,73.024398,Nerul LP/D Y Patil," +
                        "25,19.051531,73.020317,Shirvane Gaon," +
                        "26,19.056291,73.01921,Jui Nagar Railway Station," +
                        "27,19.063022,73.018928,Sanpada Police Station," +
                        "28,19.070465,73.018982,Turbhe Nakka," +
                        "30,19.072997,73.014191,Turbhe Aagaar," +
                        "31,19.073105,73.012604,ICL Shala," +
                        "32,19.073461,73.008891,APMC Bus Stop," +
                        "33,19.073689,73.005712,Kanda Batata Bazar," +
                        "34,19.074029,73.001429,St Lorence High School," +
                        "35,19.07435,72.9984,Bank of Baroda," +
                        "36,19.075504,72.996992,Vashi Bus Depot," +
                        "37,19.073446,72.997829,Navratna Hotel," +
                        "38,19.070872,72.997563,Apna Bazar," +
                        "39,19.06849,72.996599,Vashi Highway," +
                        "40,19.069275,72.989127,Vashi Gaon," +
                        "41,19.063603,72.979039,Khadi Pul Navi Mumbai Toll Naka," +
                        "42,19.057525,72.954851,Jakat Naka," +
                        "43,19.050742,72.936149,Maharashtra Nagar," +
                        "44,19.049625,72.932908,Mankhurd Railway Station ," +
                        "45,19.045268,72.925651,Aagarwadi," +
                        "46,19.04395,72.919883,Anushakti Nagar BRC Hospital," +
                        "47,19.044711,72.91762,Telecom Factory," +
                        "48,19.045231,72.915755,Punjab Wadi," +
                        "49,19.045601,72.911783,Devnar Agar," +
                        "50,19.047424,72.909818,Dukes Company," +
                        "51,19.052296,72.910665,I Max cinema / Anik Aagar," +
                        "52,19.055584,72.910378,Ashok Nagar," +
                        "53,19.063746,72.912256,Indian Oil Nagar," +
                        "54,18.977377,72.845282,Reay Road Railway Station," +
                        "55,18.967372,72.846118,Mazgaon Dock," +
                        "56,18.985336,72.848463,otton Green railway station," +
                        "58,18.978979,72.845574,Khadi Bhandar," +
                        "59,18.931375,72.831249,Hutatma Chowk," +
                        "60,18.926141,72.832479,Shyam Prasad Mukherjee Chowk," +
                        "61,18.928488,72.82961,Mantralaya,";

        String cvsSplitBy = ",";
        String Busnumber="54 Kharghar-Taloja";
        final String TAG = "MyActivity";
        int i =0;

        try {
                Firebase BusRoute = new Firebase("https://travel-time-tracker.firebaseio.com/BusRoute");

                while (line != null) {

                    line=line.replace(" ","");
                    line=line.replace("/","|");
                    String[] RowData = line.split(cvsSplitBy);

                    Firebase objectBusName = BusRoute.child(Busnumber);
                    Log.v(TAG,"ID:"+RowData[i]+"Name:"+RowData[i+3]+"Lat"+RowData[i+1]+"Lng"+RowData[i+2]);
                    Firebase objectBusStop = objectBusName.child(RowData[i]+" "+RowData[i+3]);
                    Firebase childLat = objectBusStop.child("Lat");
                    Firebase childLng = objectBusStop.child("Lng");
                    Firebase childName = objectBusStop.child("Name");

                    childLat.setValue(Double.parseDouble(RowData[i+1]));
                    childLng.setValue(Double.parseDouble(RowData[i+2]));
                    childName.setValue(RowData[i+3]);

                    i+=4;

                }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {

        }
    }
}
