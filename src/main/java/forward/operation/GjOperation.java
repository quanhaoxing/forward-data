package forward.operation;

import cn.hutool.db.Entity;
import forward.dataBase.LqData;
import forward.util.LqHttpUtil;
import forward.util.PropertyUtil;
import forward.util.SqlUtil;
import org.apache.log4j.Logger;

import java.text.SimpleDateFormat;
import java.util.*;

public class GjOperation {

    public static final Logger logger = Logger.getLogger(GjOperation.class);

    public void forward() throws Exception {
        synchronized (LqData.gjList){
            while(LqData.gjList.isEmpty()){
                LqData.gjList.wait();
            }
            logger.info("----------------2.2 运输车轨迹缓存个数---------------->\t"+LqData.gjList.size());
            Entity entity = LqData.gjList.peek();

//            if (entity.getStr("VehicleId").isEmpty()) {
//                continue;
//            }
            String id = entity.getStr("Id");
            //编号
            String XT = "g" + String.format("%02d", Long.parseLong(entity.getStr("VehicleId"))) + "hsgs15";
            Double V = entity.getDouble("Speed");
            Double JD = entity.getDouble("Longitude");
            Double WD = entity.getDouble("Latitude");
            String SJ = entity.getStr("TransportTime");

            //请求参数
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("JD", JD);
            params.put("WD", WD);
            params.put("XT", XT);
            params.put("SJ", SJ);
            params.put("V", V);

            //请求头
            Map<String, String> headers = LqHttpUtil.HttpHeaders();

            //访问外部接口
            String yscUrl = PropertyUtil.getProperty("ysc.url");
            String result = LqHttpUtil.Post(yscUrl, headers, params);

//            logger.info("-------------请求参数----->" + JSONUtil.parseObj(params).toString());
            logger.info("---------2.2 运输车轨迹接口返回结果----->" + params.get("SJ").toString() + ":\t" + result);
            if(result.contains("200")){
                String tableName = "t024_VehicleGj";
                SqlUtil.update(tableName, id, true);
                LqData.gjList.poll();
            }
            if (LqData.gjList.isEmpty()) {
                LqData.gjList.notifyAll();
            }
        }

    }

    public void getData() throws Exception{
        synchronized (LqData.gjList){
            while (!LqData.gjList.isEmpty()) {
                LqData.gjList.wait();
            }
            List<Entity> results = null;
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            //访问内部数据库
            results = SqlUtil.select(
                    "select top 100 " +
                            "gj.Id, " +
                            "gj.Status, " +
                            "gj.VehicleId, " +
                            "gj.Speed, " +
                            "gj.Longitude, " +
                            "gj.Latitude, " +
                            "gj.TransportTime " +
                            " from t024_VehicleGj gj where " +
                            " gj.Status = 0 " +
                            " and gj.TransportTime >= '" + addMonth(dateFormat.format(calendar.getTime()),-1) + "'" +
                            " and gj.TransportTime <= '" + dateFormat.format(calendar.getTime())+ "'" +
                            " order by gj.TransportTime desc ");
            logger.info("-------------2.2 运输车轨迹获取数量----->\t" + (results == null? 0:results.size()));
            if (results != null && results.size() != 0 ) {
//                results.removeAll(gjList);
                LqData.gjList.addAll(results);
                LqData.gjList.notifyAll();
            }

        }
    }

    public String addMonth(String day, int month){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null;
        try {
            date = format.parse(day);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        if (date == null)
            return "";
//        System.out.println("front:" + format.format(date)); //显示输入的日期
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.MONTH, month);// 24小时制
        date = cal.getTime();
//        System.out.println("after:" + format.format(date));  //显示更新后的日期
        cal = null;
        return format.format(date);

    }

}
