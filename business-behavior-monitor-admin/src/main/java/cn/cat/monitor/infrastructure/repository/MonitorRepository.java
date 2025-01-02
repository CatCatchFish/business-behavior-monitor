package cn.cat.monitor.infrastructure.repository;

import cn.cat.monitor.domain.model.entity.MonitorDataEntity;
import cn.cat.monitor.domain.model.valobj.GatherNodeExpressionVO;
import cn.cat.monitor.domain.repository.IMonitorRepository;
import cn.cat.monitor.infrastructure.dao.*;
import cn.cat.monitor.infrastructure.po.MonitorData;
import cn.cat.monitor.infrastructure.po.MonitorDataMapNode;
import cn.cat.monitor.infrastructure.po.MonitorDataMapNodeField;
import cn.cat.monitor.infrastructure.redis.IRedisService;
import cn.cat.monitor.types.Constants;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Repository
public class MonitorRepository implements IMonitorRepository {
    @Resource
    private IMonitorDataDao monitorDataDao;
    @Resource
    private IMonitorDataMapDao monitorDataMapDao;
    @Resource
    private IMonitorDataMapNodeDao monitorDataMapNodeDao;
    @Resource
    private IMonitorDataMapNodeFieldDao monitorDataMapNodeFieldDao;
    @Resource
    private IRedisService redisService;

    @Override
    public List<GatherNodeExpressionVO> queryGatherNodeExpressionVO(String systemName, String className, String methodName) {
        // 1. 查询采集节点
        MonitorDataMapNode monitorDataMapNodeReq = new MonitorDataMapNode();
        monitorDataMapNodeReq.setGatherSystemName(systemName);
        monitorDataMapNodeReq.setGatherClazzName(className);
        monitorDataMapNodeReq.setGatherMethodName(methodName);
        List<MonitorDataMapNode> monitorDataMapNodes = monitorDataMapNodeDao.queryMonitoryDataMapNodeList(monitorDataMapNodeReq);
        if (monitorDataMapNodes.isEmpty()) return null;

        List<GatherNodeExpressionVO> gatherNodeExpressionVOS = new ArrayList<>();
        for (MonitorDataMapNode monitorDataMapNode : monitorDataMapNodes) {
            // 2. 查询采集节点的字段
            String monitorId = monitorDataMapNode.getMonitorId();
            String monitorNodeId = monitorDataMapNode.getMonitorNodeId();

            MonitorDataMapNodeField monitorDataMapNodeFieldReq = new MonitorDataMapNodeField();
            monitorDataMapNodeFieldReq.setMonitorId(monitorId);
            monitorDataMapNodeFieldReq.setMonitorNodeId(monitorNodeId);
            List<MonitorDataMapNodeField> monitorDataMapNodeFields = monitorDataMapNodeFieldDao.queryMonitorDataMapNodeList(monitorDataMapNodeFieldReq);

            List<GatherNodeExpressionVO.Filed> fields = new ArrayList<>();
            for (MonitorDataMapNodeField monitorDataMapNodeField : monitorDataMapNodeFields) {
                fields.add(GatherNodeExpressionVO.Filed.builder()
                        .logName(monitorDataMapNodeField.getLogName())
                        .logIndex(monitorDataMapNodeField.getLogIndex())
                        .logType(monitorDataMapNodeField.getLogType())
                        .attributeField(monitorDataMapNodeField.getAttributeField())
                        .attributeName(monitorDataMapNodeField.getAttributeName())
                        .attributeOgnl(monitorDataMapNodeField.getAttributeOgnl())
                        .build());
            }

            gatherNodeExpressionVOS.add(GatherNodeExpressionVO.builder()
                    .monitorId(monitorDataMapNode.getMonitorId())
                    .monitorNodeId(monitorDataMapNode.getMonitorNodeId())
                    .gatherSystemName(monitorDataMapNode.getGatherSystemName())
                    .gatherClazzName(monitorDataMapNode.getGatherClazzName())
                    .gatherMethodName(monitorDataMapNode.getGatherMethodName())
                    .fields(fields)
                    .build());
        }
        return gatherNodeExpressionVOS;
    }

    @Override
    public String queryMonitoryNameByMonitoryId(String monitorId) {
        return monitorDataMapDao.queryMonitorNameByMonitoryId(monitorId);
    }

    @Override
    public void saveMonitoryData(MonitorDataEntity monitorDataEntity) {
        MonitorData monitorDataReq = new MonitorData();
        monitorDataReq.setMonitorId(monitorDataEntity.getMonitorId());
        monitorDataReq.setMonitorName(monitorDataEntity.getMonitorName());
        monitorDataReq.setMonitorNodeId(monitorDataEntity.getMonitorNodeId());
        monitorDataReq.setSystemName(monitorDataEntity.getSystemName());
        monitorDataReq.setClazzName(monitorDataEntity.getClazzName());
        monitorDataReq.setMethodName(monitorDataEntity.getMethodName());
        monitorDataReq.setAttributeName(monitorDataEntity.getAttributeName());
        monitorDataReq.setAttributeField(monitorDataEntity.getAttributeField());
        monitorDataReq.setAttributeValue(monitorDataEntity.getAttributeValue());

        monitorDataDao.insert(monitorDataReq);

        String cacheKey = Constants.RedisKey.monitor_node_data_count_key + monitorDataEntity.getMonitorId() + Constants.UNDERLINE + monitorDataEntity.getMonitorNodeId();
        redisService.incr(cacheKey);
    }

}
