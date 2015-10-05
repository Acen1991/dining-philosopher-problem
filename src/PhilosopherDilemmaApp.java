import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

public class PhilosopherDilemmaApp {

	public static void main(String[] args) throws InterruptedException,
			IOException {

		int numPhilosopher = 5;
		Philosopher[] philosophers = new Philosopher[numPhilosopher];
		String[] philosophersNames = new String[] { "Epicure", "Cicéron",
				"Platon", "Aristote", "Socrate" };

		PrintStream printStream;

		if (args[0].equals("console")) {
			printStream = System.out;
		} else if (args[0] != null) {
			printStream = new PrintStream(new FileOutputStream(args[0].toString()));
		} else {
			throw new IllegalStateException(
					"you should provide a parameter (console|file) to the java app");
		}

		for (int i = 0; i < numPhilosopher; i++) {
			philosophers[i] = new Philosopher(philosophersNames[i]);
			philosophers[i].setPrintStream(printStream);
			if (i != 0) {
				philosophers[i].setRightFork(philosophers[i - 1].getLeftFork());
			}
		}
		philosophers[0].setRightFork(philosophers[numPhilosopher - 1]
				.getLeftFork());

		List<Thread> threadpool = new ArrayList<>();

		for (Philosopher philosopher : philosophers) {
			Thread threadPerPhilosopher = new Thread(philosopher);
			threadpool.add(threadPerPhilosopher);

			threadPerPhilosopher.start();

		}

		for (Thread t : threadpool) {
			t.join();
		}

		printStream.println("The philosophers have eaten " + Philosopher.numEat
				+ " times");

		printStream.close();
	}
}
