package forward.operation;

import cn.hutool.db.Entity;
import cn.hutool.json.JSONUtil;
import forward.dataBase.LqData;
import forward.enums.ParamTypeEnum;
import forward.util.LqHttpUtil;
import forward.util.PropertyUtil;
import forward.util.SqlUtil;
import forward.util.StringUtil;
import org.apache.log4j.Logger;

import java.util.*;

public class TransportGxOperation {

    public static final Logger logger = Logger.getLogger(TransportGxOperation.class);

    public void forward() throws Exception {
        synchronized (LqData.transportGxList) {
            while (LqData.transportGxList.isEmpty()) {
                LqData.transportGxList.wait();
            }
            logger.info("----------------2.1 沥青运输(改性)缓存个数---------------->\t" + LqData.transportGxList.size());
            Entity entity = LqData.transportGxList.peek();
            String sportId = entity.getStr("sportId");
            String testId = entity.getStr("testId");
            String secId = StringUtil.isNull(entity.getStr("DeviceNum"));
            //运输车编号
            String vehicleNo = "g" + String.format("%02d", Long.parseLong(entity.getStr("VehicleId"))) + "hsgs15";
            //单号
            String taskNumber = StringUtil.isNull(entity.getStr("TaskNum"));

            //车牌号
            String vehicleNum = StringUtil.isNull(entity.getStr("vehicleNum"));
            //运输开始时间
            String loadStartTime = StringUtil.isNull(entity.getStr("TransportStrTime"));
            //运输结束时间
            String loadEndTime = null;
            if(entity.getStr("TransportEndTime") == null || entity.getStr("TransportEndTime").isEmpty()){
                loadEndTime = StringUtil.isNull(entity.getStr("TransportStrTime"));
            }else{
                loadEndTime = StringUtil.isNull(entity.getStr("TransportEndTime"));
            }
            //到达工程标段
            //沥青加工厂Id必须是BH00049
//            String secId = "BH00049";
            //重量
            String weight = StringUtil.isNull(entity.getStr("TransportWeight"));
            //样品名称
            String sampleName = StringUtil.isNull(entity.getStr("SampleName"));
            //取样地点
            String samplingSite = StringUtil.isNull(entity.getStr("SampleAddress"));
            //取样日期
            String samplingDate = StringUtil.isNull(entity.getStr("SampleTime"));
            //样品编号
            String sampleNum = StringUtil.isNull(entity.getStr("SampleNum"));
            if(taskNumber.isEmpty()){
                taskNumber = sampleNum;
            }
            //校验日期
            String checkTime = StringUtil.isNull(entity.getStr("TestTime"));
            //报告编号
            String reportNum = StringUtil.isNull(entity.getStr("ReportNum"));
            //校验标准
            String trialCriteria = StringUtil.isNull(entity.getStr("TestCriterion"));
            //实验结果
            String trialResult = StringUtil.isNull(entity.getStr("TestResult"));
            //备注
            String remark1 = StringUtil.isNull(entity.getStr("Remark"));
            //说明
            String explain = StringUtil.isNull(entity.getStr("Explain"));
            //提货单位
            String company = StringUtil.isNull(entity.getStr("fName"));
            //到达地
            String area = StringUtil.isNull(entity.getStr("adress"));
            //查询电话
            String phone = StringUtil.isNull(entity.getStr("EnquirePhone"));
            //责任人
            String personLiable = StringUtil.isNull(entity.getStr("ChargePerson"));
            //复核人
            String checkPerson = StringUtil.isNull(entity.getStr("RecheckPerson"));
            //校验员
            String inspector = StringUtil.isNull(entity.getStr("CheckPerson"));

            Map<String, Object> params = new HashMap<String, Object>();
            Map<String, Object> map = new HashMap<String, Object>();
            params.put("vehicleNo", vehicleNo);
            params.put("vehicleNum", vehicleNum);
            params.put("taskNumber", taskNumber);
            params.put("loadStartTime", loadStartTime);
            params.put("loadEndTime", loadEndTime);
            params.put("secId", secId);
            params.put("weight", weight);
            params.put("sampleName", sampleName);
            params.put("samplingSite", samplingSite);
            params.put("samplingDate", samplingDate);
            params.put("sampleNum", sampleNum);
            params.put("checkTime", checkTime);
            params.put("reportNum", reportNum);
            params.put("trialCriteria", trialCriteria);
            params.put("trialResult", trialResult);
            params.put("remark1", remark1);
            params.put("explain", explain);
            params.put("company", company);
            params.put("area", area);
            params.put("phone", phone);
            params.put("personLiable", personLiable);
            params.put("checkPerson", checkPerson);
            params.put("inspector", inspector);
            for (int i = 0; i < entity.size(); i++) {
                for (ParamTypeEnum type : ParamTypeEnum.values()) {
                    map = this.containerBox(i + 1, entity, type.getName());
//                            logger.info("map: "+map.toString());
                    if (map != null && map.size() > 0) {
                        params.put(type.getCode(), JSONUtil.parseObj(map).toString());
                        map.clear();
                        break;
                    }
                }
            }

            //请求头
            Map<String, String> headers = LqHttpUtil.HttpHeaders();

            //访问外部接口
            String lyUrl = PropertyUtil.getProperty("ly.url");
            String result = LqHttpUtil.Post(lyUrl, headers, params);

            logger.info("-------------请求参数----->" + JSONUtil.parseObj(params).toString());
            logger.info("---------2.1 沥青运输(改性)接口返回结果----->" + result);

            if (result.contains("200")) {
                String t030_asphaltTransport = "t030_asphaltTransport";
                String t030_asphaltTestHistory = "t030_asphaltTestHistory";
                SqlUtil.update(t030_asphaltTransport, sportId, false);
                SqlUtil.update(t030_asphaltTestHistory, testId, false);
                LqData.transportGxList.poll();
            }
            if (LqData.transportGxList.isEmpty()) {
                LqData.transportGxList.notifyAll();
            }
        }

    }

    public void getData() throws Exception{
        synchronized (LqData.transportGxList) {
            while (!LqData.transportGxList.isEmpty()) {
                LqData.transportGxList.wait();
            }
            List<Entity> results = null;
            //访问内部数据库
            results = SqlUtil.select(
                    "SELECT\n" +
                            "\ttest.id testId,\n" +
                            "\tsport.id sportId,\n" +
                            "\ttest.Status hStatus,\n" +
                            "\tsport.Status sStatus,\n" +
                            "\tvi.VehicleNum vehicleNum, \n" +
                            "\tsport.VehicleId ,\n" +
                            "\tbd.FactoryName fName,\n" +
                            "\tbd.Address adress,\n" +
                            "\tbd.DeviceNum DeviceNum,\n" +
                            "\tsport.TransportStrTime ,\n" +
                            "\tsport.TransportEndTime ,\n" +
                            "\tsport.TransportWeight ,\n" +
                            "\tsport.SampleName ,\n" +
                            "\tsport.SampleAddress ,\n" +
                            "\tsport.SampleTime ,\n" +
                            "\tsport.SampleNum ,\n" +
                            "\tsport.ReportNum ,\n" +
                            "\tsport.Remark ,\n" +
                            "\tsport.Explain ,\n" +
                            "\tsport.EnquirePhone ,\n" +
                            "\tsport.ChargePerson ,\n" +
                            "\tsport.RecheckPerson ,\n" +
                            "\tsport.CheckPerson ,\n" +
                            "\tsport.TaskNum ,\n" +
                            "\ttest.TestTime ,\n" +
                            "\ttest.TestResult,\n" +
                            "\ttest.TestCriterion,\n" +
                            "\ttest.Tag1Medhod,\n" +
                            "\ttest.Tag2Medhod,\n" +
                            "\ttest.Tag3Medhod,\n" +
                            "\ttest.Tag4Medhod,\n" +
                            "\ttest.Tag5Medhod,\n" +
                            "\ttest.Tag6Medhod,\n" +
                            "\ttest.Tag7Medhod,\n" +
                            "\ttest.Tag8Medhod,\n" +
                            "\ttest.Tag9Medhod,\n" +
                            "\ttest.Tag10Medhod,\n" +
                            "\ttest.Tag11Medhod,\n" +
                            "\ttest.Tag12Medhod,\n" +
                            "\ttest.Tag13Medhod,\n" +
                            "\ttest.Tag14Medhod,\n" +
                            "\ttest.Tag15Medhod,\n" +
                            "\ttest.Tag16Medhod,\n" +
                            "\ttest.Tag17Medhod,\n" +
                            "\ttest.Tag18Medhod,\n" +
                            "\ttest.Tag19Medhod,\n" +
                            "\ttest.Tag20Medhod,\n" +
                            "\ttest.Tag1Result,\n" +
                            "\ttest.Tag2Result,\n" +
                            "\ttest.Tag3Result,\n" +
                            "\ttest.Tag4Result,\n" +
                            "\ttest.Tag5Result,\n" +
                            "\ttest.Tag6Result,\n" +
                            "\ttest.Tag7Result,\n" +
                            "\ttest.Tag8Result,\n" +
                            "\ttest.Tag9Result,\n" +
                            "\ttest.Tag10Result,\n" +
                            "\ttest.Tag11Result,\n" +
                            "\ttest.Tag12Result,\n" +
                            "\ttest.Tag13Result,\n" +
                            "\ttest.Tag14Result,\n" +
                            "\ttest.Tag15Result,\n" +
                            "\ttest.Tag16Result,\n" +
                            "\ttest.Tag17Result,\n" +
                            "\ttest.Tag18Result,\n" +
                            "\ttest.Tag19Result,\n" +
                            "\ttest.Tag20Result,\n" +
                            "\ttest.Tag1Demand,\n" +
                            "\ttest.Tag2Demand,\n" +
                            "\ttest.Tag3Demand,\n" +
                            "\ttest.Tag4Demand,\n" +
                            "\ttest.Tag5Demand,\n" +
                            "\ttest.Tag6Demand,\n" +
                            "\ttest.Tag7Demand,\n" +
                            "\ttest.Tag8Demand,\n" +
                            "\ttest.Tag9Demand,\n" +
                            "\ttest.Tag10Demand,\n" +
                            "\ttest.Tag11Demand,\n" +
                            "\ttest.Tag12Demand,\n" +
                            "\ttest.Tag13Demand,\n" +
                            "\ttest.Tag14Demand,\n" +
                            "\ttest.Tag15Demand,\n" +
                            "\ttest.Tag16Demand,\n" +
                            "\ttest.Tag17Demand,\n" +
                            "\ttest.Tag18Demand,\n" +
                            "\ttest.Tag19Demand,\n" +
                            "\ttest.Tag20Demand,\n" +
                            "\ttag.Tag1Name,\n" +
                            "\ttag.Tag2Name,\n" +
                            "\ttag.Tag3Name,\n" +
                            "\ttag.Tag4Name,\n" +
                            "\ttag.Tag5Name,\n" +
                            "\ttag.Tag6Name,\n" +
                            "\ttag.Tag7Name,\n" +
                            "\ttag.Tag8Name,\n" +
                            "\ttag.Tag9Name,\n" +
                            "\ttag.Tag10Name,\n" +
                            "\ttag.Tag11Name,\n" +
                            "\ttag.Tag12Name,\n" +
                            "\ttag.Tag13Name,\n" +
                            "\ttag.Tag14Name,\n" +
                            "\ttag.Tag15Name,\n" +
                            "\ttag.Tag16Name,\n" +
                            "\ttag.Tag17Name,\n" +
                            "\ttag.Tag18Name,\n" +
                            "\ttag.Tag19Name,\n" +
                            "\ttag.Tag20Name\n" +
                            "FROM\n" +
                            "\t t030_asphaltTransport sport \n" +
                            "\t JOIN t030_asphaltProcessFactory af on sport.AsphaltFacId = af.Id\n" +
                            "\t JOIN t030_asphaltTestHistory test ON test.AsphaltFacId = sport.AsphaltFacId and sport.ReportNum = test.ReportNum\n" +
                            "\t JOIN t030_asphaltTestTag tag ON tag.AsphaltFacId = sport.AsphaltFacId \n" +
                            "\t JOIN t030_asphaltTransportBid bd ON sport.arriveBidId = bd.Id\n" +
                            "\t JOIN t024_vehicleInfo vi ON vi.id = sport.VehicleId \n" +
                            "WHERE\n" +
                            "\t sport.Status in (0,1) and test.TestType = 1 and tag.TestType = 1\n" +
                            "\t and sport.AsphaltFacId = 1 and af.Image5 = 1 \n" +
                            "\t order by sport.dataMakeTime desc ");
            logger.info("------------2.1 沥青运输(改性)获取数量----->\t" + (results == null? 0:results.size()));
            if (results != null && results.size() != 0) {
//                results.removeAll(transportList);
                LqData.transportGxList.addAll(results);
                LqData.transportGxList.notifyAll();
            }
        }

    }

    public Map<String, Object> containerBox(int i, Entity entity, String str) {
        Map<String, Object> in = new HashMap<String, Object>();
        if (str.equals(entity.getStr("Tag" + i + "Name"))) {
            in.put("quality", entity.getStr("Tag" + i + "Demand"));
            in.put("result", entity.getStr("Tag" + i + "Result"));
            in.put("method", entity.getStr("Tag" + i + "Medhod"));
        }
        return in;
    }
}
