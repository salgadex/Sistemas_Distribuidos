import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.Condition;
import java.util.*;

public class CinemaImpl implements Cinema{
    private final Lock lock = new ReentrantLock();
    private final Condition filmeDisponivel = lock.newCondition();
    private String filmeAtual = null;
    private Set<String> cinefilosNaSala = new HashSet<>();

    public void assiste(String filme) {
        this.lock.lock();
        try{
            while(filmeAtual != null && !filmeAtual.equals(filme)){
                filmeDisponivel.await();
            }
            filmeAtual = filme;
            cinefilosNaSala.add(Thread.currentThread().getName());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Thread interrompida", e);
        }
        finally{
            this.lock.unlock();
        }



    }

    public void abandona(String filme) {
       this.lock.lock();
       try {
           cinefilosNaSala.remove(Thread.currentThread().getName());
           if(cinefilosNaSala.isEmpty()){
               filmeAtual = null;
               filmeDisponivel.signalAll();
           }
       }

       finally {
           this.lock.unlock();
       }
    }

    public String filmeEmExibicao() {
        this.lock.lock();
        try {
             return filmeAtual != null ? filmeAtual : "NENHUM";
        }

        finally {
            this.lock.unlock();
        }
    }
}