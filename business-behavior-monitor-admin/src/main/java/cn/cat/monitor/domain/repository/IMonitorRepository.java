package cn.cat.monitor.domain.repository;

import cn.cat.monitor.domain.model.entity.MonitorDataEntity;
import cn.cat.monitor.domain.model.entity.MonitorDataMapEntity;
import cn.cat.monitor.domain.model.valobj.GatherNodeExpressionVO;

import java.util.List;

public interface IMonitorRepository {

    List<GatherNodeExpressionVO> queryGatherNodeExpressionVO(String systemName, String className, String methodName);

    String queryMonitoryNameByMonitoryId(String monitorId);

    void saveMonitoryData(MonitorDataEntity monitorDataEntity);

    List<MonitorDataMapEntity> queryMonitorDataMapEntityList();

}
