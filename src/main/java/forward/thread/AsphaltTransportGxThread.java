package forward.thread;

import forward.operation.TransportGxOperation;
import org.apache.log4j.Logger;

public class AsphaltTransportGxThread implements Runnable {

    public static final Logger logger = Logger.getLogger(AsphaltTransportGxThread.class);

    private TransportGxOperation transportGxOperation;

    private Integer forwardCount;

    private Integer selectCount;

    public AsphaltTransportGxThread(Integer forwardCount, Integer selectCount, TransportGxOperation transportGxOperation) {
        this.transportGxOperation = transportGxOperation;
        this.forwardCount = forwardCount;
        this.selectCount = selectCount;
    }

    @Override
    public void run() {

        Thread forward = new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(forwardCount);
                    this.transportGxOperation.forward();
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
                    this.transportGxOperation.getData();
                } catch (Exception e) {
                    logger.error(e.getMessage());
                }
            }
        });
        getData.start();
    }
}
