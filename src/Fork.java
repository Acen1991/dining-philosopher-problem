public class Fork {

	private boolean taken = false;

	private static int forkNumCounter = 0;
	private int forkNum = 0;

	public Fork(String philosopherName) {
		System.out.println("I am fork num " + forkNum + " and I am at "
				+ philosopherName + "'s left side");
		forkNum = forkNumCounter;
		forkNumCounter++;
	}

	public synchronized void toBeTaken(String philosopherName) {
		try {
			while (true) {

				if (this.taken) {
					System.out.println("I am fork num " + forkNum
							+ " and I cannot yet be taken by "
							+ philosopherName);
					wait();
				} else {
					System.out.println("I am fork num " + forkNum
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
		System.out.println("I am fork num " + forkNum
				+ " and I have been dropped by " + philosopherName);
		notifyAll();
	}

	@Override
	public String toString() {
		return "Fork [forkNum=" + forkNum + "]";
	}

}
