package remote.server.fork;

import java.util.ArrayList;
import java.util.List;

public class ProxyServer {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		List<Thread> threadpool = new ArrayList<>();

		for (int i = 0; i < 5; i++) {
			Thread server = new Thread(new Fork(50000 + i));
			threadpool.add(server);
			server.start();
		}

		for (Thread t : threadpool) {
			try {
				t.join();
			} catch (InterruptedException e) { // TODO Auto-generated catch
												// block
				e.printStackTrace();
			}
		}

	}

}
