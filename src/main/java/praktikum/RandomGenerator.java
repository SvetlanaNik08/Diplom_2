package praktikum;
import org.apache.commons.lang3.RandomStringUtils;

public class RandomGenerator {
    public static User getRandom() {
        String userEmail = RandomStringUtils.randomAlphabetic(10).toLowerCase() + "@yandex.ru";
        String userPassword = RandomStringUtils.randomAlphabetic(10);
        String userName = RandomStringUtils.randomAlphabetic(10);
        return new User(userEmail, userPassword, userName);
    }
}
