package no.ntnu;

import java.util.*;

/**
 * Represents the business logic of the application (not dependent on any UDP, sockets, etc)
 */
public class Logic {
    // The OK response to send to the user when a task is solved correctly
    public static final String OK = "ok";

    // The response to send to the user in case of an error
    public static final String ERROR = "error";

    // This is the message a client would send if it asks for a new task
    private static final String TASK_MESSAGE = "task";

    // Store the tasks here
    private static final List<String> tasks = new ArrayList<>();
    // Corresponding answers here
    private static final List<String> answers = new ArrayList<>();

    private static final Random randomGenerator = new Random();

    /**
     * Generate a random task for the client
     *
     * @return A random task
     */
    public static String getRandomTask() {
        initializeTasks();
        return tasks.get(getRandomTaskIndex());
    }

    /**
     * Generate a random task index
     *
     * @return A random index within the bounds [0; numberOfTasks]
     * Note: this would throw an exception if called before initializing tasks. But this should never happen.
     */
    private static int getRandomTaskIndex() {
        return randomGenerator.nextInt(tasks.size());
    }

    /**
     * Make sure we have all the tasks initialized - add them to the map. Only do this once!
     */
    private static void initializeTasks() {
        if (tasks.isEmpty()) {
            tasks.add("Would a wood chuck chuck if a wood chuck could chuck wood?");
            answers.add("question 12");
            tasks.add("I will be back.");
            answers.add("statement 4");
            tasks.add(".");
            answers.add("statement 0");
            tasks.add("?");
            answers.add("question 0");
            tasks.add("What does the fox say?");
            answers.add("question 5");
            tasks.add("NTNU.");
            answers.add("statement 1");
            tasks.add("What is the most popular programmer choice?");
            answers.add("question 7");
            tasks.add("Missing a Semicolon.");
            answers.add("statement 3");
            tasks.add("What is the biggest lie in computer programming?");
            answers.add("question 8");
            tasks.add("Lorem ipsum.");
            answers.add("statement 2");
            tasks.add("Will Will Smith smith?");
            answers.add("question 4");
            tasks.add("Will Smith will smith.");
            answers.add("statement 4");
        }
    }

    /**
     * Check whether the received message means that the client is requesting a new task
     *
     * @param receivedMessage The message received from a client
     * @return True if this message means that the client wants to get a new task; false otherwise
     */
    public static boolean isClientRequestingATask(String receivedMessage) {
        return receivedMessage != null && receivedMessage.equals(TASK_MESSAGE);
    }

    /**
     * Checks if the given answer is a correct answer for the assigned task
     *
     * @param task   The previously assigned task
     * @param answer The provided answer
     * @return True if the answer is correct, false if not correct
     */
    public static boolean hasClientAnsweredCorrectly(String task, String answer) {
        if (task == null || answer == null) return false;
        int taskIndex = tasks.indexOf(task);
        if (taskIndex == -1) return false;
        String correctAnswer = answers.get(taskIndex);
        return answer.equals(correctAnswer);
    }
}
