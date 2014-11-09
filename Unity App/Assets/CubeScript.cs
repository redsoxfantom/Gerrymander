﻿using UnityEngine;
using System.Collections;

public class CubeScript : MonoBehaviour 
{
	public ServerScript server;
	public Vector3 rotationDamp;
	public Vector3 translationDamp;

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

		transform.localEulerAngles = Vector3.Scale(new Vector3 (xrot,yrot,zrot),rotationDamp);

		transform.localPosition = Vector3.Scale(new Vector3 (xpos, ypos, zpos), translationDamp);
	}
}
