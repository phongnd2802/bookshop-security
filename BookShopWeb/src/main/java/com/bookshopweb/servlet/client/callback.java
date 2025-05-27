package com.bookshopweb.servlet.client;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

@WebServlet("/callback")
public class callback extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String clientId = "1088499236693-v50rr2v1k9p3t17khkcum2og4l0g3a3v.apps.googleusercontent.com";
        String clientSecret = "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";
        String redirectUri = "http://localhost:8080/demo1_war_exploded/callback"; // Cần phải khớp với URI đã đăng ký

        String code = request.getParameter("code");
        if (code != null) {
            String tokenUrl = "https://oauth2.googleapis.com/token";
            String postData = "code=" + code + "&client_id=" + clientId + "&client_secret=" + clientSecret
                    + "&redirect_uri=" + redirectUri + "&grant_type=authorization_code";

            try {
                URL url = new URL(tokenUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
                connection.getOutputStream().write(postData.getBytes("UTF-8"));

                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    javax.servlet.http.HttpSession session = request.getSession();
                    InputStreamReader reader = new InputStreamReader(connection.getInputStream());
                    JsonObject jsonResponse = JsonParser.parseReader(reader).getAsJsonObject();
                    String accessToken = jsonResponse.get("access_token").getAsString();

                    // Có thể lưu accessToken vào session và chuyển hướng đến trang chính của ứng dụng
                    session.setAttribute("access_token", accessToken);
                    response.sendRedirect("welcome.jsp");
                }  // Xử lý lỗi nếu có

            } catch (IOException e) {
                // Xử lý lỗi nếu có
            }
        }  // Xử lý lỗi nếu không có mã xác thực

    }
}
