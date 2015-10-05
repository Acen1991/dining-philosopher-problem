import java.io.PrintStream;
import java.util.Random;

public class Philosopher implements Runnable {
	private Fork leftFork;
	private Fork rightFork;
	private State state;
	Random randomFunc = new Random();
	public static int numEat = 0;
	private String name;
	private PrintStream pw;

	public Philosopher(String name) {
		this.name = name;
	}

	public void setPrintStream(PrintStream pw) {
		this.pw = pw;
		leftFork = new Fork(name, pw);
	}

	public Fork getLeftFork() {
		return leftFork;
	}

	public void setLeftFork(Fork leftFork) {
		this.leftFork = leftFork;
	}

	public Fork getRightFork() {
		return rightFork;
	}

	public void setRightFork(Fork rightFork) {
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

			int sleepTime = randomFunc.nextInt(256);

			pw.println("I am " + this.name + " and I am thinking for "
					+ sleepTime + " milliseconds");

			try {
				Thread.sleep(sleepTime);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			this.state = State.STARVING;

			this.leftFork.toBeTaken(this.name);
			this.rightFork.toBeTaken(this.name);

			this.state = State.EATING;
			numEat++;

			sleepTime = randomFunc.nextInt(256);

			pw.println("I am " + this.name + " and I am eating for " + " "
					+ sleepTime + " milliseconds");

			try {
				Thread.sleep(sleepTime);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			this.leftFork.toBeDropped(name);
			this.rightFork.toBeDropped(name);
		}

	}

	@Override
	public String toString() {
		return "Philosopher [leftFork=" + leftFork + ", rightFork=" + rightFork
				+ ", name=" + name + "]";
	}

}
