package co.peirong;

import com.peirong.App;
import org.jasypt.encryption.StringEncryptor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import static com.alibaba.druid.filter.config.ConfigTools.encrypt;
import static com.alibaba.druid.filter.config.ConfigTools.genKeyPair;

@SpringBootTest(classes = {App.class})
public class ConfigToolsTest {

    @Autowired
    private StringEncryptor stringEncryptor;

//    @Value("${spring.mail.password}")
//    private String username;

    @Test
    public void testPassword() throws Exception {
       // System.out.println(username);

        String username = stringEncryptor.encrypt("ez.drive@foxmail.com");
        //String password = stringEncryptor.encrypt("oduomzrmiurreebj");


        System.out.println(username);
        //System.out.println(password);

    }
}
