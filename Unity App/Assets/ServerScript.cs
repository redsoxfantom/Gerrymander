using UnityEngine;
using System.Threading;
using System.Net.Sockets;
using System.IO;
using System.Collections.Generic;
using System.Text;
public delegate void OnReceiveMessage(string message);


public class ServerScript : MonoBehaviour
{
	private OnReceiveMessage onReceiveMessage;
	private bool running;
	public int port;
	private Thread mThread;
	private TcpListener tcp_Listener = null;
	private List<Client> arrReader;
	public Position p;

	private string outStr = "NOT CONNECTED";
	
	void OnGUI()
	{
		GUI.Label (new Rect (10, 10, 200, 20), ("TEST" + outStr));
	}

	void Start()
	{
		p = new Position ();
		this.onReceiveMessage = onReceiveMessageHandler;
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
	
	void ClientCheck()
	{
		try
		{
			tcp_Listener = new TcpListener(port);
			tcp_Listener.Start();
			while (running)
			{
				TcpClient client = tcp_Listener.AcceptTcpClient();
				Debug.Log("accepted");
				arrReader.Add(new Client(client , onReceiveMessage));
			}
		}
		catch (ThreadAbortException)
		{
			Debug.Log("exception");
		}
		finally
		{
			running = false;
			tcp_Listener.Stop();
		}
	}

	public void StopListening()
	{
		running = false;
		mThread.Join(500);
	}
}


public class Client
{
	private Thread tread;
	private StreamReader streamReader;
	private bool running = true;
	private OnReceiveMessage onReceiveMessage;
	private NetworkStream ns;

	public Client (TcpClient client , OnReceiveMessage onReceiveMessage)
	{
		this.onReceiveMessage = onReceiveMessage;
		ns = client.GetStream();
		streamReader = new StreamReader(ns);
		ThreadStart ts = new ThreadStart(StartTread);
		tread = new Thread(ts);
		tread.Start();
	}

	private void StartTread()
	{
		while(running)
		{
			string cmsg = streamReader.ReadLine();
			Debug.Log(cmsg);
			onReceiveMessage(cmsg);
		}
	}
}