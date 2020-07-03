package forward.util;

public class StringUtil {

    /**
     * 判断字符串是否为空
     * @param s
     * @return
     */
    public static String isNull(String s){
        if(s==null){
            s="";
        }
        return s;
    }
}
