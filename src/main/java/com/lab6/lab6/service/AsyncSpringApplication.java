package com.lab6.lab6.service;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

@SpringBootApplication
@EnableScheduling
public class AsyncSpringApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(AsyncSpringApplication.class, args);
    }

    private static final AtomicBoolean periodicTaskEnabled = new AtomicBoolean(false);

    public static boolean isPeriodicTaskEnabled() {
        return periodicTaskEnabled.get();
    }

    public static void enablePeriodicTask() {
        periodicTaskEnabled.set(true);
    }

    @Override
    public void run(String... args) {
        Scanner scanner = new Scanner(System.in);
        String userInput;
        do {
            System.out.print("Обрати Y/N для запуску періодичної задачі: ");
            userInput = scanner.nextLine();
        } while (!"Y".equalsIgnoreCase(userInput) && !"N".equalsIgnoreCase(userInput));

        if ("Y".equalsIgnoreCase(userInput)) {
            System.out.println("Запуск періодичної задачі кожні 10 секунд...");
            enablePeriodicTask();
        } else {
            System.out.println("Періодична задача пропущена.");
            System.exit(0);
        }
    }
}

@Component
class PeriodicTask {

    @Scheduled(fixedRate = 10000) // Виконується кожні 10 секунд
    public void executeTask() {
        if (AsyncSpringApplication.isPeriodicTaskEnabled()) {
            System.out.println("Періодична задача виконується. Час: " + LocalDateTime.now());
        }
    }
}

@Component
class RandomIntervalTask {

    private final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
    private final LocalDateTime startTime = LocalDateTime.now();
    private final Random random = new Random();

    public RandomIntervalTask() {
        scheduleRandomTask();
    }

    private void scheduleRandomTask() {
        int delay = 1 + random.nextInt(10); // Випадковий інтервал 1-10 секунд
        executorService.schedule(() -> {
            Duration elapsed = Duration.between(startTime, LocalDateTime.now());
            System.out.println("Задача з випадковим інтервалом виконана. Час виконання: " + elapsed.getSeconds() + " секунд від старту.");
            scheduleRandomTask(); // Плануємо наступний запуск
        }, delay, TimeUnit.SECONDS);
    }

    public void startRandomTaskImmediately() {
        executorService.execute(() -> {
            Duration elapsed = Duration.between(startTime, LocalDateTime.now());
            System.out.println("Випадкова задача запущена одразу. Час виконання: " + elapsed.getSeconds() + " секунд від старту.");
            scheduleRandomTask();
        });
    }
}

@Component
class RandomTaskStarter {
    public RandomTaskStarter(RandomIntervalTask randomIntervalTask) {
        randomIntervalTask.startRandomTaskImmediately();
    }
}
