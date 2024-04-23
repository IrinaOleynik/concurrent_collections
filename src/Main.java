
import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class Main {
    static BlockingQueue<String> queueA = new ArrayBlockingQueue<>(100);
    static BlockingQueue<String> queueB = new ArrayBlockingQueue<>(100);
    static BlockingQueue<String> queueC = new ArrayBlockingQueue<>(100);
    static final int textCount = 10_000;
    static final int textLength = 100_000;

    public static void main(String[] args) throws InterruptedException {

        new Thread(() -> {
            for (int i = 0; i < textCount; i++) {
                String text = generateText("abc");
                try {
                    queueA.put(text);
                    queueB.put(text);
                    queueC.put(text);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();

        Thread a = getNewThread(queueA, 'a');
        Thread b = getNewThread(queueB, 'b');
        Thread c = getNewThread(queueC, 'c');

        a.start();
        b.start();
        c.start();

        a.join();
        b.join();
        c.join();
    }

    public static Thread getNewThread(BlockingQueue<String> queue, char letter) {
        return new Thread(() -> {
            int maxCount = 0;
            for (int i = 0; i < textCount; i++) {
                try {
                    String text = queue.take();
                    int currentCount = countChar(text, letter);
                    if (currentCount > maxCount) {
                        maxCount = currentCount;
                    }
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            System.out.println("Максимальное количество символов '" + letter + "' = " + maxCount);
        });
    }

    public static String generateText(String letters) {
        Random random = new Random();
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < textLength; i++) {
            text.append(letters.charAt(random.nextInt(letters.length())));
        }
        return text.toString();
    }

    public static int countChar(String text, char letter) throws InterruptedException {
        int counter = 0;
        for (char c : text.toCharArray()) {
            if (c == letter) {
                counter++;
            }
        }
        return counter;
    }
}