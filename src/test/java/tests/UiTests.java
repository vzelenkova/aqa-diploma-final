package tests;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Screenshots;
import data.DataHelper;
import db.DbUtils;
import io.github.bonigarcia.wdm.WebDriverManager;
import io.qameta.allure.Allure;
import org.junit.jupiter.api.*;
import org.openqa.selenium.chrome.ChromeOptions;
import page.CardPage;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;

import static com.codeborne.selenide.Selenide.*;


@DisplayName("UI тесты")
public class UiTests {
    private CardPage cardPage;

    @BeforeAll
    static void setupAll() {
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.setBinary("/opt/google/chrome/google-chrome");
        options.addArguments("--no-sandbox", "--disable-dev-shm-usage");
        Configuration.browser = "chrome";
        Configuration.headless = true;
        Configuration.browserSize = "1920x1080";
        Configuration.browserCapabilities = options;
    }

    @BeforeEach
    void openPage() throws Exception {
        DbUtils.clearTables();
        open(System.getProperty("sut.url", "http://localhost:8080"));
        cardPage = new CardPage();
    }

    @DisplayName("Ошибки при всех незаполненных полях")
    @Test
    void shouldShowValidationErrorsForEmptyFields() {
        cardPage.clickButton("Купить");
        cardPage.clickButton("Продолжить");
        cardPage.shouldHighlightInvalidFields(
                Map.of(
                        "Номер карты", "Неверный формат",
                        "Месяц", "Неверный формат",
                        "Год", "Неверный формат",
                        "Владелец", "Поле обязательно для заполнения",
                        "CVC/CVV", "Неверный формат"
                )
        );
    }

    @DisplayName("Ошибка при вводе недействительного месяца")
    @Test
    void shouldRejectInvalidCardMonth() {
        cardPage.clickButton("Купить");
        cardPage.fillCardInfo(DataHelper.getInvalidCardMonth());
        cardPage.clickButton("Продолжить");
        cardPage.shouldHighlightInvalidFields(
                Map.of("Месяц", "Неверно указан срок действия карты"));
    }

    @DisplayName("Ошибка при вводе прошедшего года")
    @Test
    void shouldRejectInvalidCardYear() {
        cardPage.clickButton("Купить");
        cardPage.fillCardInfo(DataHelper.getInvalidCardYear());
        cardPage.clickButton("Продолжить");
        cardPage.shouldHighlightInvalidFields(
                Map.of("Год", "Истёк срок действия карты"));
    }



    @DisplayName("Отклонение покупки по недействительной карте")
    @Test
    void shouldDeclineTourWithDeclinedCard() {
        cardPage.clickButton("Купить");
        cardPage.fillCardInfo(DataHelper.getDeclinedCard());
        cardPage.clickButton("Продолжить");
        cardPage.shouldShowTextNotification("Ошибка! Банк отказал в проведении операции");
    }

    @DisplayName("Успешная покупка тура по карте")
    @Test
    void shouldBuyTourWithApprovedCard() {
        cardPage.clickButton("Купить");
        cardPage.fillCardInfo(DataHelper.getApprovedCard());
        cardPage.clickButton("Продолжить");
        cardPage.shouldShowTextNotification("Операция одобрена Банком");
    }


    @DisplayName("Покупка в кредит с одобренной картой")
    @Test
    void shouldSuccessfullyCreditWithApprovedCard() {
        cardPage.clickButton("Купить в кредит");
        cardPage.fillCardInfo(DataHelper.getApprovedCard());
        cardPage.clickButton("Продолжить");
        cardPage.shouldShowTextNotification("Операция одобрена Банком");
    }

    @AfterEach
    public void tearDown() throws IOException {
        File screenshot = Screenshots.takeScreenShotAsFile();
        if (screenshot != null && screenshot.exists()) {
            try (FileInputStream in = new FileInputStream(screenshot)) {
                Allure.addAttachment("Screenshot", in);
            }
        }
    }
}
