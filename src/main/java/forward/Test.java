package forward;

import forward.operation.*;
import forward.thread.*;
import forward.util.PropertyUtil;


public class Test {

    private static final Integer forwardCount = Integer.parseInt(PropertyUtil.getProperty("forwardCount"));

    private static final Integer selectCount = Integer.parseInt(PropertyUtil.getProperty("selectCount"));


    public static void main(String[] args)  {

        //1.1 沥青批次生产信息
        AsphaltProResultThread res = new AsphaltProResultThread(forwardCount,selectCount,new ResultOperation());
        //1.2 沥青批次生产过程阶段信息
        AsphaltProcessThread pro = new AsphaltProcessThread(forwardCount,selectCount,new ProcessOperation());
        //2.1 沥青运输(改性)
        AsphaltTransportGxThread gx = new AsphaltTransportGxThread(forwardCount,selectCount,new TransportGxOperation());
        //2.1 沥青运输(基质)
        AsphaltTransportJzThread jz = new AsphaltTransportJzThread(forwardCount,selectCount,new TransportJzOperation());
        //2.2 运输车轨迹
        VehicleGjThread gj = new VehicleGjThread(forwardCount,selectCount,new GjOperation());

        new Thread(res).start();
        new Thread(pro).start();
        new Thread(gx).start();
        new Thread(jz).start();
        new Thread(gj).start();

    }





}
