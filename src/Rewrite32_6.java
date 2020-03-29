import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Rewrite32_6 {
    private static ArrayBlockingQueue<Integer> buffer = new ArrayBlockingQueue<>(2);

    public static void main(String[] args) {
        // Create a thread pool with two threads
        ExecutorService executor = Executors.newFixedThreadPool(2);
        executor.execute(new DepositTask());
        executor.execute(new WithdrawTask());
        executor.shutdown();

    }

    private static class DepositTask implements Runnable{
        @Override     // Keep adding an amount to the account
        public void run() {
            try{     // Purposely delay it to let the withdraw method proceed
                int i = 1;
                while (true){
                    System.out.println("Account writes " + i);
                    buffer.put(i++); // Add any value to the buffer, say 1
                    // Put the thread into sleep
                    Thread.sleep((int)(Math.random()*1000));
                }
            }
            catch (InterruptedException ex){
                ex.printStackTrace();
            }
        }
    }

    // A task for reading and deleting an int from the buffer
    private static class WithdrawTask implements Runnable{

        @Override
        public void run() {
            try{
                while (true){
                    System.out.println("\t\t\tConsumer reads " + buffer.take());
                    // Put thread into sleep
                    Thread.sleep((int)(Math.random()*10));
                }
            }
            catch (InterruptedException ex){
                ex.printStackTrace();
            }
        }
    }
}
