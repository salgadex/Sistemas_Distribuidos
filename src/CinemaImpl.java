import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.Condition;
import java.util.*;

public class CinemaImpl implements Cinema{
    private final Lock lock = new ReentrantLock();
    private final Condition filmeDisponivel = lock.newCondition();
    private String filmeAtual = null;
    private int naSala = 0;

    public void assiste(String filme) {
        this.lock.lock();
        try{
            while(this.filmeAtual != null && !this.filmeAtual.equals(filme) && naSala > 0){
                filmeDisponivel.await();
            }
            this.filmeAtual = filme;
            naSala++;

        } catch (InterruptedException e) {
            throw new RuntimeException(e);

        } finally{
            this.lock.unlock();
        }



    }

    public void abandona(String filme) {
        this.lock.lock();
        try {
            naSala--;
            if(naSala == 0){
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
            if(filmeAtual == null) {
                return "Nenhum";
            }
            else {
                return this.filmeAtual;
            }
        }

        finally {
            this.lock.unlock();
        }
    }
}