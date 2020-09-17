/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.jakec.smart_key;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.nfc.cardemulation.HostApduService;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;

import java.util.Arrays;

public class HCEService extends HostApduService {
    // AID for our loyalty card service.
    private static final String SELECT_APDU_HEADER = "00A40400";
    private static final byte[] AID = {0x00, (byte) 0xA4, 0x04, 0x00, 0x07, (byte) 0xF0, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06};
    private static final byte[] SELECT_OK_SW = HexStringToByteArray("9000");
    private static final byte[] UNKNOWN_CMD_SW = HexStringToByteArray("0000");
    private final int ADD_KEY_MODE = 0x00;
    private Context context;
    private Activity a = new Activity();
    private boolean dupe = false;

    private AndroidKeyData key;
    int counter = 0;
    // BEGIN_INCLUDE(processCommandApdu)
    private int messageCounter = 0;
    private String mode = "";
    private KeyDataDB db;
    private AndroidKeyData newKey;
    private Boolean def = false;
    @SuppressLint("MissingPermission")
    @Override
    public byte[] processCommandApdu(byte[] apdu, Bundle extras) {
        if (context == null) {
            context = this.getApplicationContext();
        }
        if(db == null){
            db = new KeyDataDB(context);
        }
        if (Arrays.equals(apdu, AID)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                return Build.getSerial().getBytes();
               /* if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                    //
                }*/
            }
        }
        else{
            String s = "";
            for (byte i : apdu) {
                s += i + ", ";
            }
            System.out.println("APDU = " + s);
            String input = convertHexToString(ByteArrayToHexString(apdu));
            System.out.println("ADPU String = "+input);
            String[] values = input.split("@@");
            if(values.length > 0) {
                if (values[0].trim().equalsIgnoreCase("create")) {
                    if(mode.equalsIgnoreCase("create")){
                        if(newKey != null){
                            /*if(values.length > 3) {
                                newKey.setKeyData(values[1]);
                                newKey.setKeyRoller(Integer.parseInt(values[2]));
                                newKey.setEncryptionKey(values[3]);
                            }*/
                            if(dupe){
                                Toast.makeText(context, "Updating Key.....", Toast.LENGTH_LONG).show();
                                db.deleteKey(newKey.getKey_ID());
                            } else {
                                Toast.makeText(context, "Added a new key: "+newKey.getVehName(), Toast.LENGTH_LONG).show();
                            }
                            db.addKey(newKey);
                            ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
                            Activity currentActivity = ((MyApp) context.getApplicationContext()).getCurrentActivity();
                            if (currentActivity != null) {
                                ComponentName cn = currentActivity.getComponentName();
                                System.out.println("" + cn);
                                currentActivity.finish();
                                startActivity(currentActivity.getIntent());
                            }
                        }
                    } else {
                        mode = values[0];
                        newKey = new AndroidKeyData(Integer.parseInt(values[2]));
                        newKey.setVehName(values[1]);
                        newKey.setVeh_ID(Integer.parseInt(values[2]));
                        if(values.length > 3){
                            newKey.setEnd(Integer.parseInt(values[3]));
                        }
                        if(new KeyDataDB(context).keyPresent(newKey.getKey_ID())){
                            Toast.makeText(context, "Key already exists in database, it will be updated", Toast.LENGTH_LONG).show();
                            dupe = true;
                            return "dupe".getBytes();
                        }
                        return "success".getBytes();
                    }
                } else if(values[0].trim().equalsIgnoreCase("default")){
                    if(def){
                        Toast.makeText(context, "Key used : " + key.getVehName(), Toast.LENGTH_LONG);
                        this.key.rollKey();
                        return "Success".getBytes();
                    } else {
                        this.key = db.getKey(Integer.parseInt(values[1]));
                        if(this.key != null) {
                            def = true;
                            return new String(key.getkeyData()).getBytes();
                        }
                    }
                }
            }
        }
        return "ERROR_ANDROID".getBytes();
    }


    @Override
    public void onDeactivated(int reason) {

    }
    // END_INCLUDE(processCommandApdu)

    public static byte[] BuildSelectApdu(String aid) {
        // Format: [CLASS | INSTRUCTION | PARAMETER 1 | PARAMETER 2 | LENGTH | DATA]
        return HexStringToByteArray(SELECT_APDU_HEADER + String.format("%02X",
                aid.length() / 2) + aid);
    }

    public static String ByteArrayToHexString(byte[] bytes) {
        final char[] hexArray = {'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};
        char[] hexChars = new char[bytes.length * 2]; // Each byte has two hex characters (nibbles)
        int v;
        for (int j = 0; j < bytes.length; j++) {
            v = bytes[j] & 0xFF; // Cast bytes[j] to int, treating as unsigned value
            hexChars[j * 2] = hexArray[v >>> 4]; // Select hex character from upper nibble
            hexChars[j * 2 + 1] = hexArray[v & 0x0F]; // Select hex character from lower nibble
        }
        return new String(hexChars);
    }

    public String convertHexToString(String hex){

        StringBuilder sb = new StringBuilder();
        StringBuilder temp = new StringBuilder();

        //49204c6f7665204a617661 split into two characters 49, 20, 4c...
        for( int i=0; i<hex.length()-1; i+=2 ){

            //grab the hex in pairs
            String output = hex.substring(i, (i + 2));
            //convert hex to decimal
            int decimal = Integer.parseInt(output, 16);
            //convert the decimal to character
            sb.append((char)decimal);

            temp.append(decimal);
        }

        return sb.toString();
    }

    public static byte[] HexStringToByteArray(String s) throws IllegalArgumentException {
        int len = s.length();
        if (len % 2 == 1) {
            throw new IllegalArgumentException("Hex string must have even number of characters");
        }
        byte[] data = new byte[len / 2]; // Allocate 1 byte per 2 hex characters
        for (int i = 0; i < len; i += 2) {
            // Convert each character into a integer (base-16), then bit-shift into place
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }

    public static byte[] ConcatArrays(byte[] first, byte[]... rest) {
        int totalLength = first.length;
        for (byte[] array : rest) {
            totalLength += array.length;
        }
        byte[] result = Arrays.copyOf(first, totalLength);
        int offset = first.length;
        for (byte[] array : rest) {
            System.arraycopy(array, 0, result, offset, array.length);
            offset += array.length;
        }
        return result;
    }
}
