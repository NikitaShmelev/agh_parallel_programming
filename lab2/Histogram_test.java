import java.util.Scanner;

class Histogram_test {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Set image size: n (#rows), m (#columns)");
        int n = scanner.nextInt();
        int m = scanner.nextInt();
        Obraz obraz_1 = new Obraz(n, m);

        // Вариант 1: Секвенциально + по одному символу на поток (extends Thread)
        runSequentialVersion(obraz_1);
        runParallelThreadVersion(scanner, obraz_1);

        // Вариант 2: Runnable + диапазон символов на поток (implements Runnable)
        System.out.println("\n\n--- Now running Version 2 (Runnable, block decomposition) ---");
        Obraz obraz_2 = new Obraz(n, m);  // Новый объект с теми же размерами
        runSequentialVersion(obraz_2);
        runParallelRunnableVersion(scanner, obraz_2);
    }

    // Последовательный расчёт и вывод
    public static void runSequentialVersion(Obraz obraz) {
        obraz.clear_histogram();
        obraz.calculate_histogram();
        System.out.println("\nSequential histogram:");
        // obraz.print_histogram();
    }

    // Вариант 1 – потоки по одному символу (класс Watek не включён, но можно добавить отдельно)
    public static void runParallelThreadVersion(Scanner scanner, Obraz obraz) {
        System.out.println("\n[Version 1] Set number of threads for Thread-per-character:");
        int num_threads = scanner.nextInt();

        Watek[] threads = new Watek[num_threads];
        int charsPerThread = 94 / num_threads;
        int remainingChars = 94 % num_threads;
        int startChar = 0;

        for (int i = 0; i < num_threads; i++) {
            int endChar = startChar + charsPerThread + (i < remainingChars ? 1 : 0);
            threads[i] = new Watek(startChar, endChar, obraz);
            threads[i].start();
            startChar = endChar;
        }

        for (int i = 0; i < num_threads; i++) {
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.println("\n[Version 1] Parallel histogram results:");
        if (obraz.compare_histograms()) {
            System.out.println("Histograms match!");
        } else {
            System.out.println("Histograms don't match!");
        }
    }

    // Вариант 2 – Runnable + диапазон символов на поток
    public static void runParallelRunnableVersion(Scanner scanner, Obraz obraz) {
        System.out.println("\n[Version 2] Set number of threads for Runnable block decomposition:");
        int num_threads = scanner.nextInt();

        Thread[] threads = new Thread[num_threads];
        int charsPerThread = 94 / num_threads;
        int remainingChars = 94 % num_threads;
        int startChar = 0;

        for (int i = 0; i < num_threads; i++) {
            int endChar = startChar + charsPerThread + (i < remainingChars ? 1 : 0);
            Runnable task = new HistogramRunnable(startChar, endChar, obraz);
            threads[i] = new Thread(task, "Runnable-" + i);
            startChar = endChar;
        }

        for (int i = 0; i < num_threads; i++) {
            threads[i].start();
        }

        for (int i = 0; i < num_threads; i++) {
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.println("\n[Version 2] Parallel histogram results:");
        if (obraz.compare_histograms()) {
            System.out.println("Histograms match!");
        } else {
            System.out.println("Histograms don't match!");
        }
    }
}
