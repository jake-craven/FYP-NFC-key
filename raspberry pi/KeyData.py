import random

class KeyData(object):
  
  
  keyChars = ['a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0']
  size = 250
  key = []
  roller = 1
  
  def __init__(self):
    self.gen_key()
    self.roller = random.randint(1,101)
    
  def gen_key(self):
    x = 0
    while x < self.size:
      x = x+1
      self.key.append(random.choice(self.keyChars))
    
    
    def roll_key(self):
      print('Key rolled')