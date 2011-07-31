import os,sys


if len(sys.argv)<2:
	print('filer file')
	sys.exit(1)

f=sys.argv[1]
a=open(f)
b=a.readlines()
for i in range(len(b)):
	if b[i].find("rotate-gun")>-1:
		pass
	elif b[i].find("destroy")>-1:
		pass
	else:
		print(b[i][0:-1])
