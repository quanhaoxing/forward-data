package forward.util;

import java.math.BigDecimal;

public class PositionUtil {
    /**
     * 转换度分
     * @param longitude
     * @return
     */
    public static  Double gPSCoordinate(Double longitude){
        //获取整数部分
        double floor = Math.floor(longitude);
        //获取小数部分
        double decimals = longitude - floor;
        //还原度分
        double s = decimals/100*60;
        double x = (floor+s)*100;
        BigDecimal b   =   new   BigDecimal(x);
        double   f1   =   b.setScale(7,   BigDecimal.ROUND_HALF_UP).doubleValue();
        return  f1;
    }
}
