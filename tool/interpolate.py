import math

def inter(x,y,vx,vy,t):
	if t==x:
		return vx
	else:
		return 1.0*(vy-vx)/(y-x)*(t-x)+vx

print(inter(42231,42417,192,378,42255))
