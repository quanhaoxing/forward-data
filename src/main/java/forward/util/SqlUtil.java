package forward.util;

import cn.hutool.core.date.DateUtil;
import cn.hutool.db.Db;
import cn.hutool.db.Entity;
import org.apache.log4j.Logger;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SqlUtil {
    public static final Logger logger = Logger.getLogger(SqlUtil.class);
    public static List<Entity> select(String sql) {
        List<Entity> list = null;
        try{
            list = Db.use().query(sql);
        }catch (SQLException s){
            logger.error(s.getMessage());
        }
        return list;

    }

    public static int update(String tableName, String id, boolean isGj){
        int count = 0;
        int status = 1;
        if(!isGj){
            status = 2;
        }

        try{
            count = Db.use().update(
                    Entity.create().set("Status", status)
                            .set("dataUpdTime", DateUtil.now())
                            .set("dataUpdUser", "AsphaltProduction")
                            .set("dataUpdPGM", "AsphaltProduction"),
                    Entity.create(tableName).set("Id", id)
            );
        }catch (SQLException s){
            logger.error(s.getMessage());
        }
        return count;
    }
}
