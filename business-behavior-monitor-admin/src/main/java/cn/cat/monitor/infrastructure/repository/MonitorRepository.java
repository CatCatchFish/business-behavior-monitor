package cn.cat.monitor.infrastructure.repository;

import cn.cat.monitor.domain.model.entity.MonitorDataEntity;
import cn.cat.monitor.domain.model.entity.MonitorDataMapEntity;
import cn.cat.monitor.domain.model.valobj.GatherNodeExpressionVO;
import cn.cat.monitor.domain.model.valobj.MonitorTreeConfigVO;
import cn.cat.monitor.domain.repository.IMonitorRepository;
import cn.cat.monitor.infrastructure.dao.*;
import cn.cat.monitor.infrastructure.po.*;
import cn.cat.monitor.infrastructure.redis.IRedisService;
import cn.cat.monitor.types.Constants;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
    private IMonitorDataMapNodeLinkDao monitorDataMapNodeLinkDao;
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

    @Override
    public List<MonitorDataMapEntity> queryMonitorDataMapEntityList() {
        List<MonitorDataMap> monitorDataMaps = monitorDataMapDao.queryMonitorDataMapList();

        List<MonitorDataMapEntity> monitorDataMapEntities = new ArrayList<>();
        for (MonitorDataMap monitorDataMap : monitorDataMaps) {
            MonitorDataMapEntity monitorDataEntity = MonitorDataMapEntity.builder()
                    .monitorId(monitorDataMap.getMonitorId())
                    .monitorName(monitorDataMap.getMonitorName())
                    .build();
            monitorDataMapEntities.add(monitorDataEntity);
        }
        return monitorDataMapEntities;
    }

    @Override
    public MonitorTreeConfigVO queryMonitorFlowData(String monitorId) {
        // 1. 查询监控节点和节点连线信息
        List<MonitorDataMapNode> monitorDataMapNodes = monitorDataMapNodeDao.queryMonitoryDataMapNodeByMonitorId(monitorId);
        List<MonitorDataMapNodeLink> monitorDataMapNodeLinks = monitorDataMapNodeLinkDao.queryMonitorDataMapNodeLinkByMonitorId(monitorId);

        // 2. 将连线信息按起点分组，建立起点到终点ID的映射关系
        Map<String, List<String>> fromMonitorNodeIdToNodeIds = monitorDataMapNodeLinks.stream()
                .collect(Collectors.groupingBy(MonitorDataMapNodeLink::getFromMonitorNodeId,
                        Collectors.mapping(MonitorDataMapNodeLink::getToMonitorNodeId, Collectors.toList())));

        // 3. 构造节点信息列表
        List<MonitorTreeConfigVO.Node> nodeList = new ArrayList<>();
        for (MonitorDataMapNode monitorDataMapNode : monitorDataMapNodes) {
            // 查询缓存节点流量值
            String cacheKey = Constants.RedisKey.monitor_node_data_count_key + monitorId + Constants.UNDERLINE + monitorDataMapNode.getMonitorNodeId();
            Long count = redisService.getAtomicLong(cacheKey);
            // 构造节点配置信息
            nodeList.add(MonitorTreeConfigVO.Node.builder()
                    .monitorNodeId(monitorDataMapNode.getMonitorNodeId())
                    .monitorNodeName(monitorDataMapNode.getMonitorNodeName())
                    .loc(monitorDataMapNode.getLoc())
                    .color(monitorDataMapNode.getColor())
                    .monitorNodeValue(null == count ? "0" : String.valueOf(count))
                    .build());
        }

        // 4. 构造连线信息列表
        List<MonitorTreeConfigVO.Link> linkList = new ArrayList<>();
        for (MonitorDataMapNodeLink monitorDataMapNodeLink : monitorDataMapNodeLinks) {
            // 获取节点值
            String fromCacheKey = Constants.RedisKey.monitor_node_data_count_key + monitorId + Constants.UNDERLINE + monitorDataMapNodeLink.getFromMonitorNodeId();
            Long fromCacheCount = redisService.getAtomicLong(fromCacheKey);
            Long toCacheCount = 0L;

            // 合并所有值
            List<String> toNodeIds = fromMonitorNodeIdToNodeIds.get(monitorDataMapNodeLink.getFromMonitorNodeId());
            for (String toNodeId : toNodeIds) {
                String toCacheKey = Constants.RedisKey.monitor_node_data_count_key + monitorId + Constants.UNDERLINE + toNodeId;
                toCacheCount += redisService.getAtomicLong(toCacheKey);
            }

            // 计算连线的差异流量值
            long differenceValue = (null == fromCacheCount ? 0L : fromCacheCount) - toCacheCount;
            linkList.add(MonitorTreeConfigVO.Link.builder()
                    .fromMonitorNodeId(monitorDataMapNodeLink.getFromMonitorNodeId())
                    .toMonitorNodeId(monitorDataMapNodeLink.getToMonitorNodeId())
                    .linkKey(String.valueOf(monitorDataMapNodeLink.getId()))
                    .linkValue(String.valueOf(differenceValue <= 0 ? 0 : differenceValue))
                    .build());
        }

        // 5. 返回监控树配置信息
        return MonitorTreeConfigVO.builder()
                .monitorId(monitorId)
                .nodeList(nodeList)
                .linkList(linkList)
                .build();
    }

}
