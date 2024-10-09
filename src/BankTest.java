import java.util.Random;

public class BankTest {

    private static class Mover implements Runnable {
        Bank b;
        int s; // Number of accounts

        public Mover(Bank b, int s) {
            this.b = b;
            this.s = s;
        }

        public void run() {
            final int moves = 100000;
            int from, to;
            Random rand = new Random();

            for (int m = 0; m < moves; m++) {
                from = rand.nextInt(s); // Get one
                while ((to = rand.nextInt(s)) == from); // Slow way to get distinct
                b.transfer(from, to, 1);
            }
        }
    }
    public static void main(String[] args) throws InterruptedException {

        final int N=10;

        Bank b = new Bank();

        int[] accountIds = new int[N];
        for (int i=0; i< N; i++) {
            accountIds[i]=b.createAccount(1000);
        }

        System.out.println("Saldo Inicial: " + b.totalBalance(accountIds));


        Thread t1 = new Thread(new Mover(b,N));
        Thread t2 = new Thread(new Mover(b,N));

        t1.start();
        t2.start();

        Thread.sleep(1000);
        int closedAccount = accountIds[2];
        int closedAccountBalance = b.closeAccount(closedAccount);

        System.out.println("Conta: " + closedAccount + " fechada com saldo: " + closedAccountBalance);
        t1.join();
        t2.join();

        int finalBalance = b.totalBalance(accountIds);
        System.out.println("Saldo Final Após Transferências: " + finalBalance);

        for(int i = 3; i < N; i++) {
           int balanceAfterClosedAccounts = b.closeAccount(accountIds[i]);
           System.out.println("Conta: " + accountIds[i] + " fechada com saldo: " + balanceAfterClosedAccounts );
        }

        System.out.println("Saldo Total Final: " + b.totalBalance(accountIds));
    }
}