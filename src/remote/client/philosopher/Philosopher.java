package remote.client.philosopher;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Random;

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
		// TODO Auto-generated method stub

		while (numEat < 10) {
			this.state = State.THINKING;

			int sleepTime = randomFunc.nextInt(maxSpleepTime);

			pw.println(this.name.toUpperCase() + "\t"+ this.state + "\t" + sleepTime);

			try {
				Thread.sleep(sleepTime);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			this.state = State.STARVING;

			actionFork(this.leftFork, "take");
			actionFork(this.rightFork, "take");

			this.state = State.EATING;
			numEat++;

			sleepTime = randomFunc.nextInt(maxSpleepTime);
			
			pw.println(this.name.toUpperCase() + "\t"+ this.state + "\t" + sleepTime);

			try {
				Thread.sleep(sleepTime);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			actionFork(this.leftFork, "drop");
			actionFork(this.rightFork, "drop");
		}

	}

	private void actionFork(Socket fork, String takeOrDrop) {
		try {
			BufferedWriter wrLeft = new BufferedWriter(new OutputStreamWriter(
					fork.getOutputStream()));
			BufferedReader rdLeft = new BufferedReader(new InputStreamReader(
					fork.getInputStream()));

			wrLeft.write(name.toUpperCase() + "\t" + takeOrDrop.toUpperCase()
					+ "\t" + fork.getPort() + "\n");
			
			wrLeft.flush();

			while (true) {
				String resLeft = rdLeft.readLine();
				if (resLeft != null) {
					if (resLeft.toUpperCase().contains("ACK")) {
						pw.println(name.toUpperCase()+"\t"+takeOrDrop.toUpperCase()+ "\t" + fork.getPort());
						break;
					} else if (resLeft.toUpperCase().contains("SORRY")) {
						pw.println(name + "\tcannot" + takeOrDrop.toUpperCase() + "\t" + fork.getPort());
						Thread.yield();
					}
				}
			}
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	@Override
	public String toString() {
		return "Philosopher [leftFork=" + leftFork + ", rightFork=" + rightFork
				+ ", name=" + name + "]";
	}

}
