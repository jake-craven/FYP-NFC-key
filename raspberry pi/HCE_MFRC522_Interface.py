import MFRC522
import RPi.GPIO as GPIO

import numpy as np
import time as pause
import KeyData
import DBQueryBuilder as DB
import Lock
import re
import IO
class HCE_MFRC522_Interface:

  READER = None;
  
  KEY = [0xFF,0xFF,0xFF,0xFF,0xFF,0xFF]
  BLOCK_ADDRS = [8, 9, 10]
  ANDROID_AID = [0xF0,0x01,0x02,0x03,0x04,0x05,0x06]
  ANDROID_ADPU = [0x00,0xB0,0x00,0x00,0x0F,0xBE,0xEF]
  
  CurrentCommand = None
  QUIT = 1
  CREATE = 2
  CREATE_TEMP = 3
  SUCCESS = 1    
  
  Device_Serial = None
  PIN = None
  
  
  Veh_Name = None
  VehID = None
  Key = None
  
  counter = 0
  lock = Lock.Lock()
  
  QB = DB.DBQueryBuilder()
  
  def get_bytes(self, s):
    lst = []
    for ch in s:
        hv = ord(ch)
        lst.append(int(float(hv)))
    #print("Bytes: "+str(lst))
    return lst
  
  def __init__(self):
    self.READER = MFRC522.MFRC522()
  
  def addLoop(self):
    id = 0
    while 1:
      id, test = self.addLoopC()        
      while not id:
        id, test = self.addLoopC() 
        if(id == None):
          id = 0
      #print("\n\n\nWaiting")
      pause.sleep(1)
      #print("Touch NFC\n\n\n")   
    return id
    
  def write(self):
    id, test = self.write_no_block()       
    return id
    
  def HCE_write(self):
    id = self.HCE_basics()
    if(id == self.SUCCESS):
      self.counter = self.counter+1
      #print("Card Authorised")
      if self.CurrentCommand == None:
        ret = self.HCE_default()
        if(ret != self.SUCCESS):
          IO.out("("+str(self.counter)+".)Communication failed please retouch device")
        return ret
      elif(len(self.CurrentCommand) > 0):
        if (self.CurrentCommand[0] == self.QUIT):
          return None
        elif (self.CurrentCommand[0] == self.CREATE):
          ret = self.HCE_Create()
          if(ret != self.SUCCESS):
            IO.out("("+str(self.counter)+".)Creation failed please retouch device")
          return ret
        elif (self.CurrentCommand[0] == self.CREATE_TEMP):
          ret = self.HCE_Create_temp()
          if(ret != self.SUCCESS):
            IO.out("("+str(self.counter)+".)Creation failed please retouch device")
          return ret
        else:
          return self.HCE_default()
          if(ret != self.SUCCESS):
            IO.out("("+str(self.counter)+".)Communication failed please retouch device")
          return ret
    else: 
      return None  
          
    return None
    
  def HCE_Create(self):
    self.Key = KeyData.KeyData()
    status, backData = self.HCE_send_AID()
    if status == self.SUCCESS:
      self.Device_Serial = re.sub('[\W_]+', '', str(''.join(chr(i) for i in backData).lower().strip()))
      text = "create@@"+self.Veh_Name+"@@"+str(self.VehID)
      status, backData = self.HCE_APDU(self.get_bytes(text))
      if status == self.SUCCESS:
        #print("".join(chr(i) for i in backData))
        if("".join(chr(i) for i in backData).lower() == "dupe".lower()):
          if(self.QB.user_exists(self.VehID, self.Device_Serial)):
            IO.out("> This user already has a valid key, touch another device to add key")
            return None
        status, backData = self.HCE_APDU(self.get_bytes(text))
        self.QB.create_user(self.CurrentCommand[2], self.Device_Serial, self.VehID, self.Key.roller, self.Key.key)
        IO.out("Successfully created key task")
        return self.SUCCESS
    return None
      
    
  def HCE_Create_temp(self):
    self.Key = KeyData.KeyData()
    status, backData = self.HCE_send_AID()
    if status == self.SUCCESS:
      self.Device_Serial = re.sub('[\W_]+', '', str(''.join(chr(i) for i in backData).lower().strip()))
      text = "create@@"+self.Veh_Name+"@@"+str(self.VehID)+"@@"+self.CurrentCommand[3]
      status, backData = self.HCE_APDU(self.get_bytes(text))
      if status == self.SUCCESS:
        if(re.search("dupe", re.sub('[\W_]+', '', str(''.join(chr(i) for i in backData).lower().strip())).lower())):
          if(self.QB.user_exists(self.VehID, self.Device_Serial)):
            IO.out("> This user already has a valid key, touch another device to add key")
            return None
        #print(text)
        status, backData = self.HCE_APDU(self.get_bytes(text))
        self.QB.create_temp_user(self.CurrentCommand[2], self.Device_Serial, self.VehID, self.Key.roller, self.Key.key, self.CurrentCommand[3])
        IO.out("Successfully created key task")
        return self.SUCCESS
    return None
      
      
  
  
  
  def HCE_basics(self):
    (status, TagType) = self.READER.MFRC522_Request(self.READER.PICC_REQALL)
    if status != self.READER.MI_OK:
        return 0
      
    (status, uid) = self.READER.MFRC522_Anticoll()
    if status != self.READER.MI_OK:
        return 0
      
      
    id = self.uid_to_num(uid)
    self.READER.MFRC522_SelectTag(uid)
    status = self.READER.MFRC522_Authorise_Android()
    if status != self.READER.MI_OK:
        return 0
    return self.SUCCESS
      
  def HCE_default(self):
    status, backData = self.HCE_send_AID()
    if status == self.SUCCESS:
      self.Device_Serial = re.sub('[\W_]+', '', str(''.join(chr(i) for i in backData).lower().strip()))
      text = "default@@"+str(self.VehID)
      status, backData = self.HCE_APDU(self.get_bytes(text))
      if status == self.SUCCESS:
        text = re.sub('[\W_]+', '', str(''.join(chr(i) for i in backData).lower().strip()))
        if(re.search("toggle", text)):
          if(self.QB.verify_temp_key(self.VehID, self.Device_Serial) or self.QB.verify_device_key(self.VehID, self.Device_Serial)):
            self.lock.toggle()
            status, backData = self.HCE_APDU(self.get_bytes("default"))
            return self.SUCCESS
          else:
            IO.out("Key not recognised")
            return None
        else:
          IO.out("Device doesn't have a valid key")
          return self.SUCCESS
    return None
    
  def HCE_send_AID(self):
    status, backData = self.READER.MFRC522_SelectAID()
    if status == 2:
        #print("4.) Failed AID Selection")
        return 0, []
    else: 
      #print("4.) Sussessful AID Selection")
      status, backData = self.AndroidRetry(status, backData)
      if status == 2:
          return 0, []
      else: 
        return self.SUCCESS, backData
  
  def HCE_APDU(self, apdu):
    status, backData, sentData = self.READER.MFRC522_ANDROID_ADPU(apdu)
    if status != self.READER.MI_OK:
      #print("5.) Failed ADPU Selection")
      return 0, []
    else: 
      #print("5.) Sussessful ADPU Selection")
      status, backData = self.AndroidRetry(status, backData)
      if status != self.READER.MI_OK:
          #print("\t-Failed retry")
          return 0, []
      else: 
        #print("\t-Sussessful retry")
        return self.SUCCESS, backData
      

    
    
    
  def addLoopC(self):
    (status, TagType) = self.READER.MFRC522_Request(self.READER.PICC_REQALL)
    if status != self.READER.MI_OK:
        #print("1.) Failed Request")
        return 0, None
    #else: 
      #print("1.) Sussessful Request")
      
      
    (status, uid) = self.READER.MFRC522_Anticoll()
    if status != self.READER.MI_OK:
        #print("2.) Failed AntiCollision")
        return 0, None
    #else: 
      #print("2.) Sussessful AntiCollision")
      
      
    id = self.uid_to_num(uid)
    self.READER.MFRC522_SelectTag(uid)
    status = self.READER.MFRC522_Authorise_Android()
    if status != self.READER.MI_OK:
        #print("3.) Failed Authorisation")
        return 0, None
    #else: 
      #print("3.) Sussessful Authorisatsion")
      
    status, backData = self.READER.MFRC522_SelectAID()
    if status != self.READER.MI_OK:
        #print("4.) Failed AID Selection\n")
        return 0, None
    #else: 
      #print("4.) Sussessful AID Selection\n")
      #print( '\tMessage: '.join(chr(i) for i in backData))
      #print( '\tReceived: '+str(backData))
    if self.AndroidRetry(status, backData) != self.READER.MI_OK:
        #print("\t-Failed retry")
        return 0, None
    
    #else: 
      #print("\t-Sussessful retry")
      
      
    while(status != 2):
      self.ANDROID_ADPU[len(self.ANDROID_ADPU)-1] = backData[len(backData)-1]
      status, backData, sentData = self.READER.MFRC522_ANDROID_ADPU(self.ANDROID_ADPU)
      if status != self.READER.MI_OK:
          #print("5.) Failed ADPU Selection")
          #print("\tSendData: "+str(sentData))
          return 0, None
      #else: 
        #print("5.) Sussessful ADPU Selection")
        #print( '\tReceived: '+str(backData))
        
      if self.AndroidRetry(status, backData) != self.READER.MI_OK:
          #print("\t-Failed retry")
          return 0, None
      #else: 
        #print("\t-Sussessful retry")
          
          
    #print( ''.join(chr(i) for i in backData))
    status = self.READER.MFRC522_ANDROID_Deselect()
    return id, "this"
    
    
  def write_no_block(self):
    (status, TagType) = self.READER.MFRC522_Request(self.READER.PICC_REQALL)
    if status != self.READER.MI_OK:
        #print("1.) Failed Request")
        return 0, None
    #else: 
      #print("\n\n1.) Sussessful Request")
      
      
    (status, uid) = self.READER.MFRC522_Anticoll()
    if status != self.READER.MI_OK:
        #print("2.) Failed AntiCollision")
        return 0, None
    #else: 
      #print("2.) Sussessful AntiCollision")
      
      
    id = self.uid_to_num(uid)
    self.READER.MFRC522_SelectTag(uid)
    status = self.READER.MFRC522_Authorise_Android()
    if status != self.READER.MI_OK:
        #print("3.) Failed Authorisation")
        return 0, None
    #else: 
      #print("3.) Sussessful Authorisatsion")
      
    status, backData = self.READER.MFRC522_SelectAID()
    if status != self.READER.MI_OK:
        #print("4.) Failed AID Selection")
        return 0, None
    #else: 
      #print("4.) Sussessful AID Selection")
    
    status, backData = self.AndroidRetry(status, backData) 
    if status == self.READER.MI_ERR:
        #print("\t-Failed retry")
        #print( '\tStatus: '+str(status))
        #print( '\tbackData: '+str(backData))
        return 0, None
    #else: 
      #print("\t-Sussessful retry")
      #print( '\tMessage: '+"".join(chr(i) for i in backData))
      #print( '\tReceived: '+str(backData))
      
      
    status, backData, sentData = self.READER.MFRC522_ANDROID_ADPU(self.ANDROID_ADPU)
    if status != self.READER.MI_OK:
        #print("5.) Failed ADPU Selection")
        #print("\tSendData: "+str(sentData))
        return 0, None
    #else: 
      #print("5.) Sussessful ADPU Selection")
      
    status, backData = self.AndroidRetry(status, backData) 
    if status == self.READER.MI_ERR:
        #print("\t-Failed retry")
        #print( '\tStatus: '+str(status))
        #print( '\tbackData: '+str(backData))
        return 0, None
    #else: 
      #print("\t-Sussessful retry")
      #print( '\tMessage: '+"".join(chr(i) for i in backData))
      #print( '\tReceived: '+str(backData))
          
    status = self.READER.MFRC522_ANDROID_Deselect()
    return id, "this"
      
  def uid_to_num(self, uid):
      n = 0
      for i in range(0, 5):
          n = n * 256 + uid[i]
      return n
  
  def verify_retry(self, status, backData):
    if status == 2 :
      return 0
    if (len(backData) > 0): 
      if backData[0] == 0xF2:
        return 1
    return 0
    
    
  def AndroidRetry(self, status, backData):
    while(self.verify_retry(status, backData) == 1):
      #print("\nAndroid wants more time")
      status, backData = self.READER.MFRC522_ANDROID_SEND_MESSAGE(backData)
    return status, backData
  
      
  def ANDROID_Deselect(self):
      id, test = self.ANDROID_Deselect_Long()        
      while not id:
          id, test = self.ANDROID_Deselect_Long()  
      return id
      
  def ANDROID_Deselect_Long(self):
      (status, TagType) = self.READER.MFRC522_Request(self.READER.PICC_REQALL)
      if status != self.READER.MI_OK:
          return None, None
      (status, uid) = self.READER.MFRC522_Anticoll()
      if status != self.READER.MI_OK:
          return None, None
      id = self.uid_to_num(uid)
      self.READER.MFRC522_SelectTag(uid)
      status = self.READER.MFRC522_ANDROID_Deselect()
      #print("Status: "+ str(status))
      return status, None 
  
