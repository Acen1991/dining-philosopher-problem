package remote.server.fork;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Fork implements Runnable {

	private boolean taken = false;

	private static int portCounter = 0;
	private int forkNum = 0;
	private PrintStream ps;
	private int port;

	public Fork(String philosopherName, PrintStream ps) {
		forkNum = portCounter;
		portCounter++;
		this.ps = ps;
		ps.println("I am fork num " + port + " and I am at "
				+ philosopherName + "'s left side");
	}

	public Fork(int port) {
		this.port = port;
	}

	public synchronized void toBeTaken(String philosopherName) {
		try {
			while (true) {

				if (this.taken) {
					ps.println("I am fork num " + port
							+ " and I cannot yet be taken by "
							+ philosopherName);
					wait();
				} else {
					ps.println("I am fork num " + port
							+ " and I have been taken by " + philosopherName);
					this.taken = true;
					return;
				}
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public synchronized void toBeDropped(String philosopherName) {
		this.taken = false;
		ps.println("I am fork num " + port + " and I have been dropped by "
				+ philosopherName);
		notifyAll();
	}

	@Override
	public String toString() {
		return "Fork [port=" + port + "]";
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
			Socket socket = new ServerSocket(port).accept();
			do {
				unmarshal(socket);
			} while (true);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private synchronized void unmarshal(Socket socket) {
		try {

			BufferedReader rd = new BufferedReader(new InputStreamReader(
					socket.getInputStream()));

			BufferedWriter wr = new BufferedWriter(new OutputStreamWriter(
					socket.getOutputStream()));

			while (true) {
				String va = rd.readLine();
				if (va != null) {
					System.out.println(va);
					if (va.contains("take")) {
						if (this.taken) {
							wr.write("I am + " +port+" SORRY I am taken\n");
							System.out.println("I am + " +port+" SORRY I have been taken");
							wr.flush();
							continue;
						} else {
							System.out.println("I am + " +port+" ACK I have been taken");
							this.taken = true;
						}
					} else if (va.contains("drop")) {
						if (this.taken) {
							System.out.println("I am + " +port+" ACK I have been dropped");
							this.taken = false;
						} else {
							wr.write("I am + " +port+" SORRY I have already been dropped\n");
							System.out.println("I am + " +port+" SORRY I have already been dropped");
							wr.flush();
							continue;
						}
					} else {
						System.out.println("I am + " +port+" ACK");
					}
					wr.write(port+" ACK\n");
					
					wr.flush();
					break;
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private synchronized void processOld(Socket socket) {
		try {

			BufferedReader rd = new BufferedReader(new InputStreamReader(
					socket.getInputStream()));

			BufferedWriter wr = new BufferedWriter(new OutputStreamWriter(
					socket.getOutputStream()));

			while (true) {
				String va = rd.readLine();
				if (va != null) {
					System.out.println(va);
					wr.write("ACK\n");
					wr.flush();
					break;
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
