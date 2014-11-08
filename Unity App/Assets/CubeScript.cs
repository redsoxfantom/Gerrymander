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
		float xrot = server.xrot;
		float yrot = server.yrot;
		float zrot = server.zrot;

		transform.localEulerAngles = new Vector3 (xrot,yrot,zrot);
	}
}
