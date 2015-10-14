package remote.client.philosopher;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Arrays;
import java.util.Random;

import common.utils.Action;
import common.utils.State;
import common.utils.StreamUtils;

public class Philosopher implements Runnable {
	private Socket leftFork;
	private Socket rightFork;
	private State state;
	Random randomFunc = new Random();
	public static int numEat = 0;
	private String name;
	private PrintStream pw;
	private static int maxSpleepTime = 256;

	public Philosopher(String name, Socket leftFork) {
		this.name = name;
		this.leftFork = leftFork;
	}

	public void setPrintStream(PrintStream pw) {
		this.pw = pw;
	}

	public Socket getLeftFork() {
		return leftFork;
	}

	public void setLeftFork(Socket leftFork) {
		this.leftFork = leftFork;
	}

	public Socket getRightFork() {
		return rightFork;
	}

	public void setRightFork(Socket rightFork) {
		this.rightFork = rightFork;
	}

	public State getState() {
		return state;
	}

	public void setState(State state) {
		this.state = state;
	}

	@Override
	public void run() {
		while (numEat < 10) {
			this.state = State.THINKING;

			int sleepTime = randomFunc.nextInt(maxSpleepTime);

			pw.println(this.name.toUpperCase() + "\t" + this.state + "\t"
					+ sleepTime);

			try {
				Thread.sleep(sleepTime);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			this.state = State.STARVING;

			actOnFork(this.leftFork, Action.TAKE);
			actOnFork(this.rightFork, Action.TAKE);

			this.state = State.EATING;
			numEat++;

			sleepTime = randomFunc.nextInt(maxSpleepTime);

			pw.println(this.name.toUpperCase() + "\t" + this.state + "\t"
					+ sleepTime);

			try {
				Thread.sleep(sleepTime);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			actOnFork(this.leftFork, Action.DROP);
			actOnFork(this.rightFork, Action.DROP);
		}

	}

	private synchronized void actOnFork(Socket fork, Action action) {
		try {
			BufferedWriter bufferedWriter = new BufferedWriter(
					new OutputStreamWriter(fork.getOutputStream()));
			BufferedReader bufferedReader = new BufferedReader(
					new InputStreamReader(fork.getInputStream()));

			StreamUtils.writeIntoStreamAndLog(pw, bufferedWriter,
					name.toUpperCase() + "\tWANT TO " + action.name() + "\t"
							+ fork.getPort() + "\n");

			while (true) {
				String resLeft = bufferedReader.readLine();
				if (resLeft != null) {
					if (resLeft.toUpperCase().contains("ACK")) {
						pw.println(name.toUpperCase() + "\t" + action.name()
								+ "\t" + fork.getPort());
						break;
					} else if (resLeft.toUpperCase().contains("SORRY")) {
						pw.println(name.toUpperCase() + "\tcannot"
								+ action.name() + "\t" + fork.getPort());
						Thread.yield();
					}
				}
			}
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

}
