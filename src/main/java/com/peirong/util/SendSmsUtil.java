package com.peirong.util;

import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;
//导入可选配置类
import com.tencentcloudapi.common.profile.ClientProfile;
import com.tencentcloudapi.common.profile.HttpProfile;
// 导入对应SMS模块的client
import com.tencentcloudapi.sms.v20190711.SmsClient;
// 导入要请求接口对应的request response类
import com.tencentcloudapi.sms.v20190711.models.SendSmsRequest;
import com.tencentcloudapi.sms.v20190711.models.SendSmsResponse;
import com.tencentcloudapi.sms.v20190711.models.SendStatus;

import javax.servlet.http.HttpSession;

public class SendSmsUtil {
    public static SendStatus[] sendSms(String[] phoneNumber, String code) {
        SendStatus[] returnString = {};
        try {
            Credential cred = new Credential(Constants.SecretID, Constants.SecretKey);
            HttpProfile httpProfile = new HttpProfile();
            httpProfile.setReqMethod("POST");
            httpProfile.setConnTimeout(60);
            httpProfile.setEndpoint("sms.tencentcloudapi.com");
            ClientProfile clientProfile = new ClientProfile();
            clientProfile.setSignMethod("HmacSHA256");
            clientProfile.setHttpProfile(httpProfile);
            SmsClient client = new SmsClient(cred, "ap-nanjing", clientProfile);
            SendSmsRequest req = new SendSmsRequest();

            req.setSmsSdkAppid(Constants.SdkAppid);
            req.setSign(Constants.signName);
            req.setTemplateID(Constants.templateId);

            req.setPhoneNumberSet(phoneNumber);
            String[] templateParams = {code};

            req.setTemplateParamSet(templateParams);
            SendSmsResponse res = client.SendSms(req);
            System.out.println(SendSmsResponse.toJsonString(res));
            System.out.println(res.getRequestId());
            returnString = res.getSendStatusSet();

        } catch (TencentCloudSDKException e) {
            e.printStackTrace();
        }
        return returnString;
    }
}
