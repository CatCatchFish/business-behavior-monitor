package cn.cat.monitor.domain.service;

import cn.cat.monitor.domain.model.entity.MonitorDataEntity;
import cn.cat.monitor.domain.model.entity.MonitorDataMapEntity;
import cn.cat.monitor.domain.model.valobj.GatherNodeExpressionVO;
import cn.cat.monitor.domain.model.valobj.MonitorTreeConfigVO;
import cn.cat.monitor.domain.repository.IMonitorRepository;
import cn.cat.monitor.types.Constants;
import com.alibaba.fastjson.JSONObject;
import ognl.Ognl;
import ognl.OgnlContext;
import ognl.OgnlException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class LogAnalyticalService implements ILogAnalyticalService {

    @Resource
    private IMonitorRepository repository;

    @Override
    public void doAnalytical(String systemName, String className, String methodName, List<String> logList) throws OgnlException {
        // 查询匹配解析节点
        List<GatherNodeExpressionVO> gatherNodeExpressionVOs = repository.queryGatherNodeExpressionVO(systemName, className, methodName);
        if (null == gatherNodeExpressionVOs || gatherNodeExpressionVOs.isEmpty()) return;

        // 解析日志
        for (GatherNodeExpressionVO gatherNodeExpressionVO : gatherNodeExpressionVOs) {
            String monitoryName = repository.queryMonitoryNameByMonitoryId(gatherNodeExpressionVO.getMonitorId());

            List<GatherNodeExpressionVO.Filed> fields = gatherNodeExpressionVO.getFields();
            for (GatherNodeExpressionVO.Filed field : fields) {
                Integer logIndex = field.getLogIndex();

                String logName = logList.get(0);
                if (!logName.equals(field.getLogName())) continue;

                String attributeValue = "";
                String logStr = logList.get(logIndex);
                if ("Object".equals(field.getLogType())) {
                    OgnlContext context = new OgnlContext();
                    context.setRoot(JSONObject.parseObject(logStr));
                    Object root = context.getRoot();
                    attributeValue = String.valueOf(Ognl.getValue(field.getAttributeOgnl(), context, root));
                } else {
                    attributeValue = logStr.trim();
                    if (attributeValue.contains(Constants.COLON)) {
                        attributeValue = attributeValue.split(Constants.COLON)[1].trim();
                    }
                }

                MonitorDataEntity monitorDataEntity = MonitorDataEntity.builder()
                        .monitorId(gatherNodeExpressionVO.getMonitorId())
                        .monitorName(monitoryName)
                        .monitorNodeId(gatherNodeExpressionVO.getMonitorNodeId())
                        .systemName(gatherNodeExpressionVO.getGatherSystemName())
                        .clazzName(gatherNodeExpressionVO.getGatherClazzName())
                        .methodName(gatherNodeExpressionVO.getGatherMethodName())
                        .attributeName(field.getAttributeName())
                        .attributeField(field.getAttributeField())
                        .attributeValue(attributeValue)
                        .build();

                repository.saveMonitoryData(monitorDataEntity);
            }
        }
    }

    @Override
    public List<MonitorDataMapEntity> queryMonitorDataMapEntityList() {
        return repository.queryMonitorDataMapEntityList();
    }

    @Override
    public MonitorTreeConfigVO queryMonitorFlowData(String monitorId) {
        return repository.queryMonitorFlowData(monitorId);
    }

}
