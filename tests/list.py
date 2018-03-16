import disassembler
import sys

def main():
	lst = []
	lst.append(1)
	print(lst)

if len(sys.argv) == 1:
	main()
else:
	disassembler.disassemble(main)
