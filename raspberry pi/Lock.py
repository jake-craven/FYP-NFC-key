import IO

class Lock:
  locked = 1   
  
  def __init__(self):
    self.lock()
    
  def toggle(self):
    if self.locked:
      self.unlock()
    else : 
      self.lock()
      
  def lock(self):
    self.locked = 1
    IO.out("Locked")
    
  def unlock(self):
    self.locked = 0
    IO.out("Unlocked")
      