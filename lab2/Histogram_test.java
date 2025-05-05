import java.util.Scanner;

class Histogram_test {
	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);

		System.out.println("Set image size: n (#rows), m(#columns)");
		int n = scanner.nextInt();
		int m = scanner.nextInt();
		Obraz obraz_1 = new Obraz(n, m);

		obraz_1.calculate_histogram();
		System.out.println("Sequential histogram:");
		obraz_1.print_histogram();

		System.out.println("\nSet number of threads");
		int num_threads = scanner.nextInt();

		Watek[] NewThr = new Watek[num_threads];
		int charsPerThread = 94 / num_threads;
		int remainingChars = 94 % num_threads;
		int startChar = 0;

		for (int i = 0; i < num_threads; i++) {
			int endChar = startChar + charsPerThread + (i < remainingChars ? 1 : 0);
			NewThr[i] = new Watek(startChar, obraz_1);
			startChar = endChar;
		}

		for (int i = 0; i < num_threads; i++) {
			NewThr[i].start();
		}

		for (int i = 0; i < num_threads; i++) {
			try {
				NewThr[i].join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		System.out.println("\nParallel histogram results:");
		if (obraz_1.compare_histograms()) {
			System.out.println("Histograms match!");
		} else {
			System.out.println("Histograms don't match!");
		}
	}
}