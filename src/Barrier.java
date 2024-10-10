import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.Condition;

public class Barrier {

    private int N;
    private int count = 0;
    private Lock l = new ReentrantLock();
    private Condition c = l.newCondition();


    public Barrier(int N) {
        this.N = 5;
        this.count = 0;
        this.l = new ReentrantLock();
        this.c = l.newCondition();
    }

    void espera() throws InterruptedException {

        l.lock();
        try {
            count++;
            if(count < N) {
                while(count < N) {
                    System.out.println("Threads á espera:" + count);
                    c.await();
                }
            }
            else{
                c.signalAll();
                System.out.println("Threads a passar");
            }
        }
        finally {
            l.unlock();
        }
    }
}


