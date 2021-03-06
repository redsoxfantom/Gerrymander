﻿using UnityEngine;
using System.Threading;
using System.Net.Sockets;
using System.Net;
using System.IO;
using System.Collections.Generic;
using System.Text;
using System;

public delegate void OnDisconnect(Client client);
public delegate void OnReceiveMessage(string message);


public class ServerScript : MonoBehaviour
{
	private OnReceiveMessage onReceiveMessage;
	private OnDisconnect onDisconnect;
	private bool running;
	public int port;
	private Thread mThread;
	private TcpListener tcp_Listener = null;
	private List<Client> arrReader;
	public Position p;

	private string outStr = "NOT CONNECTED";
	
	void OnGUI()
	{
		GUI.Label (new Rect (10, 10, 300, 20), (outStr));
	}

	void Start()
	{
		Application.targetFrameRate = -1;

		p = new Position ();
		onReceiveMessage = onReceiveMessageHandler;
		onDisconnect = onDisconnectHandler;
		running = true;
		arrReader = new List<Client>();
		ThreadStart ts = new ThreadStart(ClientCheck );
		mThread = new Thread(ts);
		mThread.Start();
	}

	void onReceiveMessageHandler(string message)
	{
		outStr = message;
		p.parseMessage (message);
	}

	void onDisconnectHandler(Client client)
	{
		arrReader.Remove (client);
	}
	
	void ClientCheck()
	{
		try
		{
			tcp_Listener = new TcpListener(IPAddress.Any, port);
			tcp_Listener.Start();
			while (running)
			{
				TcpClient client = tcp_Listener.AcceptTcpClient();
				Debug.Log("accepted");
				arrReader.Add(new Client(client , onReceiveMessage, onDisconnect));
			}
		}
		catch (ThreadAbortException)
		{
			Debug.Log("exception");
			running = false;
			tcp_Listener.Stop();
		}
	}

	void OnApplicationQuit()
	{
		Debug.Log ("ShuttingDown");
		StopListening ();
	}

	void StopListening()
	{
		foreach(Client c in arrReader)
		{
			Debug.Log("CLOSING CLIENT "+c);
			c.CloseConnection();
		}
		tcp_Listener.Stop();
		running = false;
		mThread.Abort ();
	}
}


public class Client
{
	private TcpClient client;
	private Thread tread;
	private StreamReader streamReader;
	private bool running = true;
	private OnDisconnect onDisconnect;
	private OnReceiveMessage onReceiveMessage;
	private NetworkStream ns;
	System.Diagnostics.Stopwatch sw;

	public Client (TcpClient client , OnReceiveMessage onReceiveMessage, OnDisconnect onDisconnect)
	{
		sw = System.Diagnostics.Stopwatch.StartNew();
		this.client = client;
		this.onReceiveMessage = onReceiveMessage;
		this.onDisconnect = onDisconnect;
		ns = client.GetStream();
		streamReader = new StreamReader(ns);
		ThreadStart ts = new ThreadStart(StartTread);
		tread = new Thread(ts);
		tread.Start();
	}

	public void CloseConnection()
	{
		tread.Abort ();
		client.Close ();
	}

	private void StartTread()
	{
		try
		{
			while(running)
			{
				double first = (double)sw.ElapsedTicks / (double)System.Diagnostics.Stopwatch.Frequency;
				string cmsg = streamReader.ReadLine();
				double last = (double)sw.ElapsedTicks / (double)System.Diagnostics.Stopwatch.Frequency;
				double total = last-first;

				Debug.Log("It took "+total+" seconds to read " + cmsg);
				onReceiveMessage(cmsg);
			}
		}
		catch(IOException e) // Client disconnected
		{
			Debug.Log(e);
			onDisconnect(this);
		}
	}
}