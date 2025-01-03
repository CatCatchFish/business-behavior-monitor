package cn.cat.monitor.domain.service;

import cn.cat.monitor.domain.model.entity.MonitorDataEntity;
import cn.cat.monitor.domain.model.entity.MonitorDataMapEntity;
import cn.cat.monitor.domain.model.valobj.MonitorTreeConfigVO;
import ognl.OgnlException;

import java.util.List;

public interface ILogAnalyticalService {

    void doAnalytical(String systemName, String className, String methodName, List<String> logList) throws OgnlException;

    List<MonitorDataMapEntity> queryMonitorDataMapEntityList();

    MonitorTreeConfigVO queryMonitorFlowData(String monitorId);

    List<MonitorDataEntity> queryMonitorDataEntityList(String monitorId, String monitorName, String monitorNodeId);

}
