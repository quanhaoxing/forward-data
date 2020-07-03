package forward.util;

import org.apache.log4j.Logger;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertyUtil {
    private static Properties props;
    public static final Logger logger = Logger.getLogger(PropertyUtil.class);
    static{
        loadProps();
    }

    synchronized static private void loadProps(){
//        logger.info("开始加载properties文件内容.......");
        props = new Properties();
        InputStream in = null;
        try {
            //通过类加载器进行获取properties文件流
            in = PropertyUtil.class.getClassLoader().getResourceAsStream("param.properties");
            props.load(in);
        } catch (FileNotFoundException e) {
            logger.info("param.properties文件未找到");
        } catch (IOException e) {
            logger.info("出现IOException");
        } finally {
            try {
                if(null != in) {
                    in.close();
                }
            } catch (IOException e) {
                logger.info("param.properties文件流关闭出现异常");
            }
        }
//        System.out.println("加载properties文件内容完成...........");
//        System.out.println("properties文件内容：" + props);
    }

    /**
     * 判断是否含有 key
     * @param key
     * @return
     */
    public static boolean containKey(String key){
        if(null == props) {
            loadProps();
        }
        return props.containsKey(key);
    }

    /**
     * 根据key获取key的值
     * @param key
     * @return
     */
    public static String getProperty(String key){
        if(null == props) {
            loadProps();
        }
        return props.getProperty(key);
    }

    /**
     * 根据key获取key的值
     * @param key
     * @param defaultValue 如果返回的值为null则给一个默认值
     * @return
     */
    public static String getProperty(String key, String defaultValue) {
        if(null == props) {
            loadProps();
        }
        return props.getProperty(key, defaultValue);
    }

}
