package forward.thread;

import forward.operation.ProcessOperation;
import org.apache.log4j.Logger;

public class AsphaltProcessThread implements Runnable {

    public static final Logger logger = Logger.getLogger(AsphaltProcessThread.class);

    private ProcessOperation processOperation;

    private Integer forwardCount;

    private Integer selectCount;

    public AsphaltProcessThread(Integer forwardCount, Integer selectCount, ProcessOperation processOperation) {
        this.processOperation = processOperation;
        this.forwardCount = forwardCount;
        this.selectCount = selectCount;
    }

    @Override
    public void run() {

        Thread getData = new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(selectCount);
                    this.processOperation.getData();
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
                    this.processOperation.forward();
                } catch (Exception e) {
                    logger.error(e.getMessage());
                }
            }
        });
        forward.start();
    }
}
