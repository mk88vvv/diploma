import java.util.ArrayList;
import java.util.Objects;

public enum Admins {
    CEO(429272623L), DEVELOPER(673725464L);
    private Long id;

    public static boolean checkIsAdmin(Long chatId) {
        Admins[] admins = Admins.values();
        for (Admins admin : admins) {
            if (Objects.equals(admin.getId(), chatId)) {
                return true;
            }
        }
        return false;
    }


    Admins(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}
