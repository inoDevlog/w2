package com.ino.w2.controller;

import com.ino.w2.dto.TodoDTO;
import com.ino.w2.service.TodoService;
import lombok.extern.log4j.Log4j2;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@WebServlet(name = "todoRegisterController", value = "/todo/register")
@Log4j2
public class TodoRegisterController extends HttpServlet {

    private TodoService todoService = TodoService.INSTANCE;
    private final DateTimeFormatter DATEFORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        log.info(("/todo/register GET.........."));

        HttpSession session = req.getSession();

        // JSESSIONID 없는 새로운 사용자
        if (session.isNew()) {
            log.info("JSESSION 쿠키가 새로 만들어진 사용자");
            resp.sendRedirect("/login");
            return;
        }

        // JSESSIONID는 있지만 session context에 loginInfo라는 이름으로 저장된 객체가 없는 경우
        if (session.getAttribute("loginInfo") == null) {
            log.info("로그인한 정보가 없는 사용자");
            resp.sendRedirect("/login");
            return;
        }

        // 정상적인 경우?
        req.getRequestDispatcher("/WEB-INF/todo/register.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        TodoDTO todoDTO = TodoDTO.builder()
                .title(req.getParameter("title"))
                .dueDate(LocalDate.parse(req.getParameter("dueDate"), DATEFORMATTER))
                .build();

        log.info("/todo/register POST..........");
        log.info(todoDTO);
        try {
            todoService.register(todoDTO);

        } catch (Exception e) {
            e.printStackTrace();
        }
        resp.sendRedirect("/todo/list");
    }
}
