package cn.cat.monitor.trigger.listener;

import cn.cat.monitor.sdk.model.LogMessage;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.listener.MessageListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MonitorLogListener implements MessageListener<LogMessage> {

    @Override
    public void onMessage(CharSequence charSequence, LogMessage logMessage) {

    }

}
