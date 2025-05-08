// Новый класс, реализующий Runnable для обработки диапазона символов
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
        // Подсчет и печать результатов для диапазона символов
        obraz.calculate_for_range(startIndex, endIndex);
    }
}
