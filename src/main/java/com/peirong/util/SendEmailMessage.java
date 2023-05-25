package com.peirong.util;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Random;
import java.util.concurrent.ExecutorService;

@Configuration
public class SendEmailMessage {

    @Resource
    private SendEmailUtil sendEmailUtil;
    @Resource
    private SendSmsUtil sendSmsUtil;
    @Resource
    private HttpServletRequest request;
    @Resource
    private ExecutorService executorService;


    public void sendMessage(String account) throws Exception {

        String code = String.format("%06d", new Random().nextInt(100000));

        System.out.println(code);
        OkHttpClient client = new OkHttpClient();
        Request postRequest = new Request.Builder().url(sendSmsUtil.postURL(account, code)).get().build();
        HttpSession session = request.getSession();
        session.setMaxInactiveInterval(300);
        executorService.execute(() -> {
            try {
                String sendTool = client.newCall(postRequest).execute().body().string();
                session.setAttribute("phone-code-" + account, code);
            } catch (IOException e) {
                session.removeAttribute("phone-code-" + account);
                throw new RuntimeException(e);
            }
        });
    }

    public void sendEmail(String account) {
        String code = String.format("%06d", new Random().nextInt(100000));
        System.out.println(code);
        HttpSession session = request.getSession();
        session.setMaxInactiveInterval(60);
        try {
            executorService.execute(() -> sendEmailUtil.sendEmail(account, code));
            session.setAttribute("email-code-" + account, code);
        } catch (Exception ex) {
            session.removeAttribute("email-code-");
        }
    }
}
