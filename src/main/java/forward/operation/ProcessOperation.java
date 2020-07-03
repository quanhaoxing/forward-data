package forward.operation;

import cn.hutool.db.Entity;
import cn.hutool.json.JSONUtil;
import forward.dataBase.LqData;
import forward.util.LqHttpUtil;
import forward.util.PropertyUtil;
import forward.util.SqlUtil;
import forward.util.StringUtil;
import org.apache.log4j.Logger;

import java.util.*;

public class ProcessOperation {
    public static final Logger logger = Logger.getLogger(ProcessOperation.class);

    public void forward() throws Exception {
        synchronized (LqData.processList) {
            while (LqData.processList.isEmpty()) {
                LqData.processList.wait();
            }
            logger.info("----------------1.1 沥青批次生产过程缓存个数---------------->\t"+LqData.processList.size());
            Entity entity = LqData.processList.peek();
            String id = StringUtil.isNull(entity.getStr("Id"));
            //沥青加工厂Id必须是BH00049
            String mpNum = StringUtil.isNull(entity.getStr("DeviceNum"));
//                    String mpNum = StringUtil.isNull(entity.getStr("AsphaltFacId"));
            String stage = StringUtil.isNull(entity.getStr("PhaseType"));
            String taskNumber = StringUtil.isNull(entity.getStr("TaskNum"));
            String stageTem = StringUtil.isNull(entity.getStr("PhaseTemp"));
            String stageTime = StringUtil.isNull(entity.getStr("ProTime"));
            String stageFrequency = StringUtil.isNull(entity.getStr("Rate"));
            String stageTemLL = StringUtil.isNull(entity.getStr("TheoryTemp"));

            //请求参数
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("taskNumber", taskNumber);
            params.put("stage", stage);
            params.put("stageTime", stageTime);
            params.put("stageFrequency", stageFrequency);
            params.put("stageTem", stageTem);
            params.put("mpNum", mpNum);
            //阶段理论温度（暂时不传）
//          params.put("stageTemLL", stageTemLL);

            //请求头
            Map<String, String> headers = LqHttpUtil.HttpHeaders();

            //访问外部接口
            String yscUrl = PropertyUtil.getProperty("lpp.url");
            String result = LqHttpUtil.Post(yscUrl, headers, params);

            logger.info("-------------请求参数----->" + JSONUtil.parseObj(params).toString());
            logger.info("---------1.1 沥青批次生产过程返回结果----->" + result);

            if(result.contains("200")){
                String tableName = "t030_AsphaltProcess";
                SqlUtil.update(tableName, id, false);
                LqData.processList.poll();
            }
            if (LqData.processList.isEmpty()) {
                LqData.processList.notifyAll();
            }
        }
    }

    public void getData() throws Exception{
        synchronized (LqData.processList) {
            while (!LqData.processList.isEmpty()) {
                LqData.processList.wait();
            }
            List<Entity> results = null;
            //访问内部数据库
            results = SqlUtil.select(
                    "select ap.PhaseType," +
                            " ap.Id," +
                            " ap.TaskNum," +
                            " ap.DeviceNum, " +
                            " ap.PhaseTemp," +
                            " ap.ProTime," +
                            " ap.Rate," +
                            " ap.TheoryTemp," +
                            " ap.Status" +
                            " from t030_AsphaltProcess ap " +
                            " JOIN t030_asphaltProcessFactory af on ap.AsphaltFacId = af.Id " +
                            "where ap.AsphaltFacId = 1 and af.Image5 = 1 and ap.Status = 1 and ap.DeviceNum is not null " +
                            "order by ap.dataMakeTime desc ");
            logger.info("------------1.2 沥青批次生产过程获取数量----->\t" + (results == null? 0:results.size()));
            if (results != null && results.size() != 0) {
//                results.removeAll(processList);
                LqData.processList.addAll(results);
                LqData.processList.notifyAll();
            }
        }
    }
}
