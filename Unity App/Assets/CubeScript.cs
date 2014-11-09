using UnityEngine;
using System.Collections;

public class CubeScript : MonoBehaviour 
{
	public ServerScript server;

	// Use this for initialization
	void Start () 
	{
	
	}
	
	// Update is called once per frame
	void Update () 
	{
		float xrot = server.p.rotations [0];
		float yrot = server.p.rotations [1];
		float zrot = server.p.rotations [2];

		float xpos = server.p.accelerations [0];
		float ypos = server.p.accelerations [1];
		float zpos = server.p.accelerations [2];

		transform.localEulerAngles = new Vector3 (xrot,yrot,zrot);

		transform.localPosition = new Vector3 (xpos, ypos, zpos);
	}
}
