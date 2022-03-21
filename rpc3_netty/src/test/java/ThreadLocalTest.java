public class ThreadLocalTest implements Runnable{
    private static final ThreadLocal local = new ThreadLocal();
    public static void main(String[] args) {


        new Thread(new ThreadLocalTest()).start();
        new Thread(new ThreadLocalTest()).start();

    }

    @Override
    public void run() {
        local.set("fff");
    }
}
