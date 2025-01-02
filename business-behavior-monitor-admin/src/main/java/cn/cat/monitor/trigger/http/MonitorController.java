package cn.cat.monitor.trigger.http;

import cn.cat.monitor.domain.model.entity.MonitorDataMapEntity;
import cn.cat.monitor.domain.model.valobj.MonitorTreeConfigVO;
import cn.cat.monitor.domain.service.ILogAnalyticalService;
import cn.cat.monitor.trigger.http.dto.MonitorDataMapDTO;
import cn.cat.monitor.trigger.http.dto.MonitorFlowDataDTO;
import cn.cat.monitor.types.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController()
@CrossOrigin("*")
@RequestMapping("/api/v1/monitor/")
public class MonitorController {

    @Resource
    private ILogAnalyticalService logAnalyticalService;

    @RequestMapping(value = "query_monitor_data_map_entity_list", method = RequestMethod.GET)
    public Response<List<MonitorDataMapDTO>> queryMonitorDataMapEntityList() {
        try {
            List<MonitorDataMapEntity> monitorDataMapEntities = logAnalyticalService.queryMonitorDataMapEntityList();
            List<MonitorDataMapDTO> monitorDataMapDTOS = new ArrayList<>(monitorDataMapEntities.size());
            for (MonitorDataMapEntity monitorDataMapEntity : monitorDataMapEntities) {
                monitorDataMapDTOS.add(MonitorDataMapDTO.builder()
                        .monitorId(monitorDataMapEntity.getMonitorId())
                        .monitorName(monitorDataMapEntity.getMonitorName())
                        .build());
            }
            return Response.<List<MonitorDataMapDTO>>builder()
                    .code("0000")
                    .info("调用成功")
                    .data(monitorDataMapDTOS)
                    .build();
        } catch (Exception e) {
            log.error("查询监控列表数据失败", e);
            return Response.<List<MonitorDataMapDTO>>builder()
                    .code("0001")
                    .info("调用失败")
                    .build();
        }
    }

    @RequestMapping(value = "query_monitor_flow_map", method = RequestMethod.GET)
    public Response<MonitorFlowDataDTO> queryMonitorFlowMap(@RequestParam String monitorId) {
        try {
            log.info("查询监控流数据 monitorId:{}", monitorId);
            MonitorTreeConfigVO monitorTreeConfigVO = logAnalyticalService.queryMonitorFlowData(monitorId);

            List<MonitorTreeConfigVO.Node> nodeList = monitorTreeConfigVO.getNodeList();
            List<MonitorTreeConfigVO.Link> linkList = monitorTreeConfigVO.getLinkList();

            List<MonitorFlowDataDTO.NodeData> nodeDataList = new ArrayList<>();
            for (MonitorTreeConfigVO.Node node : nodeList) {
                nodeDataList.add(new MonitorFlowDataDTO.NodeData(
                        node.getMonitorNodeId(),
                        node.getMonitorNodeId(),
                        node.getMonitorNodeName(),
                        node.getMonitorNodeValue(),
                        node.getLoc(),
                        node.getColor()));
            }

            List<MonitorFlowDataDTO.LinkData> linkDataList = new ArrayList<>();
            for (MonitorTreeConfigVO.Link link : linkList) {
                String linkValue = link.getLinkValue();
                linkDataList.add("0".equals(linkValue) ?
                        new MonitorFlowDataDTO.LinkData(link.getFromMonitorNodeId(), link.getToMonitorNodeId()) :
                        new MonitorFlowDataDTO.LinkData(link.getFromMonitorNodeId(), link.getToMonitorNodeId(), link.getLinkKey(), linkValue));
            }
            MonitorFlowDataDTO monitorFlowDataDTO = new MonitorFlowDataDTO();
            monitorFlowDataDTO.setNodeDataArray(nodeDataList);
            monitorFlowDataDTO.setLinkDataArray(linkDataList);

            return Response.<MonitorFlowDataDTO>builder()
                    .code("0000")
                    .info("调用成功")
                    .data(monitorFlowDataDTO)
                    .build();
        } catch (Exception e) {
            log.error("查询监控流数据失败 monitorId:{}", monitorId, e);
            return Response.<MonitorFlowDataDTO>builder()
                    .code("0001")
                    .info("调用失败")
                    .build();
        }
    }
}
