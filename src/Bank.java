import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

class Bank {

    private static class Account {
        private int balance;
        private ReentrantLock lockAccount;
        Account(int balance) {
            this.balance = balance;
            this.lockAccount = new ReentrantLock();
        }
        int balance()
        {
            this.lockAccount.lock();
            try {
                return balance;
            }
            finally {
                this.lockAccount.unlock();
            }
        }
        boolean deposit(int value) {
            this.lockAccount.lock();
            try {
                balance += value;
                return true;
            }
            finally {
                this.lockAccount.unlock();
            }
        }
        boolean withdraw(int value) {
            this.lockAccount.lock();
            try {
                if (value > balance)
                    return false;
                balance -= value;
                return true;
            }
            finally {
                this.lockAccount.unlock();
            }
        }
    }

    private Map<Integer, Account> map = new HashMap<Integer, Account>();
    private int nextId = 0;
    private ReentrantLock lockBank = new ReentrantLock();

    // create account and return account id
    public int createAccount(int balance) {
        Account c = new Account(balance);
        this.lockBank.lock();
        try {
            int id = nextId;
            nextId += 1;
            map.put(id, c);
            return id;
        }
        finally {
            this.lockBank.unlock();
        }
    }

    // close account and return balance, or 0 if no such account
    public int closeAccount(int id) {
        Account c = null;
        this.lockBank.lock();
        try {
            c = map.remove(id);
            if (c == null)
                return 0;
            c.lockAccount.lock();
        }
        finally {
            this.lockBank.unlock();
        }
        try {
            return c.balance();
        }
        finally {
            c.lockAccount.unlock();
        }
    }

    // account balance; 0 if no such account
    public int balance(int id) {
        Account c = null;
        this.lockBank.lock();
        try {
            c = map.get(id);
            if (c == null)
                return 0;
            c.lockAccount.lock();
        }
        finally {
            this.lockBank.unlock();
        }
        try {
            return c.balance();
        }
        finally {
            c.lockAccount.unlock();
        }
    }

    // deposit; fails if no such account
    public boolean deposit(int id, int value) {
        Account c = null;
        this.lockBank.lock();
        try {
            c = map.get(id);
            if (c == null)
                return false;
            c.lockAccount.lock();
        }
        finally {
            this.lockBank.unlock();
        }

        try {
            return c.deposit(value);
        }

        finally {
            c.lockAccount.unlock();
        }
    }

    // withdraw; fails if no such account or insufficient balance
    public boolean withdraw(int id, int value) {
        Account c = null;
        this.lockBank.lock();
        try {
            c = map.get(id);
            if (c == null)
                return false;
            c.lockAccount.lock();
        }

        finally {
            this.lockBank.unlock();
        }

        try {
            return c.withdraw(value);
        }

        finally {
            c.lockAccount.unlock();
        }
    }

    // transfer value between accounts;
    // fails if either account does not exist or insufficient balance
    public boolean transfer(int from, int to, int value) {
        Account cfrom, cto;
        Account firstLock;
        Account secondLock;

        this.lockBank.lock();
        try {
            cfrom = map.get(from);
            cto = map.get(to);
            if (cfrom == null || cto == null)
                return false;
            firstLock = (from < to) ? cfrom : cto;
            secondLock = (from < to) ? cto : cfrom;
            firstLock.lockAccount.lock();
            secondLock.lockAccount.lock();
        }

        finally {
            this.lockBank.unlock();
        }

        try {
            return cfrom.withdraw(value) && cto.deposit(value);
        }

        finally {
            firstLock.lockAccount.unlock();
            secondLock.lockAccount.unlock();
        }
    }

    // sum of balances in set of accounts; 0 if some does not exist
    public int totalBalance(int[] ids) {
        ArrayList<Integer> accountsLocked= new ArrayList(ids.length);
        this.lockBank.lock();
        int total = 0;
        for (int i : ids) {
            int id = ids[i];
            if(this.map.containsKey(id)) {
             map.get(id).lockAccount.lock();
             accountsLocked.add(id);
            }
        }
        this.lockBank.unlock();
        for(int id : accountsLocked) {
            total += map.get(id).balance();
            map.get(id).lockAccount.unlock();
        }

        return total;
    }

}
