package com.arounders.web.api;

import com.arounders.web.dto.ReportDTO;
import com.arounders.web.entity.Report;
import com.arounders.web.service.ReportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;

@RestController
@RequestMapping(value = "/reports/api/v1")
@Log4j2
@RequiredArgsConstructor
public class ReportApiController {

    private final ReportService service;
    private final HttpSession session;

    /* None Using */
    @GetMapping("/{id}")
    public ResponseEntity<ReportDTO> getReport(@PathVariable("id") Long id){

        ReportDTO report = service.getReport(id);
        log.info("#ReportApiController : getReport -> " + report);

        return report != null? new ResponseEntity<>(report, HttpStatus.OK) :
                new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /* /reports/api/v1?status=? */
    @GetMapping("")
    public ResponseEntity<List<ReportDTO>> getReports(@RequestParam(value = "status", required = false) Integer status){

        /* 목록 조회 */
        List<ReportDTO> list = service.getReports(status);

        log.info("#ReportApiController : getReports -> " + status);
        list.forEach(log::info);

        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @PostMapping("/boards/{boardId}")
    public ResponseEntity<Long> report(@PathVariable("boardId") Long boardId){

        /* test용 */
        //Long memberId = 12L;
        /* 실제 사용 */
        Long memberId = (Long) session.getAttribute("id");
        log.info(memberId + "번 회원이 " + boardId + "번 게시글을 신고했습니다.");
        Long id = service.register(Report.builder().memberId(memberId).boardId(boardId).build());

        return new ResponseEntity<>(id, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Long> confirm(@PathVariable("id") Long id){

        log.info("관리자가 " + id + "번째 신고를 처리했습니다.");
        service.finish(id);

        return new ResponseEntity<>(id, HttpStatus.OK);
    }
}
