import java.util.Random;

public class Philosopher implements Runnable {
	private Fork leftFork;
	private Fork rightFork;
	private State state;
	Random randomFunc = new Random();
	private String name;

	public Philosopher(String name) {
		this.name = name;
		leftFork = new Fork(name);
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

		while (true) {
			this.state = State.THINKING;

			try {
				int sleepTime = randomFunc.nextInt(256);
				System.out.println("I am " + this.name
						+ " and I am thinking for " + sleepTime
						+ " milliseconds");
				Thread.sleep(sleepTime);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			this.state = State.STARVING;

			this.leftFork.toBeTaken(this.name);
			this.rightFork.toBeTaken(this.name);

			this.state = State.EATING;

			try {
				int sleepTime = randomFunc.nextInt(256);
				System.out.println("I am " + this.name
						+ " and I am eating for " + " " + sleepTime
						+ " milliseconds");
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
