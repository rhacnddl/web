package com.arounders.web.controller;
import com.arounders.web.dto.BufferMap;
import com.arounders.web.dto.ChatDTO;

import com.arounders.web.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Controller
@RequiredArgsConstructor
@Log4j2
public class StompController {

    private final HttpServletRequest request;
    private final SimpMessagingTemplate template;
    private final ChatService chatService;

    private final BufferMap<Long, List<ChatDTO>> chatListMap;

    private final Map<Long, Integer> countMap;

    @MessageMapping(value = "/chat/enter")
    public void enter(ChatDTO message){
        message.setTime(getDate());
        message.setMessage("입장하였습니다.");

        /* Temp Logic */
        Long roomId = message.getChatRoomId();
        if (!countMap.containsKey(roomId))
            countMap.put(roomId, 1);
        else
            countMap.replace(roomId, countMap.get(roomId) + 1);

        log.info(countMap.get(roomId));
        /* Temp Logic */

        template.convertAndSend("/sub/chat/room/" + message.getChatRoomId(), message);
    }

    @MessageMapping(value = "/chat/exit")
    public void exit(ChatDTO message){
        message.setTime(getDate());
        message.setMessage("퇴장하셨습니다.");

        /* Temp Logic */
        Long roomId = message.getChatRoomId();

        countMap.replace(roomId, countMap.get(roomId) - 1);

        log.info("#StompController -> exit : Room No. " + message.getChatRoomId());
        log.info("인원수 : " + countMap.get(roomId));

        if(countMap.get(roomId) == 0){
            chatService.save(roomId, chatListMap.get(roomId), request.getServletContext().getRealPath("/chat"));

            countMap.remove(roomId);
            chatListMap.remove(roomId);
        }
        /* Temp Logic */

        template.convertAndSend("/sub/chat/room/" + message.getChatRoomId(), message);
    }

    @MessageMapping(value = "/chat/message")
    public void message(ChatDTO message){

        message.setTime(getDate());
        /* Temp Logic */
        Long roomId = message.getChatRoomId();

        if(!chatListMap.containsKey(roomId)) {
            chatListMap.put(roomId, new ArrayList<>());
        }

        List<ChatDTO> chatList = chatListMap.get(roomId);

        chatList.add(message);

        if(chatList.size() > 30){
            chatService.save(roomId, chatList, request.getServletContext().getRealPath("/chat"));
            chatList.clear();
        }
        /* Temp Logic */

        //chatService.save(message.getChatRoomId(), message);

        template.convertAndSend("/sub/chat/room/" + message.getChatRoomId(), message);
    }

    private String getDate(){

        ZonedDateTime nowKR = ZonedDateTime.now(ZoneId.of("Asia/Seoul"));
        LocalDateTime now = nowKR.withZoneSameInstant(ZoneId.of("Asia/Seoul")).toLocalDateTime();

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        return dtf.format(now);
    }
}
