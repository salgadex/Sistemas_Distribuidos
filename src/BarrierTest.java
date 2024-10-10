public class BarrierTest {

    public static class Worker implements Runnable {
        private Barrier barrier;

        public Worker(Barrier barrier) {
            this.barrier = barrier;
        }

        public void run() {
            try {
                System.out.println(Thread.currentThread().getName() + " chegou à barreira.");
                barrier.espera();
                System.out.println(Thread.currentThread().getName() + " passou pela barreira.");
            }
            catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static void main(String[] args) {
        int numThreads = 5;
        Barrier barrier = new Barrier(numThreads);

        Thread[] threads = new Thread[numThreads];

        // Cria as threads
        for (int i = 0; i < numThreads; i++) {
            threads[i] = new Thread(new Worker(barrier), "Thread-" + i);
        }

        for (int i = 0; i < numThreads; i++) {
            threads[i].start();
        }
    }



}