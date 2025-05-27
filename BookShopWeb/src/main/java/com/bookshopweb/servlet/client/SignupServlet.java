package com.bookshopweb.servlet.client;

import com.bookshopweb.beans.User;
import com.bookshopweb.service.UserService;
import com.bookshopweb.utils.HashingUtils;
import com.bookshopweb.utils.Protector;
import com.bookshopweb.utils.Validator;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.*;

@WebServlet(name = "SignupServlet", value = "/signup")
public class SignupServlet extends HttpServlet {
    private final UserService userService = new UserService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        String csrfToken = (String) session.getAttribute("csrf_token");
        if (csrfToken == null) {
            csrfToken = UUID.randomUUID().toString();
            session.setAttribute("csrf_token", csrfToken);
        }
        request.setAttribute("csrf_token", csrfToken);
        request.getRequestDispatcher("/WEB-INF/views/signupView.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        String sessionToken = (String) session.getAttribute("csrf_token");
        String formToken = request.getParameter("csrf_token");

        if (sessionToken == null || formToken == null || !sessionToken.equals(formToken)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "CSRF token mismatch");
            return;
        }
        // Lưu các parameter (tên-giá trị) vào map values
        Map<String, String> values = new HashMap<>();
        values.put("username", request.getParameter("username"));
        values.put("password", request.getParameter("password"));
        values.put("fullname", request.getParameter("fullname"));
        values.put("email", request.getParameter("email"));
        values.put("phoneNumber", request.getParameter("phoneNumber"));
        values.put("gender", request.getParameter("gender"));
        values.put("address", request.getParameter("address"));
        values.put("policy", request.getParameter("policy"));

        // Kiểm tra các parameter, lưu các vi phạm (nếu có) vào map violations
        Map<String, List<String>> violations = new HashMap<>();
        Optional<User> userFromServer = Protector.of(() -> userService.getByUsername(values.get("username")))
                .get(Optional::empty);
        violations.put("usernameViolations", Validator.of(values.get("username"))
                .isNotNullAndEmpty()
                .isNotBlankAtBothEnds()
                .isAtMostOfLength(25)
                .isNotExistent(userFromServer.isPresent(), "Tên đăng nhập")
                .toList());
        violations.put("passwordViolations", Validator.of(values.get("password"))
                .isNotBlankAtBothEnds()
                .isNotNullAndEmpty()
                .isAtMostOfLength(32)
                .isAtLeastOfLength(8)
                .toList());
        violations.put("fullnameViolations", Validator.of(values.get("fullname"))
                .isNotNullAndEmpty()
                .isNotBlankAtBothEnds()
                .toList());
        violations.put("emailViolations", Validator.of(values.get("email"))
                .isNotNullAndEmpty()
                .isNotBlankAtBothEnds()
                .hasPattern("[a-zA-Z0-9]+@[a-zA-Z0-9]+\\.(com|vn)", "email")
                .toList());
        violations.put("phoneNumberViolations", Validator.of(values.get("phoneNumber"))
                .isNotNullAndEmpty()
                .isNotBlankAtBothEnds()
                .hasPattern("^(?!0{10})\\d{10,11}$", "số điện thoại")
                .toList());
        violations.put("genderViolations", Validator.of(values.get("gender"))
                .isNotNull()
                .toList());
        violations.put("addressViolations", Validator.of(values.get("address"))
                .isNotNullAndEmpty()
                .isNotBlankAtBothEnds()
                .toList());
        violations.put("policyViolations", Validator.of(values.get("policy"))
                .isNotNull()
                .toList());

        // Tính tổng các vi phạm sau kiểm tra (nếu có)
        int sumOfViolations = violations.values().stream().mapToInt(List::size).sum();
        String successMessage = "Đã đăng ký thành công!";
        String errorMessage = "Đã có lỗi truy vấn!";
        values.replaceAll((key, value) -> Protector.escapeHtml(value));

        // Khi không có vi phạm trong kiểm tra các parameter
        if (sumOfViolations == 0) {
            User user = new User(
                    0L,
                    values.get("username"),
                    HashingUtils.hash(values.get("password")),
                    values.get("fullname"),
                    values.get("email"),
                    values.get("phoneNumber"),
                    Protector.of(() -> Integer.parseInt(values.get("gender"))).get(0),
                    values.get("address"),
                    "CUSTOMER"
            );

            Protector.of(() -> userService.insert(user))
                    .done(r ->request.setAttribute("successMessage", Protector.escapeHtml(successMessage)))
                    .fail(e -> {
                        Map<String, String> safeValues = new HashMap<>();
                        values.forEach((key, value) -> safeValues.put(key, Protector.escapeHtml(value)));
                        request.setAttribute("values", safeValues);
                        request.setAttribute("errorMessage", Protector.escapeHtml(errorMessage));
                    });
        } else {
            // Khi có vi phạm
            Map<String, String> safeValues = new HashMap<>();
            values.forEach((key, value) -> safeValues.put(key, Protector.escapeHtml(value)));
            request.setAttribute("values", safeValues);
            request.setAttribute("violations", violations);
        }

        request.getRequestDispatcher("/WEB-INF/views/signupView.jsp").forward(request, response);
    }
}
