package remote.server.fork;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;

import common.utils.Action;
import common.utils.StreamUtils;

public class Fork implements Runnable {

	private boolean taken = false;
	private int port;

	public Fork(int port) {
		this.port = port;
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
					// This is really really really not meant to be productive
					// !!!
					if (va.toUpperCase().contains(Action.TAKE.name())) {
						if (this.taken) {
							StreamUtils.writeIntoStreamAndLog(wr, port
									+ " SORRY has already been taken\n");
							continue;
						} else {
							StreamUtils.writeIntoStreamAndLog(wr, port
									+ " ACK has been taken");
							this.taken = true;
						}
					} else if (va.toUpperCase().contains(Action.DROP.name())) {
						if (this.taken) {
							StreamUtils.writeIntoStreamAndLog(wr, port
									+ " ACK has been dropped");
							this.taken = false;
						} else {
							StreamUtils
									.writeIntoStreamAndLog(
											wr,
											port
													+ " SORRY has already been dropped (BUG)\n");
							continue;
						}
					} else {
						StreamUtils.writeIntoStreamAndLog(wr, port
								+ "(BUGGED)\n");
					}

					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
