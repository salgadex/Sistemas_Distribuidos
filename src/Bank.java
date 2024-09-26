import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Bank {

    private static class Account {
        private int balance;
        private Lock l;
        Account(int balance) {
            this.balance = balance;
            this.l = new ReentrantLock();
        }
        int balance() {
            return balance;
        }

        boolean deposit(int value) {
            l.lock();
            try {
                balance += value;
                return true;
            }
            finally {
                l.unlock();
            }

        }
        boolean withdraw(int value) {
            l.lock();
            try {
                if (value > balance)
                    return false;
                balance -= value;
                return true;
            }
            finally {
                l.unlock();
            }
        }

        void lock()
        {
            this.l.lock();
        }

        void unlock()
        {
            this.l.unlock();
        }


    }

    // Bank slots and vector of accounts
    private int slots;
    private Account[] av;
    private Lock lock;

    public Bank(int n) {
        slots=n;
        av=new Account[slots];
        for (int i=0; i<slots; i++) av[i]=new Account(0);
        this.lock = new ReentrantLock();
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
    public boolean transfer (int from, int to, int value) {
        if((from < 0 || from >= slots) || (to < 0 || to >= slots)) {
            return false;
        }
        else {
            if(from < to) {
                av[from].lock();
                av[to].lock();
            }
            else {
                av[to].lock();
                av[from].lock();
            }
            try {
                return this.withdraw(from, value) && this.deposit(to, value);
            }
            finally {
                av[from].unlock();
                av[to].unlock();
            }
        }
    }

    public int totalBalance() {
       int total=0;
       for(int i=0; i<slots; i++) {
           av[i].lock();
       }
       try {
           for (int i = 0; i < slots; i++) {
               total += av[i].balance();


           }
       }

       finally {
           for(int i=0; i<slots; i++) {
               av[i].unlock();
         }
       }
       return total;
    }


}
