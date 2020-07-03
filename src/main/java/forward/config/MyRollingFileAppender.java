package forward.config;

import org.apache.log4j.Priority;
import org.apache.log4j.RollingFileAppender;

import java.io.File;

public class MyRollingFileAppender extends RollingFileAppender {
    @Override
    public boolean isAsSevereAsThreshold(Priority priority) {
        return this.getThreshold().equals(priority);
    }

    @Override
    public void setFile(String file) {
        String filePath = file;
        File fileCheck = new File(filePath);
        if (!fileCheck.exists())
            fileCheck.getParentFile().mkdirs();
        super.setFile(filePath);
    }


}
