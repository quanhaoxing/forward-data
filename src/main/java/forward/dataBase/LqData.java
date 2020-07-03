package forward.dataBase;

import cn.hutool.db.Entity;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;

public class LqData {

    public static volatile Queue<Entity> gjList = new ConcurrentLinkedDeque<Entity>();
    public static volatile Queue<Entity> processList = new ConcurrentLinkedDeque<>();
    public static volatile Queue<Entity> resultList = new ConcurrentLinkedDeque<>();
    public static volatile Queue<Entity> transportGxList = new ConcurrentLinkedDeque<>();
    public static volatile  Queue<Entity> transportJzList = new ConcurrentLinkedDeque<>();
}
