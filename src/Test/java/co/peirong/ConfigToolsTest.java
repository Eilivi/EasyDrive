package co.peirong;

import com.peirong.App;
import org.jasypt.encryption.StringEncryptor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import javax.annotation.Resource;

@SpringBootTest(classes = {App.class})
public class ConfigToolsTest {
    @Autowired
    private StringEncryptor stringEncryptor;
    @Resource
    BCryptPasswordEncoder encoder;

    @Test
    public void testPassword() throws Exception {



        System.out.println(encoder.encode("Re4030dd."));

        System.out.println(stringEncryptor.encrypt(""));
    }
}
