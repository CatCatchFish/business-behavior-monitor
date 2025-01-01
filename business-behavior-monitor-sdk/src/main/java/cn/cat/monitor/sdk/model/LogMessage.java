package cn.cat.monitor.sdk.model;

import java.util.List;

public class LogMessage {

    private String systemName;

    private String className;

    private String methodName;

    private List<String> logList;

    public LogMessage() {
    }

    public LogMessage(String systemName, String className, String methodName, List<String> logList) {
        this.systemName = systemName;
        this.className = className;
        this.methodName = methodName;
        this.logList = logList;
    }

    public String getSystemName() {
        return systemName;
    }

    public void setSystemName(String systemName) {
        this.systemName = systemName;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public List<String> getLogList() {
        return logList;
    }

    public void setLogList(List<String> logList) {
        this.logList = logList;
    }

}
