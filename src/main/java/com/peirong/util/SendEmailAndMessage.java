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
public class SendEmailAndMessage {

    @Resource
    private SendEmailUtil sendEmailUtil;
    @Resource
    private SendSmsUtil sendSmsUtil;
    @Resource
    private HttpServletRequest request;
    @Resource
    private ExecutorService executorService;


    public void sendMessage(String account) throws Exception {
        Integer codeSendToUser = ValidateCode.generateValidateCode(6);
        String code = String.valueOf(codeSendToUser);
        System.out.println(code);
        OkHttpClient client = new OkHttpClient();
        Request postRequest = new Request.Builder().url(sendSmsUtil.postURL(account, code)).get().build();
        HttpSession session = request.getSession();
        session.setMaxInactiveInterval(300);
        executorService.execute(() -> {
            try {
                String sendTool = client.newCall(postRequest).execute().body().string();
                session.setAttribute("phone-code-" + account, code);
                System.out.println("phone-code-" + account);
            } catch (IOException e) {
                session.removeAttribute("phone-code-" + account);
                throw new RuntimeException(e);
            }
        });
    }

    public void sendEmail(String account) {
        String code = String.format("%05d", new Random().nextInt(100000));
        HttpSession session = request.getSession();
        session.setMaxInactiveInterval(60);
        System.out.println(account);
        try {
            executorService.execute(() -> sendEmailUtil.sendEmail(account, code));
            session.setAttribute("email-code-" + account, code);
        } catch (Exception ex) {
            session.removeAttribute("email-code-");
        }
    }
}
