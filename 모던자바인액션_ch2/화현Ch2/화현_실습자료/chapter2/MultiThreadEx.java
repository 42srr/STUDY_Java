package chapter2;

public class MultiThreadEx {
    public static void main(String[] args) {
        Thread t1 = new Thread(new Runnable() {
            public void run() {
                for (int i = 0; i < 5; i++) {
                    System.out.println("t1 :" + i);
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        Thread t2 = new Thread(new Runnable() {
            public void run() {
                for (int i = 0; i < 5; i++) {
                    System.out.println("t2 :" + i);
                    try {
                        Thread.sleep(300);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        t1.start();
        t2.start();
    }
}
