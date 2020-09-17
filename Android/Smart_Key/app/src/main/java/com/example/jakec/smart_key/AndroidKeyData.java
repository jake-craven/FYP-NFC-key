package com.example.jakec.smart_key;

import android.content.Context;

import java.sql.Date;
import java.util.Calendar;
import java.util.Map;

/**
 * Created by jakec on 09/03/2019.
 */

public class AndroidKeyData {


    private Context context;
    private int key_ID;
    private int veh_ID;
    private String VehName;
    private char[] keyData = {'e','r','r','o','r'};
    private int keyRoller = 1;
    private byte[] encryptionKey = {0x00};
    private boolean temp = false;
    private boolean active = true;
    private Calendar end = Calendar.getInstance();


    public AndroidKeyData(){
    }

    public int getKey_ID() {
        return key_ID;
    }

    public int getVeh_ID() {
        return veh_ID;
    }

    public String getVehName() {
        return VehName;
    }

    public char[] getKeyData() {
        return keyData;
    }

    public int getKeyRoller() {
        return keyRoller;
    }

    public byte[] getEncryptionKey() {
        return encryptionKey;
    }

    public int isTemp() {
        if(temp){
         return 1;
        } else {
            return 0;
        }
    }

    public Calendar getEnd() {
        return end;
    }

    public void setKey_ID(int key_ID) {
        this.key_ID = key_ID;
    }

    public void setVeh_ID(int veh_ID) {
        this.veh_ID = veh_ID;
        this.key_ID = veh_ID;
    }

    public void setVehName(String vehName) {
        VehName = vehName.replace(" ", "\n")+"\n";
    }

    public void setKeyData(char[] keyData) {
        this.keyData = "toggle".toCharArray();
    }

    public void setKeyRoller(int keyRoller) {
        this.keyRoller = keyRoller;
    }

    public void setEncryptionKey(byte[] encryptionKey) {
        this.encryptionKey = encryptionKey;
    }

    public void setTemp(boolean temp) {
        this.temp = temp;
    }

    public void setEnd(Calendar end) {
        this.end = end;
    }

    public AndroidKeyData(int key_ID){
        setKey_ID(key_ID);
    }

    public void rollKey(){
        char[] newkeyData = new char[this.keyData.length];
        if(keyRoller == 0)
            keyRoller = 1;
        else if(keyRoller < 0)
            keyRoller = keyRoller*-1;
        for(int i = 0; i < keyData.length ; i++){
            int newPos = ((i+1)*findCharPos(keyData[i])*keyRoller) % (keyData.length-1);
            if(newPos < 0 ){
                newPos = newPos * -1;
            }
            if(newkeyData[newPos] > 0){
                char temp = newkeyData[newPos];
                newkeyData[newPos] = keyData[i];
                newPos++;
                if(newPos >= keyData.length){
                    newPos = 0;
                }
                while(newkeyData[newPos] > 0){
                    char temp2 = newkeyData[newPos];
                    newkeyData[newPos] = temp;
                    temp = temp2;
                    newPos++;
                    if(newPos >= keyData.length){
                        newPos = 0;
                    }
                }
                newkeyData[newPos] = temp;
            } else {
                newkeyData[newPos] = keyData[i];
            }
        }
        this.keyData = newkeyData;
    }

    private int findCharPos(char c){
        char[] OGArr = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0'};
        int i = 0;
        for(; i < OGArr.length ; i++){
            if(OGArr[i] == c){
                break;
            }
        }
        return i+1;
    }

    public byte[] getNFCKey(){
        return encrypt(keyData.toString().getBytes());
    }

    private byte[] encrypt(byte[] data){
        return data;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public void setEnd(String string) {
        end.setTimeInMillis(Long.parseLong(string.trim()));
    }

    public void setKeyData(String string) {
        this.keyData = string.toCharArray();
    }

    public void setEncryptionKey(String string) {
        this.encryptionKey = string.getBytes();
    }

    public void setActive(int anInt) {
        this.active = Boolean.parseBoolean(""+anInt);
    }

    public void setTemp(int anInt) {
        if(anInt == 1){
            temp = true;
        }else {
            temp = false;
        }
    }

    public String getDateString() {
        return end.toString();
    }

    public void setValid(){
        this.keyData = null;
        char[] tmp = {'t','o','g','g','l','e'};
        this.keyData = tmp;
        System.out.print(this.keyData.toString());
    }

    public char[] getkeyData() {
        return "toggle".toCharArray();
    }

    public void setEnd(int i) {
        temp = true;
        this.end.add(Calendar.MINUTE, i);
    }
    public String getEndString(){
        int y = end.get(Calendar.YEAR);
        int m = end.get(Calendar.MONTH)+1;
        int d = end.get(Calendar.DAY_OF_MONTH);
        int hours = end.get(Calendar.HOUR_OF_DAY);
        String h = ""+hours;
        if(hours < 10){
            h = "0"+hours;
        }
        int minutes = end.get(Calendar.MINUTE);
        String mins = ""+minutes;
        if(minutes < 10){
            mins = "0"+minutes;
        }
        int seconds = end.get(Calendar.SECOND);
        String secs = ""+seconds;
        if(seconds < 10){
            secs = "0"+seconds;
        }
        return ""+d+"/"+m+"/"+y+"    "+h+":"+mins+":"+secs;
    }
}
