public class Position 
{
	//[0] = x, [1] = y, [2] = z
	public float[] rotations { get; set;}
	public float[] accelerations{ get; set; }

	public Position()
	{
		rotations = new float[3];
		accelerations = new float[3];
	}

	public void parseMessage(string message)
	{
		char[] delims = {':'};
		char[] delims2 = {','};
		string[] keyVal = message.Split (delims);
		string[] XYZ = keyVal [1].Split (delims2);

		float[] newVals = new float[3];

		newVals [0] = float.Parse (XYZ [0]);
		newVals [1] = float.Parse (XYZ [1]);
		newVals [2] = float.Parse (XYZ [2]);

		if(keyVal[0].Equals("ACCEL"))
		{
			//accelerations = newVals;
		}
		else if(keyVal[0].Equals("ROT"))
		{
			rotations = newVals;
		}
	}
}
