package StoneEngineUtil;

public class Util 
{
	public static int Clamp(int val, int min, int max) 
	{
		if 		(val < min)	val = min;
		else if (val > max)	val = max;
		return val;
	}
	public static float Clamp(float val, float min, float max) 
	{
		if 		(val < min)	val = min;
		else if (val > max)	val = max;
		return val;
	}
}
