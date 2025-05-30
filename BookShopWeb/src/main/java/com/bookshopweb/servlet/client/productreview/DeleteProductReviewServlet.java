package com.bookshopweb.servlet.client.productreview;

import com.bookshopweb.service.ProductReviewService;
import com.bookshopweb.utils.Protector;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.UUID;

@WebServlet(name = "DeleteProductReviewServlet", value = "/deleteProductReview")
public class DeleteProductReviewServlet extends HttpServlet {
    private final ProductReviewService productReviewService = new ProductReviewService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {


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
        long productReviewId = Protector.of(() -> Long.parseLong(request.getParameter("productReviewId"))).get(0L);
        String productId = request.getParameter("productId");

        String successMessage = "Đã xóa đánh giá thành công!";
        String errorDeleteReviewMessage = "Đã có lỗi truy vấn!";

        Protector.of(() -> productReviewService.delete(productReviewId))
                .done(r -> request.getSession().setAttribute("successMessage", successMessage))
                .fail(e -> request.getSession().setAttribute("errorDeleteReviewMessage", errorDeleteReviewMessage));

        response.sendRedirect(request.getContextPath() + "/product?id=" + productId + "#review");
    }
}
