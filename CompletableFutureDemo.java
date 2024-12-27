import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class CompletableFutureDemo {

    private static final Logger logger = Logger.getLogger(CompletableFutureDemo.class.getName());

    public static void main(String[] args) {
        CompletableFutureDemo demo = new CompletableFutureDemo();

        logger.info("Starting variant 1 tasks...");
        demo.executeVariant1();

        logger.info("Starting variant 2 tasks...");
        demo.executeVariant2();
    }

    public void executeVariant1() {
        // Завдання 1.1: Обробка даних із кількох джерел
        CompletableFuture<String> source1 = CompletableFuture.supplyAsync(() -> {
            sleep(2);
            return "Data from Source 1";
        });

        CompletableFuture<String> source2 = CompletableFuture.supplyAsync(() -> {
            sleep(3);
            return "Data from Source 2";
        });

        CompletableFuture<Void> allSources = CompletableFuture.allOf(source1, source2);
        allSources.thenRun(() -> {
            try {
                String combinedData = source1.get() + " | " + source2.get();
                logger.info("Combined Data: " + combinedData);
            } catch (Exception e) {
                logger.severe("Error combining data: " + e.getMessage());
            }
        }).join();

        // Завдання 1.2: Вибір найкращого маршруту
        CompletableFuture<Double> train = CompletableFuture.supplyAsync(() -> {
            sleep(2);
            return 100.0; // Train price
        });

        CompletableFuture<Double> bus = CompletableFuture.supplyAsync(() -> {
            sleep(1);
            return 50.0; // Bus price
        });

        CompletableFuture<Double> flight = CompletableFuture.supplyAsync(() -> {
            sleep(3);
            return 300.0; // Flight price
        });

        train.thenCombine(bus, Double::min)
            .thenCombine(flight, Double::min)
            .thenAccept(bestPrice -> logger.info("Best price for the trip: " + bestPrice))
            .join();
    }

    public void executeVariant2() {
        // Завдання 2.1: Результат першого завершеного завдання
        CompletableFuture<String> task1 = CompletableFuture.supplyAsync(() -> {
            sleep(3);
            return "Task 1 result";
        });

        CompletableFuture<String> task2 = CompletableFuture.supplyAsync(() -> {
            sleep(1);
            return "Task 2 result";
        });

        CompletableFuture<String> task3 = CompletableFuture.supplyAsync(() -> {
            sleep(2);
            return "Task 3 result";
        });

        CompletableFuture.anyOf(task1, task2, task3)
            .thenAccept(result -> logger.info("First completed task result: " + result))
            .join();

        // Завдання 2.2: Бронювання квитків
        CompletableFuture<Boolean> checkAvailability = CompletableFuture.supplyAsync(() -> {
            sleep(2);
            return true; // Availability check
        });

        CompletableFuture<Double> findBestPrice = CompletableFuture.supplyAsync(() -> {
            sleep(1);
            return 200.0; // Best price found
        });

        checkAvailability.thenCombine(findBestPrice, (available, price) -> {
            if (available) {
                logger.info("Seats available. Best price: " + price);
                return CompletableFuture.completedFuture("Booking confirmed for $" + price);
            } else {
                logger.warning("No seats available.");
                return CompletableFuture.completedFuture("Booking failed");
            }
        }).thenCompose(result -> result)
          .thenAccept(logger::info)
          .join();
    }

    private void sleep(int seconds) {
        try {
            TimeUnit.SECONDS.sleep(seconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.warning("Sleep interrupted: " + e.getMessage());
        }
    }
}
