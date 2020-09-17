import re
import IO

class CommandInterpretor:

  ERROR = 0
  QUIT = 1
  CREATE = 2
  CREATE_TEMP = 3
  HELP = 4
  
  
  
  
  def user_command(self, command):
    words = re.split("\s", command)
    if len(words) > 0:
      if re.search("^-[a-zA-Z]", words[0]):
        commandChar = "".join(re.split("-", words[0])).lower()
        if commandChar == "c" :
           return self.process_create(words)
        if commandChar == "t" :
           return self.process_temp(words)
        elif commandChar == "q":
          return [self.QUIT]
        elif commandChar == "help":
          return [self.HELP]
      else: 
        IO.print_help_error("Error: no command detected please follow guidlines")
    return [self.ERROR]
    
  def process_create(self, words):
    if(len(words) > 2):
      if re.search("[0-9]{4}", words[1]) and len(words[1]) == 4:
        if len(words[2]) > 0:
          return [self.CREATE, words[1], words[2]]
        else:
          return [self.ERROR]
      else: 
        return [self.ERROR]
    else:
      return [self.ERROR]
    
  def process_temp(self, words):
    if(len(words) > 3):
      if re.search("[0-9]{4}", words[1]) and len(words[1]) == 4:
        if len(words[2]) > 0:
          if re.search("[0-9]+", words[3]):
            return [self.CREATE_TEMP, words[1], words[2], words[3]]
        else:
          return [self.ERROR]
      else: 
        return [self.ERROR]
    else:
      return [self.ERROR]