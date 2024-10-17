import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.Condition;

public class Warehouse {
    private Map<String, Product> map =  new HashMap<String, Product>();
    private Lock l = new ReentrantLock();

    private class Product {
        Condition isEmpty = l.newCondition();
        int quantity = 0;
    }

    private Product get(String item) {
        Product p = map.get(item);
        if (p != null) return p;
        p = new Product();
        map.put(item, p);
        return p;
    }

    public void supply(String item, int quantity) {
        l.lock();
        try {
            Product p = this.get(item);
            p.quantity += quantity;
            p.isEmpty.signalAll();
        }
        finally {
            l.unlock();
        }
    }

    public void consume(Set<String> items) throws InterruptedException {
        l.lock();
        try {
            for (String item : items) {
                Product p = this.get(item);
                while (p.quantity == 0) {
                    p.isEmpty.await();
                }
                p.quantity--;
            }
        }
        finally {
            l.unlock();
        }
    }

}