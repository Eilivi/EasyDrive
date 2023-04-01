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

    public static SendStatus[] sendSms(String[] phoneNumber ) {

        SendStatus[] returString= {};

        try {

            Credential cred = new Credential(Constants.SecretID, Constants.SecretKey);

            HttpProfile httpProfile = new HttpProfile();
            httpProfile.setReqMethod("POST");
            httpProfile.setConnTimeout(60);

            httpProfile.setEndpoint("sms.tencentcloudapi.com");

            ClientProfile clientProfile = new ClientProfile();
            /* SDK默认用TC3-HMAC-SHA256进行签名 */
            clientProfile.setSignMethod("HmacSHA256");
            clientProfile.setHttpProfile(httpProfile);

            SmsClient client = new SmsClient(cred, "ap-nanjing", clientProfile);

            SendSmsRequest req = new SendSmsRequest();
            req.setSmsSdkAppid(Constants.SdkAppid);
            req.setSign(Constants.signName);
            req.setTemplateID(Constants.templateId);

            String[] phoneNumbers = {"+86" + phoneNumber};
            req.setPhoneNumberSet(phoneNumber);

            //验证码个数
            int vode = ValidateCode.generateValidateCode(6);
            String code = String.valueOf(vode);
            Constants.voicode = code;

            /* 模板参数: 若无模板参数，则设置为空*/
            String[] templateParams = {code};
            req.setTemplateParamSet(templateParams);

            /* 通过 client 对象调用 SendSms 方法发起请求。注意请求方法名与请求对象是对应的
             * 返回的 res 是一个 SendSmsResponse 类的实例，与请求对象对应 */
            SendSmsResponse res = client.SendSms(req);
            System.out.println(SendSmsResponse.toJsonString(res));
            System.out.println(res.getRequestId());
            returString=res.getSendStatusSet();

        } catch (TencentCloudSDKException e) {
            e.printStackTrace();
        }

        return returString;
    }
}
