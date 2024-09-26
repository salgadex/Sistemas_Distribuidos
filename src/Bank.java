import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Bank {

    private static class Account {
        private int balance;
        private final Lock lock = new ReentrantLock();
        Account(int balance) { this.balance = balance; }
        int balance() { return balance; }

        boolean deposit(int value) {
            lock.lock();
            try {
                balance += value;
                return true;
            }
            finally {
                lock.unlock();
            }

        }
        boolean withdraw(int value) {
            lock.lock();
            try {
                if (value > balance)
                    return false;
                balance -= value;
                return true;
            }
            finally {
                lock.unlock();
            }
        }
    }

    // Bank slots and vector of accounts
    private int slots;
    private Account[] av;

    public Bank(int n) {
        slots=n;
        av=new Account[slots];
        for (int i=0; i<slots; i++) av[i]=new Account(0);
    }

    // Account balance
    public int balance(int id) {
        if (id < 0 || id >= slots)
            return 0;
        return av[id].balance();
    }

    // Deposit
    public boolean deposit(int id, int value) {
        if (id < 0 || id >= slots)
            return false;
        return av[id].deposit(value);
    }

    // Withdraw; fails if no such account or insufficient balance
    public boolean withdraw(int id, int value) {
        if (id < 0 || id >= slots)
            return false;
        return av[id].withdraw(value);
    }
}
