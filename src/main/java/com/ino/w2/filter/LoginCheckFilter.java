package com.ino.w2.filter;

import com.ino.w2.dto.MemberDTO;
import com.ino.w2.service.MemberService;
import lombok.extern.log4j.Log4j2;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

@WebFilter(urlPatterns = {"/todo/*"})
@Log4j2
public class LoginCheckFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        log.info("Login check filter..........");

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        HttpSession session = req.getSession();

        if (session.getAttribute("loginInfo") != null) {
            chain.doFilter(request, response);
            return;
        }

        //session에 loginInfo 값이 없다면?
        //쿠키를 체크
        Cookie cookie = findCookie(req.getCookies(), "remember-me");

        //세션에도 없고 쿠키도 없다면 그냥 로그인으로
        if (cookie == null) {
            resp.sendRedirect("/login");
            return;
        }

        //쿠키가 있다면?
        log.info("cookie는 있어요");
        //uuid값 확인
        String uuid = cookie.getValue();

        try {
            MemberDTO memberDTO = MemberService.INSTANCE.getByUUID(uuid);
            log.info("쿠키의 값으로 조회한 사용자 정보 : " + memberDTO);
            if (memberDTO == null) {
                throw new RuntimeException("쿠키가 유효하지 않습니다.");
            }
            //회원 정보를 세션에 추가
            session.setAttribute("loginInfo", memberDTO);
            chain.doFilter(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            resp.sendRedirect("/login");
        }

    }

    private Cookie findCookie(Cookie[] cookies, String name) {

        if (cookies == null && cookies.length == 0) {
            return null;
        }

        Optional<Cookie> result = Arrays.stream(cookies)
                .filter(ck -> ck.getName().equals(name))
                .findFirst();

        return result.isPresent() ? result.get() : null;
    }

}
