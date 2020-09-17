import IO
import mysql.connector


class DBQueryBuilder:
  mydb = mysql.connector.connect(
    host="localhost",
    user="root",
    passwd="",
    database = "smart_key"
  )
  
  
  mycursor = mydb.cursor()
  
  result = None
  ret = None
  
  def identify_user(self, key):
    command = "select UserID from users where keyData = '"+key+"' AND AccessType > 0"
    #print(command)
    self.mycursor.execute(command)
    for x in self.mycursor.fetchone():
      return x
    return None
  
  def verify_pin(self, vehID, PIN):
    self.result = None
    command = "select vehID from vehicles where PIN = %s AND vehID = %s"
    vals = (PIN, vehID)
    self.mycursor.execute(command, vals)
    self.result = self.mycursor.fetchone()
    if(self.result != None):
      for x in self.result:
        return 1
    print(">Error: PIN incorrect")
    return 0 
  
  def user_exists(self, vehID, serial):
    self.result = None
    command = "select vehid from users where keyData = %s AND vehID = %s"
    vals = (serial, vehID)
    self.mycursor.execute(command, vals)
    self.result = self.mycursor.fetchone()
    if(self.result != None):
      for x in self.result:
        return 1
    return 0   
  
  def get_last_user(self, serial, vehID):
    self.result = None
    command = "select MAX(userid) from users where keyData = %s  and vehID = %s"
    vals = (serial, vehID)
    self.mycursor.execute(command, vals)
    self.result = self.mycursor.fetchone()
    if(self.result != None):
      for x in self.result:
        return x
    return None   
    
  def create_user(self, user, serial, vehID, keyRoller, keyData):
    self.delete_dupes(serial, vehID)
    try:
      command = "INSERT INTO users (name, accessType, keyData, vehID) VALUES (%s, %s, %s, %s)"
      vals = (user, 1, serial, vehID)
      self.mycursor.execute(command, vals)
      self.mydb.commit()
    except:
      print()
    ret = self.get_last_user(serial, vehID)
    if(ret != None):
      self.add_key(ret, vehID, keyRoller, keyData)
    
    
  def add_key(self, uID, vehID, roller, key):
    command = "INSERT INTO keyData (vehID, userID, keyRoller, keychars) VALUES (%s, %s, %s, %s)"
    vals = (vehID, uID , roller , "".join(key))
    self.mycursor.execute(command, vals)
    self.mydb.commit()
    
  
    
  def create_temp_user(self, user, serial, vehID, keyRoller, keyData, mins):
    self.delete_dupes(serial, vehID)
    try:
      command = "INSERT INTO users (name, accessType, keyData, vehID) VALUES (%s, %s, %s, %s)"
      vals = (user, 1, serial, vehID)
      self.mycursor.execute(command, vals)
      self.mydb.commit()
    except:
      print()
    ret = self.get_last_user(serial, vehID)
    if(ret != None):
      self.add_temp_key(ret, vehID, keyRoller, keyData, mins)
      
    
  def add_temp_key(self, uID, vehID, roller, key, mins):
    command = "INSERT INTO tempkeys (vehID, userID, keyRoller, keyChars, enddate) VALUES (%s, %s, %s, %s, DATE_ADD(CURRENT_TIMESTAMP(), INTERVAL %s minute))"
    vals = (vehID, uID, roller, "".join(key), mins)
    self.mycursor.execute(command, vals)
    self.mydb.commit()
  
  
  
  def get_veh_name(self, vehID):
    self.result = None
    command = "select brand, model from vehicles where vehID = %s"
    vals = (vehID,)
    self.mycursor.execute(command, vals)
    self.ret = ""
    result = self.mycursor.fetchone()
    if(result != None):
      for x in result:
        self.ret = self.ret+" "+x
    return self.ret
    
  def verify_device_key(self, vehID, serial):
    self.result = None
    command = "select userID from keyData where userID in (select userID from users where vehID = %s and keyData = %s and AccessType > 0)"
    vals = (vehID, serial)
    self.mycursor.execute(command, vals)
    self.result = self.mycursor.fetchall()
    if(self.result != None):
      for x in self.result:
        return 1
    return 0   
  
    
  def verify_temp_key(self, vehID, serial):
    self.result = None
    command = "select userID from tempkeys where userID in (select userID from users where vehID = %s and keyData = %s) and endDate > CURRENT_TIMESTAMP()"
    vals = (vehID, serial)
    self.mycursor.execute(command, vals)
    self.result = self.mycursor.fetchone()
    if(self.result != None):
      for x in self.result:
        return 1
    return 0   
    
    
  def check_vehID(self, vehID):
    self.result = None
    command = "select vehID from vehicles where vehID = %s"
    vals = (vehID,)
    self.mycursor.execute(command, vals)
    self.result = self.mycursor.fetchone()
    if(self.result != None):
      for x in self.result:
        return 1
    print("problem")
    return 0   
    
  def delete_dupes(self, serial, vehID):
    command = "delete from keyData where userID in (select userID from users where keyData = %s ) and vehID = %s"
    vals = (serial, vehID)
    self.mycursor.execute(command, vals)
    self.mydb.commit()
    command = "delete from tempkeys where userID in (select userID from users where keyData = %s ) and vehID = %s"
    vals = (serial, vehID)
    self.mycursor.execute(command, vals)
    self.mydb.commit()
  
  