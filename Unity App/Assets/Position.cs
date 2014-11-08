public class Position 
{
	public float[] rotations { get; set;}
	public float[] accelerations{ get; set; }

	public Position()
	{
		rotations = new float[3];
		accelerations = new float[3];
	}

	public void parseMessage(string message)
	{
		char[] delims = {','};
		string[] splitstrings = message.Split (delims);

		for(int idx = 0; idx < splitstrings.Length; idx++)
		{
			char[] delims2 = {':'};
			string[] keyVal = splitstrings[idx].Split(delims2);
			switch(keyVal[0])
			{
			case "ACCELX":
				break;
			case "ACCELY":
				break;
			case "ACCELZ":
				break;
			case "ROTX":
				rotations[0] = float.Parse(keyVal[1]);
				break;
			case "ROTY":
				rotations[1] = float.Parse(keyVal[1]);
				break;
			case "ROTZ":
				rotations[1] = float.Parse(keyVal[1]);
				break;
			}
		}
	}
}
