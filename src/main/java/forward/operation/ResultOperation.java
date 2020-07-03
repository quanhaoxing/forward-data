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

public class ResultOperation {

    public static final Logger logger = Logger.getLogger(ResultOperation.class);

    public void forward() throws Exception {
        synchronized(LqData.resultList){
            while (LqData.resultList.isEmpty()) {
                LqData.resultList.wait();
            }
            logger.info("----------------1.2 沥青批次生产信息缓存个数---------------->\t"+LqData.resultList.size());
            Entity entity = LqData.resultList.peek();
            String id = StringUtil.isNull(entity.getStr("Id"));
            String taskNumber = StringUtil.isNull(entity.getStr("TaskNum"));
            String deviceNum = StringUtil.isNull(entity.getStr("DeviceNum"));
            String mix1 = StringUtil.isNull(entity.getStr("AsphaltWeightSj"));
            String mix1Sbs = StringUtil.isNull(entity.getStr("SBSWeightSj"));
            String viscosify = StringUtil.isNull(entity.getStr("ZYJWeightSj"));
            String rubberOil = StringUtil.isNull(entity.getStr("RubberWeight"));
            String endLq = StringUtil.isNull(entity.getStr("ProWeight"));
            String stabilizer = StringUtil.isNull(entity.getStr("WDJWeightSj"));
            String beginTime = StringUtil.isNull(entity.getStr("StartTime"));
            String endTime = StringUtil.isNull(entity.getStr("EndTime"));
//            System.out.println("--------------时间------------->\t" + beginTime);
            String lqBrand = StringUtil.isNull(entity.getStr("AsphaltBrand"));
            String sbsBrand = StringUtil.isNull(entity.getStr("SBSBrand"));
            String viscosifyBrand = StringUtil.isNull(entity.getStr("ZYJBrand"));
            String rubberOilBrand = StringUtil.isNull(entity.getStr("RubberBrand"));
            String stabilizerBrand = StringUtil.isNull(entity.getStr("WDJBrand"));
            String mix1Th = StringUtil.isNull(entity.getStr("AsphaltWeightLL"));
            String mix1SbsTh = StringUtil.isNull(entity.getStr("SBSWeightLL"));
            String viscosifyTh = StringUtil.isNull(entity.getStr("ZYJWeightLL"));
            String rubberOilTh = StringUtil.isNull(entity.getStr(""));
            String stabilizerTh = StringUtil.isNull(entity.getStr("WDJWeightLL"));
            String mix1Ra = StringUtil.isNull(entity.getStr("AsphaltPb"));
            String mix1SbsRa = StringUtil.isNull(entity.getStr("SBSPb"));
            String viscosifyRa = StringUtil.isNull(entity.getStr("ZYJPb"));
            String rubberOilRa = StringUtil.isNull(entity.getStr(""));
            String stabilizerRa = StringUtil.isNull(entity.getStr("WDJPb"));


            //请求参数
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("taskNumber", taskNumber);
            params.put("mpNum", deviceNum);
            params.put("mix1", mix1);
            params.put("mix1Sbs", mix1Sbs);
            params.put("viscosify", viscosify);
            params.put("rubberOil", rubberOil);
            params.put("endLq", endLq);
            params.put("stabilizer", stabilizer);
            params.put("beginTime", beginTime);
            params.put("endTime", endTime);
            params.put("lqBrand", lqBrand);
            params.put("sbsBrand", sbsBrand);
            params.put("viscosifyBrand", viscosifyBrand);
            params.put("rubberOilBrand", rubberOilBrand);
            params.put("mix1Th", mix1Th);
            params.put("mix1SbsTh", mix1SbsTh);
            params.put("viscosifyTh", viscosifyTh);
            params.put("rubberOilTh", rubberOilTh);
            params.put("stabilizerTh", stabilizerTh);
            params.put("mix1Ra", mix1Ra);
            params.put("mix1SbsRa", mix1SbsRa);
            params.put("viscosifyRa", viscosifyRa);
            params.put("rubberOilRa", rubberOilRa);
            params.put("stabilizerRa", stabilizerRa);
            params.put("stabilizerBrand", stabilizerBrand);

            //请求头
            Map<String, String> headers = LqHttpUtil.HttpHeaders();

            //访问外部接口
            String lsxUrl = PropertyUtil.getProperty("lsx.url");
            String result = LqHttpUtil.Post(lsxUrl, headers, params);

            logger.info("-------------请求参数----->" + JSONUtil.parseObj(params).toString());
            logger.info("---------1.2 沥青批次生产信息接口返回结果----->" + result);

            if(result.contains("200")){
                String tableName = "t030_asphaltResult";
                SqlUtil.update(tableName, id, false);
                LqData.resultList.poll();
            }
            if (LqData.resultList.isEmpty()) {
                LqData.resultList.notifyAll();
            }
        }
    }

    public void getData() throws Exception{
        synchronized (LqData.resultList) {
            while (!LqData.resultList.isEmpty()) {
                LqData.resultList.wait();
            }
            List<Entity> results = null;
            results = SqlUtil.select(
                    "select " +
                            "ar.Id, " +
                            "ar.TaskNum, " +
                            "ar.DeviceNum, " +
                            "ar.Status, " +
                            "ar.AsphaltWeightSj, " +
                            "ar.SBSWeightSj, " +
                            "ar.ZYJWeightSj, " +
                            "ar.ProWeight, " +
                            "ar.WDJWeightSj, " +
                            "ar.StartTime, " +
                            "ar.EndTime, " +
                            "ar.AsphaltBrand, " +
                            "ar.SBSBrand, " +
                            "ar.ZYJBrand, " +
                            "ar.WDJBrand, " +
                            "ar.AsphaltWeightLL, " +
                            "ar.SBSWeightLL, " +
                            "ar.ZYJWeightLL, " +
                            "ar.WDJWeightLL, " +
                            "ar.AsphaltPb, " +
                            "ar.SBSPb, " +
                            "ar.ZYJPb, " +
                            "ar.WDJPb " +
//                            "ar.*, " +
//                            "ar.*, " +
//                            "ar.*, " +
                            " from t030_asphaltResult ar " +
                            " JOIN t030_asphaltProcessFactory af on ar.AsphaltFacId = af.Id " +
                            "where ar.AsphaltFacId = 1 and af.Image5 = 1 and ar.Status = 1 and ar.DeviceNum is not null " +
                            " order by ar.dataMakeTime desc");
            logger.info("------------1.1 沥青批次生产信息获取数量----->\t" + (results == null? 0:results.size()));
            if (results != null && results.size() != 0) {
//                results.removeAll(resultList);
                LqData.resultList.addAll(results);
                LqData.resultList.notifyAll();
            }
        }

    }
}
