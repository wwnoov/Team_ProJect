package com.example.team_project.student.controller;

import com.example.team_project.class_gangui.dao.ClassDAO;
import com.example.team_project.class_gangui.dto.ClassDTO;
import lombok.extern.slf4j.Slf4j;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;

import static com.example.team_project.utils.JSFunction.alertLocation;

@WebServlet(name= "StudentClassDetailController", value = "/student/class/detail.do")
@Slf4j
public class StudentClassDetailController extends HttpServlet {

    private ClassDAO classDAO = new ClassDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // 세션 객체 생성
        HttpSession session = req.getSession();
        // 세션에서 학생 인덱스 가져오기
        int studentIdx = (Integer) session.getAttribute("student_idx");

        // 요청 파라미터에서 강의 인덱스 가져오기
        String classIdx = req.getParameter("classIdx");
        int classIdxInt = -1;

        // 강의 인덱스 유효성 검사
        if (classIdx == null || classIdx.isEmpty()) {
            alertLocation(resp, "올바르지 않은 강의 번호입니다", req.getContextPath() + "/student/class/list.do");
            return;
        } else {
            try {
                classIdxInt = Integer.parseInt(classIdx.trim());
            } catch (Exception e) {
                alertLocation(resp, "올바르지 않은 강의 번호입니다", req.getContextPath() + "/student/class/list.do");
                return;
            }
        }

        // 학생이 신청한 강의인지 확인
        int validCount = classDAO.checkIfSpecificStudentIdxRegisteredSpecificClassIdx(classIdxInt, studentIdx);
        if (validCount != 1) {
            alertLocation(resp, "수강 신청한 강의만 상세 조회할 수 있습니다", req.getContextPath() + "/student/class/list.do");
            return;
        }

        // 강의 상세 정보 가져오기
        ClassDTO classDTO = classDAO.getOneClassInformationWithRelatedLessons(classIdxInt);
        // 강의 정보를 요청 객체에 속성으로 추가
        req.setAttribute("classDTO", classDTO);
        // 강의 상세 정보 페이지로 포워딩
        req.getRequestDispatcher("/student/views/studentClassDetail.jsp")
                .forward(req, resp);
    }

    // 메시지 알림과 페이지 이동을 위한 메소드
    private void alertLocation(HttpServletResponse resp, String msg, String url) {
        try {
            resp.setContentType("text/html;charset=UTF-8");
            PrintWriter writer = resp.getWriter();
            String script = "<script>"
                    + "    alert('" + msg + "');"
                    + "    location.href='" + url + "';"
                    + "</script>";
            writer.print(script);
            writer.flush();
            writer.close();
        } catch (Exception e) {

        }
    }
}