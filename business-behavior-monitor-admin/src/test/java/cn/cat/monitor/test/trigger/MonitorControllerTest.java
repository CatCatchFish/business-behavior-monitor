package cn.cat.monitor.test.trigger;

import cn.cat.monitor.trigger.http.MonitorController;
import cn.cat.monitor.trigger.http.dto.MonitorDataDTO;
import cn.cat.monitor.trigger.http.dto.MonitorFlowDataDTO;
import cn.cat.monitor.types.Response;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.List;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class MonitorControllerTest {

    @Resource
    private MonitorController monitorController;

    @Test
    public void test_queryMonitorFlowMap() {
        Response<MonitorFlowDataDTO> response = monitorController.queryMonitorFlowMap("111");
        log.info("测试结果: {}", JSON.toJSONString(response));
    }

    @Test
    public void test_queryMonitorFlowData() {
        Response<List<MonitorDataDTO>> response = monitorController.queryMonitorDataList("111", "测试工程", "112");
        log.info("测试结果: {}", JSON.toJSONString(response));
    }

}
