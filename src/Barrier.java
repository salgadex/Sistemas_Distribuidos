import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.Condition;

public class Barrier {

    private int N;
    private int count ;
    private Lock l;
    private Condition c;
    private int generation;


    public Barrier(int N) {
        this.N = 5;
        this.count = 0;
        this.l = new ReentrantLock();
        this.c = l.newCondition();
        this.generation = 0;
    }

    void espera() throws InterruptedException {

        l.lock();
        try {
            int currentGeneration = generation;
            count++;
            if(count < N) {
                while(currentGeneration == generation) {
                    System.out.println("Threads á espera: " + count + " na geração " + generation);
                    c.await();
                }
            }
            else{
                c.signalAll();
                count = 0;
                generation++;
                System.out.println("Barreira completada, todas as threads estão a passar para a próxima fase.");
                System.out.println(" ");
            }
        }
        finally {
            l.unlock();
        }
    }
}


