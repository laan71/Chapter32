package Chapter32_Exercises;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

// Rewrite Listing 32.1 to display the output in a text area, as shown in Figure 32.30.
public class Rewrite32_1 extends Application {

    // These 2 objects are needed in all sub-classes
    TextArea textArea = new TextArea();
    static String text2Show = "";
    private static final Semaphore semaphore = new Semaphore(1);
    static int clickCounter = 0;


    @Override
    public void start(Stage stage) {

        // Shows java and javafx version
        String javaVersion = System.getProperty("java.version");
        String javafxVersion = System.getProperty("javafx.version");

        // Graphical elements are put in a vertical box
        VBox vBox = new VBox();
        vBox.setPadding(new Insets(20, 60, 60, 60));
        vBox.setSpacing(12);

        // Label showing Java og JavaFX version, are being added in VBoxen
        Label l = new Label("Hello, JavaFX " + javafxVersion + ", running on Java " + javaVersion + ".");
        vBox.getChildren().add(l);

        // Textarea is already declared and instantiated on the object, text is added and it all shows in the VBoxen
        textArea.setText("Thread results : ");
        textArea.wrapTextProperty().setValue(true);
        vBox.getChildren().add(textArea);

        // Making a button with a nice event-handler lambda expression, starting the threads
        Button button = new Button("Give me threads!");
        button.setOnAction((EventHandler) event -> {
            textArea.setText("Thread results (" + clickCounter++ + "): \n");
            startThreads();
        });
        vBox.getChildren().add(button);

        // All the elements is now in place and we are showing the Scenen with the VBoxen inside
        Scene scene = new Scene(new StackPane(vBox), 640, 480);
        stage.setScene(scene);
        stage.show();
    }

    /**
     * The metode that starts 3 competing threads
     */
    public void startThreads() {

        text2Show = "";

        // Create tasks
        Runnable printA = new PrintChar('a', 100);
        Runnable printB = new PrintChar('b', 100);
        Runnable print100 = new PrintNum(100);

        // Create threads
        Thread thread1 = new Thread(printA);
        Thread thread2 = new Thread(printB);
        Thread thread3 = new Thread(print100);

        // Using ExecutorService to manage thread execution
        ExecutorService executor = Executors.newFixedThreadPool(10);

        executor.execute(thread1);
        executor.execute(thread2);
        executor.execute(thread3);

        executor.shutdown();

        // Wait until all tasks are finished
        while (!executor.isTerminated()) {
        }

        textArea.appendText(text2Show);

    }

    // JavaFX wants it here!
    public static void main(String[] args) {
        launch(args);
    }

    // The task for printing a specified character in specified times
    class PrintChar implements Runnable {
        private char charToPrint; // The character to print
        private int times; // The times to repeat

        /**
         * Construct a task with specified character and number of
         * times to print the character
         */
        public PrintChar(char c, int t) {
            charToPrint = c;
            times = t;
        }

        @Override
        /** Override the run() method to tell the system
         *  what the task to perform
         */
        public void run() {
            for (int i = 0; i < times; i++) {
                synchronized (semaphore) {
                    text2Show += String.valueOf(charToPrint);
                }
            }
        }
    }

    // The task class for printing number from 1 to n for a given n
    class PrintNum implements Runnable {
        private int lastNum;

        /**
         * Construct a task for printing 1, 2, ... i
         */
        public PrintNum(int n) {
            lastNum = n;
        }

        /** Tell the thread how to run */
        @Override
        public void run() {
            for (int i = 1; i <= lastNum; i++) {
                synchronized (semaphore) {
                    text2Show = text2Show + String.valueOf(i);
                }
            }
        }
    }

}

