package com.arounders.web.controller;

import com.arounders.web.entity.Attachment;
import com.arounders.web.service.BoardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import com.arounders.web.dto.MemberDTO;

import com.arounders.web.entity.Member;
import com.arounders.web.service.AttachmentService;
import com.arounders.web.service.MemberService;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@Log4j2
@RequestMapping(value = "/mypage")
public class MypageController {

    private final BoardService boardService;

    private final MemberService memberService;

    private final AttachmentService attachmentService;

    private final HttpSession session;

    @GetMapping(value = "/dashboard")
    public String dashboard(Model model){

        /* Test용 */
        //Long memberId = 12L;
        /* Dev용 */
        Long memberId = (Long) session.getAttribute("id");

        Map<String, Integer> countMap = boardService.getCountListByCategory(memberId);

        model.addAttribute("countMap", countMap);

        log.info("#MypageController -> dashboard : ");
        countMap.forEach(log::info);

        Member user = memberService.getMember(memberId);
        log.info("requestURL: /mypage/dashboard, user: {}", user);
        model.addAttribute("user", user);


        return "mypage/dashboard";
    }

    @GetMapping(value = "/comments")
    public String comments(){

        return "mypage/comments";
    }

    @GetMapping(value = "/rooms")
    public String rooms(){

        return "mypage/rooms";
    }

    @GetMapping("/location")
    public String getUserLocation() {
        Long id = (Long) session.getAttribute("id");
        log.info("request url -> /mypage/location, session user id: {}", id);
        return "mypage/my-location";
    }

    @GetMapping("/info")
    @ResponseBody
    public Member getInfo(Model model) {

        Long id = (Long) session.getAttribute("id");
        log.info("request url -> /mypage/info, session user id: {}", id);

        String profileImg = (String) session.getAttribute("profileImg");
        log.info("request url -> /mypage/info, session profileImg {}", profileImg);

        /* get MemberInfo to put into form data set as default here */
        Member member = memberService.getMember(id);
        return member;
       // model.addAttribute("member", member);

        //return "mypage/myinfo";
    }

    @GetMapping(value="/profileImg")
    @ResponseBody
    public Attachment getProfileImg() {
        Long id = (Long) session.getAttribute("id");
        String path = attachmentService.findProfileImgPathById(id);
        return Attachment.builder().path(path).build();
    }

    @PostMapping("/update/info")
    @ResponseBody
    public int updateMemberInfo(MemberDTO memberDTO, @RequestParam(name="profileImg")MultipartFile multipartFile) {
        log.info("request url -> /mypage/update/info, memberDTO: {}, multipartFile: {}"
                , memberDTO, multipartFile.toString());

        Long id = (Long) session.getAttribute("id");
        log.info("request url -> /mypage/update/info, session user id: {}", id);

        String profileImg = (String) session.getAttribute("profileImg");
        log.info("request url -> /mypage/update/info, session profileImg {}", profileImg);

        /* set member id from session */
        memberDTO.setId(id);

        /* get real path then update Member Info */
        String realPath = session.getServletContext().getRealPath("/upload");

        /* updateMemberInfo here*/
        int result = memberService.updateMember(memberDTO, multipartFile, realPath);

        /* reset session if successful */
        if (result > 0) resetSession();

        return result;
    }

    @PostMapping("/update/address")
    @ResponseBody
    public int updateAddress(MemberDTO memberDTO) {

        Long id = (Long) session.getAttribute("id");
        log.info("request url -> /mypage/update/address, session user id: {}, memberDTO: {}",
                memberDTO, id);

        /* DTO settings */
        memberDTO.setId(id);
        memberDTO.setCityId(memberService.findCityId(memberDTO.getAddr()));

        /* updateAddress here */
        int result = memberService.updateAddress(memberDTO);
        /* session refresh */
        if (result > 0) resetSession();

        return result;
    }

    public void resetSession() {
        Long id = (Long) session.getAttribute("id");

        /* get user info */
        Member user = memberService.getMember(id);
        /* get profile image path */
        String profileImg = attachmentService.findProfileImgPathById(id);

        session.setAttribute("id", user.getId());
        session.setAttribute("nickname", user.getNickname());
        session.setAttribute("region", user.getAddr().split(" ")[1]);
        session.setAttribute("roleId", user.getRoleId());
        session.setAttribute("cityId", user.getCityId());
        session.setAttribute("profileImg", profileImg);
        log.info("reset session user info id : {}, nickname: {}, region: {}, role: {}, city: {}, profileImg: {}",
                user.getId(), user.getNickname(), user.getAddr().split(" ")[1], user.getRoleId(), user.getCityId(), profileImg);
    }
}

