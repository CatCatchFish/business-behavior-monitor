package cn.cat.monitor.sdk.appender;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.UnsynchronizedAppenderBase;
import cn.cat.monitor.sdk.model.LogMessage;
import cn.cat.monitor.sdk.push.IPush;
import cn.cat.monitor.sdk.push.impl.RedisPush;

import java.util.Arrays;

public class CustomAppender<E> extends UnsynchronizedAppenderBase<E> {

    // 系统名称
    private String systemName;
    // 只采集指定范围的日志
    private String groupId;
    // redis 连接地址
    private String host;
    // redis 连接端口
    private int port;

    private final IPush push = new RedisPush();

    @Override
    public void start() {
        push.open(host, port);
        super.start();
    }

    @Override
    protected void append(E e) {
        if (!(e instanceof ILoggingEvent)) return;

        ILoggingEvent event = (ILoggingEvent) e;

        String methodName = "unknown";
        String className = "unknown";

        StackTraceElement[] callerDataArray = event.getCallerData();
        if (null != callerDataArray && callerDataArray.length > 0) {
            StackTraceElement callerData = callerDataArray[0];
            methodName = callerData.getMethodName();
            className = callerData.getClassName();
        }

        if (!className.startsWith(groupId)) return;

        // 构建日志
        LogMessage logMessage = new LogMessage(systemName, className, methodName, Arrays.asList(event.getFormattedMessage().split(" ")));
        // 推送日志
        push.send(logMessage);
    }

    public String getSystemName() {
        return systemName;
    }

    public void setSystemName(String systemName) {
        this.systemName = systemName;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public IPush getPush() {
        return push;
    }

}
