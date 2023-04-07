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
    private static String SECRET_ID;
    private static String SECRET_KEY;
    @Value("${SECRET_ID}")
    public String setSECRET_ID(String mySECRET_ID) {
        return SECRET_ID = mySECRET_ID;
    }
    @Value("${SECRET_KEY}")
    public String setSECRET_KEY(String mySECRET_KEY) {
        return SECRET_KEY = mySECRET_KEY;
    }

    @Test
    public void testPassword() throws Exception {

        System.out.println(SECRET_ID);
        System.out.println(SECRET_KEY);

    }
}
