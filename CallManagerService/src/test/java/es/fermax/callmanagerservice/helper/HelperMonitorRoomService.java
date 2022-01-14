package es.fermax.callmanagerservice.helper;

import es.fermax.callmanagerservice.controller.dto.MonitorDTO;

public class HelperMonitorRoomService {


    public static MonitorDTO getStartMonitorRoom(String id) {
        MonitorDTO monitorDTO = new MonitorDTO();
        monitorDTO.setRoomId(id);
        return monitorDTO;
    }
}
