package behavioral.behavioral_tmp123.templateMethod;

public class TemplateMethod {
    public static void main(String[] args) {
        Logger l = LoggerFactory.createLogger("FILE");
        l.Log("MyApp", "SEVERITY", " NOTHIN SERIOUS");
        l = LoggerFactory.createLogger("DB");
        l.Log("MyApp", "SEVERITY TO DB", "NOTHIN SERIOUS");
    }
}

abstract class Logger {
    protected abstract boolean DoLog(String logitem);

    public boolean Log(String app, String key, String cause) {
        return DoLog(app + " " + key + " " + cause);
    }
}

class DbLogger extends Logger {
    protected boolean DoLog(String logitem) {
        System.out.println("Db log " + logitem);
        return true;
    }
}

class FileLogger extends Logger {
    protected boolean DoLog(String logitem) {
        System.out.println("File Log " + logitem);
        return true;
    }
}

class NullLogger extends Logger {
    protected boolean DoLog(String logitem) {
        System.out.println("Ignoring the log");
        return true;
    }
}

class LoggerFactory {
    public static Logger createLogger(String loggerType) {
        if (loggerType == "DB")
            return new DbLogger();
        else if (loggerType == "FILE")
            return new FileLogger();
        else
            return new NullLogger();
    }
}
