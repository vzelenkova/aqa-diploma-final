package data;

import com.github.javafaker.Faker;

import java.util.Locale;

/**
 * Utility class that generates test data for UI and API scenarios.
 */
public class DataHelper {
    private static final Faker faker = new Faker(new Locale("ru"));

    private DataHelper() { }

    public static CardInfo getApprovedCard() {
        return new CardInfo("4444 4444 4444 4441", getMonth(), getYear(), getName(), getCvc());
    }

    public static CardInfo getDeclinedCard() {
//        return new CardInfo("0000 0000 0000 0000", getMonth(), getYear(), getName(), getCvc());
        return new CardInfo("4444 4444 4444 4442", getMonth(), getYear(), getName(), getCvc());
    }

    public static CardInfo getInvalidCardMonth() {
        return new CardInfo("0000 0000 0000 0000", "13", "00", "Invalid Holder", "000");
    }

    public static CardInfo getInvalidCardYear() {
        return new CardInfo("0000 0000 0000 0000", "12", "24", "Invalid Holder", "000");
    }

    private static String getMonth() {
        return String.format("%02d", faker.number().numberBetween(1, 12));
    }

    private static String getYear() {
        // генерируем ближайшие 5 лет
        int current = java.time.Year.now().getValue() % 100;
        int year = faker.number().numberBetween(current + 1, current + 5);
        return String.format("%02d", year);
    }

    private static String getName() {
        return faker.name().fullName().toUpperCase();
    }

    private static String getCvc() {
        return String.format("%03d", faker.number().numberBetween(100, 999));
    }
}
