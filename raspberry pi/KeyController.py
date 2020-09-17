import DBQueryBuilder as DB
import thread
import time
import IO
import RPi.GPIO as GPIO
import HCE_MFRC522_Interface
import CommandInterpretor
import re


class KeyController:

  CurrentCommand = None
  QB = DB.DBQueryBuilder()
  HCE = None
  commands = CommandInterpretor.CommandInterpretor()
  VehID = None
  Veh_Name = None
    
  def User_Input(self):
    while(self.program_on()):
      user_in = raw_input("Please enter a command or type \"-help\" to view options:\n")
      if(user_in.strip() != ""):
        values = self.commands.user_command(user_in)
        if values[0] != self.commands.ERROR:
          if values[0] == self.commands.CREATE:
            if(self.QB.verify_pin(self.VehID, values[1])):
              self.HCE.CurrentCommand = values
              self.CurrentCommand = values
              print("\n\nTouch a smart device ready to accept a key or enter a new command to stop key pairing process")
              print("Creating key")
          if values[0] == self.commands.CREATE_TEMP:
            if(self.QB.verify_pin(self.VehID, values[1])):
              self.HCE.CurrentCommand = values
              self.CurrentCommand = values
              print("\n\nTouch a smart device ready to accept a key or enter a new command to stop key pairing process")
              print("Creating temporary key")
          elif values[0] == self.commands.QUIT:
            self.CurrentCommand = values
          elif values[0] == self.commands.HELP:
            IO.print_help()
            self.CurrentCommand = None
        else:
          self.CurrentCommand = None
          print("\n\nError: command error")
  
  def program_on(self):
    if self.CurrentCommand == None:
      return 1
    else: 
      if(len(self.CurrentCommand) > 0):
        if (self.CurrentCommand[0] == self.commands.QUIT):
          return 0
        else: 
          return 1  
      else: 
        return 1  
    
    
    
  def main(self):
    text = None
    while self.VehID == None:
      text = raw_input("Enter a valid id to emulate: \n")
      text = str(text).strip()
      if(re.match('^ *\d[\d ]*$', text)):
        if(self.QB.check_vehID(text)):
          self.VehID = text
      
    
    self.HCE = HCE_MFRC522_Interface.HCE_MFRC522_Interface()
    self.Veh_Name = self.QB.get_veh_name(self.VehID)
    self.HCE.Veh_Name = self.Veh_Name
    self.HCE.VehID = self.VehID
    
    try:
       inputThread = thread.start_new_thread( self.User_Input, () )
       while(self.program_on()):
         ret = self.NFC_Reader_Start()
         if self.program_on() or ret == self.commands.SUCCESS:
           self.CurrentCommand = None
           self.HCE.CurrentCommand = None
           self.HCE.count = 0
           time.sleep(3)
       inputThread.join()
    except:
       print "Program Ending"
    finally:
            GPIO.cleanup()
            
  def NFC_Reader_Start(self):
    id = self.HCE.HCE_write()    
    while not id and self.program_on():
        id = self.HCE.HCE_write()  
    return id
    
