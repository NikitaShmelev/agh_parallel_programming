import java.util.Random;
import java.util.Scanner;

// ===== Klasa Obraz =====
class Obraz {
    private int size_n;
    private int size_m;
    private char[][] tab;            // dwuwymiarowa tablica znaków
    private char[] tab_symb;         // znaki ASCII (od 33 do 126)
    private int[] histogram;         // histogram sekwencyjny
    private int[] hist_parallel;     // histogram równoległy

    public Obraz(int n, int m) {
        this.size_n = n;
        this.size_m = m;
        tab = new char[n][m];
        tab_symb = new char[94];

        final Random random = new Random();

        // Wypełniamy tablicę symboli ASCII od '!' (33) do '~' (126)
        for(int k = 0; k < 94; k++) {
            tab_symb[k] = (char)(k + 33);
        }

        // Wypełniamy obraz losowymi znakami i wypisujemy go
        for(int i = 0; i < n; i++) {
            for(int j = 0; j < m; j++) {
                tab[i][j] = tab_symb[random.nextInt(94)];
                System.out.print(tab[i][j] + " ");
            }
            System.out.print("\n");
        }
        System.out.print("\n\n");

        histogram = new int[94];
        hist_parallel = new int[94];
        clear_histogram();
    }

    // Zerowanie obu histogramów
    public void clear_histogram() {
        for(int i = 0; i < 94; i++) {
            histogram[i] = 0;
            hist_parallel[i] = 0;
        }
    }

    // Obliczanie histogramu sekwencyjnie
    public synchronized void calculate_histogram() {
        for(int i = 0; i < size_n; i++) {
            for(int j = 0; j < size_m; j++) {
                for(int k = 0; k < 94; k++) {
                    if(tab[i][j] == tab_symb[k]) histogram[k]++;
                }
            }
        }
    }

    // Obliczanie liczby wystąpień pojedynczego znaku
    public synchronized int calculate_for_char(int charIndex) {
        int count = 0;
        for(int i = 0; i < size_n; i++) {
            for(int j = 0; j < size_m; j++) {
                if(tab[i][j] == tab_symb[charIndex]) {
                    count++;
                }
            }
        }
        hist_parallel[charIndex] = count;
        return count;
    }

    // Obliczanie i drukowanie znaków w zakresie (dla Runnable)
    public void calculate_for_range(int startIndex, int endIndex) {
        for(int k = startIndex; k < endIndex; k++) {
            int count = 0;
            for(int i = 0; i < size_n; i++) {
                for(int j = 0; j < size_m; j++) {
                    if(tab[i][j] == tab_symb[k]) {
                        count++;
                    }
                }
            }
            incrementParallel(k, count);  // synchronizowane zapisanie wyniku
            print_for_char(k, count, Thread.currentThread().getName());
        }
    }

    // Synchronizowany zapis do histogramu równoległego
    public synchronized void incrementParallel(int index, int value) {
        hist_parallel[index] = value;
    }

    // Wypisywanie graficzne dla znaku (np. @ =======)
    public synchronized void print_for_char(int charIndex, int count, String threadName) {
        System.out.print(threadName + ": " + tab_symb[charIndex] + " ");
        for(int i = 0; i < count; i++) {
            System.out.print("=");
        }
        System.out.println();
    }

    // Wypisywanie histogramu sekwencyjnego
    public void print_histogram() {
        for(int i = 0; i < 94; i++) {
            System.out.print(tab_symb[i] + " " + histogram[i] + "\n");
        }
    }

    // Porównanie histogramów
    public synchronized boolean compare_histograms() {
        for(int i = 0; i < 94; i++) {
            if(histogram[i] != hist_parallel[i]) {
                return false;
            }
        }
        return true;
    }
}

// ===== Wariant 1: Thread – po 1 znaku =====
class Watek extends Thread {
    private int startIndex;
    private int endIndex;
    private Obraz obraz;

    public Watek(int startIndex, int endIndex, Obraz obraz) {
        this.startIndex = startIndex;
        this.endIndex = endIndex;
        this.obraz = obraz;
    }

    public void run() {
        for (int k = startIndex; k < endIndex; k++) {
            int count = obraz.calculate_for_char(k);
            obraz.print_for_char(k, count, this.getName());
        }
    }
}

// ===== Wariant 2: Runnable – blok znaków =====
class HistogramRunnable implements Runnable {
    private int startIndex;
    private int endIndex;
    private Obraz obraz;

    public HistogramRunnable(int startIndex, int endIndex, Obraz obraz) {
        this.startIndex = startIndex;
        this.endIndex = endIndex;
        this.obraz = obraz;
    }

    @Override
    public void run() {
        obraz.calculate_for_range(startIndex, endIndex);
    }
}

// ===== Główna klasa programu =====
public class HistogramAllInOne {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Set image size: n (#rows), m (#columns)");
        int n = scanner.nextInt();
        int m = scanner.nextInt();
        Obraz obraz_1 = new Obraz(n, m);

        // Sekwencyjne obliczenie
        runSequentialVersion(obraz_1);
        runParallelThreadVersion(scanner, obraz_1);

        // Wariant 2 – Runnable
        System.out.println("\n\n--- Now running Version 2 (Runnable, block decomposition) ---");
        Obraz obraz_2 = new Obraz(n, m);
        runSequentialVersion(obraz_2);
        runParallelRunnableVersion(scanner, obraz_2);
    }

    public static void runSequentialVersion(Obraz obraz) {
        obraz.clear_histogram();
        obraz.calculate_histogram();
        System.out.println("\nSequential histogram:");
        obraz.print_histogram();
    }

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
            threads[i].setName("Watek-" + i);
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
            threads[i] = new Thread(task, "Wątek-" + i);
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

        System.out.println("\n[Version 2] Parallel histogram results:");
        if (obraz.compare_histograms()) {
            System.out.println("Histograms match!");
        } else {
            System.out.println("Histograms don't match!");
        }
    }
}
