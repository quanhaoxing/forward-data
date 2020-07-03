package forward.thread;

import forward.operation.TransportJzOperation;
import org.apache.log4j.Logger;

public class AsphaltTransportJzThread implements Runnable {

    public static final Logger logger = Logger.getLogger(AsphaltTransportJzThread.class);

    private TransportJzOperation transportJzOperation;

    private Integer forwardCount;

    private Integer selectCount;

    public AsphaltTransportJzThread(Integer forwardCount, Integer selectCount,TransportJzOperation transportJzOperation) {
        this.transportJzOperation = transportJzOperation;
        this.forwardCount = forwardCount;
        this.selectCount = selectCount;
    }

    @Override
    public void run() {

        Thread forward = new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(forwardCount);
                    this.transportJzOperation.forward();
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
                    this.transportJzOperation.getData();
                } catch (Exception e) {
                    logger.error(e.getMessage());
                }
            }
        });
        getData.start();

    }
}
