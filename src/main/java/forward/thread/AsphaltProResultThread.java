package forward.thread;

import forward.operation.ResultOperation;
import org.apache.log4j.Logger;

public class AsphaltProResultThread implements Runnable {

    public static final Logger logger = Logger.getLogger(AsphaltProResultThread.class);

    private ResultOperation resultOperation;

    private Integer forwardCount;

    private Integer selectCount;

    public AsphaltProResultThread(Integer forwardCount, Integer selectCount, ResultOperation resultOperation) {
        this.resultOperation = resultOperation;
        this.forwardCount = forwardCount;
        this.selectCount = selectCount;
    }

    @Override
    public void run() {

        Thread forward = new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(forwardCount);
                    this.resultOperation.forward();
                } catch (Exception e) {
                    logger.error(e.getMessage());
                }
            }
        });
        forward.start();

        Thread getData = new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(selectCount);
                    this.resultOperation.getData();
                } catch (Exception e) {
                    logger.error(e.getMessage());
                }
            }
        });
        getData.start();

    }
}
