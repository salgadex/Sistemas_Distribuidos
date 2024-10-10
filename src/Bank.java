import java.util.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;

class Bank {

    private static class Account {
        private int balance;
        private ReentrantReadWriteLock lockAccount;
        Account(int balance) {
            this.balance = balance;
            this.lockAccount = new ReentrantReadWriteLock();
        }
        int balance()
        {
            this.lockAccount.readLock().lock();
            try {
                return balance;
            }
            finally {
                this.lockAccount.readLock().unlock();
            }
        }
        boolean deposit(int value) {
            this.lockAccount.writeLock().lock();
            try {
                balance += value;
                return true;
            }
            finally {
                this.lockAccount.writeLock().unlock();
            }
        }
        boolean withdraw(int value) {
            this.lockAccount.writeLock().lock();
            try {
                if (value > balance)
                    return false;
                balance -= value;
                return true;
            }
            finally {
                this.lockAccount.writeLock().unlock();
            }
        }
    }

    private Map<Integer, Account> map = new HashMap<Integer, Account>();
    private int nextId = 0;
    private ReentrantReadWriteLock lockBank = new ReentrantReadWriteLock();

    // create account and return account id
    public int createAccount(int balance) {
        Account c = new Account(balance);
        this.lockBank.writeLock().lock();
        try {
            int id = nextId;
            nextId += 1;
            map.put(id, c);
            return id;
        }
        finally {
            this.lockBank.writeLock().unlock();
        }
    }

    // close account and return balance, or 0 if no such account
    public int closeAccount(int id) {
        Account c = null;
        this.lockBank.writeLock().lock();
        try {
            c = map.remove(id);
            if (c == null)
                return 0;
            c.lockAccount.writeLock().lock();
        }
        finally {
            this.lockBank.writeLock().unlock();
        }
        try {
            return c.balance();
        }
        finally {
            c.lockAccount.writeLock().unlock();
        }
    }

    // account balance; 0 if no such account
    public int balance(int id) {
        Account c = null;
        this.lockBank.readLock().lock();
        try {
            c = map.get(id);
            if (c == null)
                return 0;
            c.lockAccount.readLock().lock();
        }
        finally {
            this.lockBank.readLock().unlock();
        }
        try {
            return c.balance();
        }
        finally {
            c.lockAccount.readLock().unlock();
        }
    }

    // deposit; fails if no such account
    public boolean deposit(int id, int value) {
        Account c = null;
        this.lockBank.readLock().lock();
        try {
            c = map.get(id);
            if (c == null)
                return false;
            c.lockAccount.writeLock().lock();
        }
        finally {
            this.lockBank.readLock().unlock();
        }

        try {
            return c.deposit(value);
        }

        finally {
            c.lockAccount.writeLock().unlock();
        }
    }

    // withdraw; fails if no such account or insufficient balance
    public boolean withdraw(int id, int value) {
        Account c = null;
        this.lockBank.readLock().lock();
        try {
            c = map.get(id);
            if (c == null)
                return false;
            c.lockAccount.writeLock().lock();
        }

        finally {
            this.lockBank.readLock().unlock();
        }

        try {
            return c.withdraw(value);
        }

        finally {
            c.lockAccount.writeLock().unlock();
        }
    }

    // transfer value between accounts;
    // fails if either account does not exist or insufficient balance
    public boolean transfer(int from, int to, int value) {
        Account cfrom, cto;
        this.lockBank.readLock().lock();
        try {
            cfrom = map.get(from);
            cto = map.get(to);
            if (cfrom == null || cto == null)
                return false;
            cfrom.lockAccount.writeLock().lock();
            cto.lockAccount.writeLock().lock();
        }

        finally {
            this.lockBank.readLock().unlock();
        }

        try {
            return cfrom.withdraw(value) && cto.deposit(value);
        }

        finally {
            cfrom.lockAccount.writeLock().unlock();
            cto.lockAccount.writeLock().unlock();
        }
    }

    public int totalBalance(int[] ids) {
        ArrayList<Account> lockedAccounts = new ArrayList<>();
        int total = 0;

        this.lockBank.readLock().lock();
        try {
            for (int id : ids) {
                Account account = map.get(id);
                if (account != null) {
                    account.lockAccount.readLock().lock();
                    lockedAccounts.add(account);
                }
            }
        } finally {
            this.lockBank.readLock().unlock();
        }

        try {
            for (Account account : lockedAccounts) {
                total += account.balance();
            }
        } finally {
            // Libera os bloqueios de leitura das contas
            for (Account account : lockedAccounts) {
                account.lockAccount.readLock().unlock();
            }
        }
        return total;
    }
}
