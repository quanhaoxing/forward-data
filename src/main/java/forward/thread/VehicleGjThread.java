package forward.thread;

import forward.operation.GjOperation;
import org.apache.log4j.Logger;

public class VehicleGjThread implements Runnable {

    private Integer forwardCount;

    private Integer selectCount;

    public static final Logger logger = Logger.getLogger(VehicleGjThread.class);

    private GjOperation gjOperation;

    public VehicleGjThread(Integer forwardCount, Integer selectCount, GjOperation gjOperation) {
        this.forwardCount = forwardCount;
        this.selectCount = selectCount;
        this.gjOperation = gjOperation;
    }

    @Override
    public void run() {
        Thread getData = new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(selectCount);
                    this.gjOperation.getData();
                } catch (Exception e) {
                    logger.error(e.getMessage());
                }
            }
        });
        getData.start();

        Thread forward = new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(forwardCount);
                    this.gjOperation.forward();
                } catch (Exception e) {
                    logger.error(e.getMessage());
                }
            }
        });
        forward.start();

        Thread forward1 = new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(forwardCount);
                    this.gjOperation.forward();
                } catch (Exception e) {
                    logger.error(e.getMessage());
                }
            }
        });
        forward1.start();
    }
}
