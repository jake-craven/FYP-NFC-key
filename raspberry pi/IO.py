import os

def cls():
  os.system('cls' if os.name=='nt' else 'clear')

def out(text):
  cls()
  print(">"+text)
  print_input_message()

def print_input_message():
  print("Please enter a command or type \"-help\" to view options:\n")

def print_help():
  print("\nEnter one of the following commands:")
  print("-- \"-c <PIN> <UserName>\"                     to create and add a new user key")
  print("-- \"-t <PIN> <UserName> <No. of Minutes>\"    to create and add a new temporary user key")
  print("-- \"-q\"                                      to end the program")
  print("-- \"-help\"                                   to view options")
  
def print_help_error(text):
  cls()
  print(text)
  print_help()