public class PhilosopherDilemmaApp {

	public static void main(String[] args) {

		int numPhilosopher = 5;
		Philosopher[] philosophers = new Philosopher[numPhilosopher];
		String[] philosophersNames = new String[] { "Epicure", "Cicéron",
				"Platon", "Aristote", "Socrate" };

		for (int i = 0; i < numPhilosopher; i++) {
			philosophers[i] = new Philosopher(philosophersNames[i]);
			if (i != 0) {
				philosophers[i].setRightFork(philosophers[i - 1].getLeftFork());
			}
		}
		philosophers[0].setRightFork(philosophers[numPhilosopher - 1]
				.getLeftFork());

		for (Philosopher philosopher : philosophers) {
			// System.out.println(philosopher);
			Thread threadPerPhilosopher = new Thread(philosopher);
			threadPerPhilosopher.start();
		}

	}
}
