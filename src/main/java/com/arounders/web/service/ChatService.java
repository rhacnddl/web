package com.arounders.web.service;

import com.arounders.web.dto.ChatDTO;
import com.arounders.web.dto.ChatRoomDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.tomcat.util.json.JSONParser;

import java.util.List;

public interface ChatService {

    /* 채팅 저장 */
    void save(Long chatRoomId, List<ChatDTO> list);
    /* 채팅 불러오기 */
    List<String> getChats(Long id);

    default String dtoToJson(ChatDTO dto){
        ObjectMapper mapper = new ObjectMapper();
        String chat = null;
        try {
            chat = mapper.writeValueAsString(dto);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return chat;
    }
}
