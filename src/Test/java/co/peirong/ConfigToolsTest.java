package co.peirong;

import com.peirong.App;
import org.jasypt.encryption.StringEncryptor;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.annotation.Resource;

@SpringBootTest(classes = {App.class})
public class ConfigToolsTest {
    @Resource
    private StringEncryptor stringEncryptor;
    @Test
    public void testPassword() throws Exception {

    }
}
