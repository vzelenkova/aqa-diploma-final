package page;

import com.codeborne.selenide.CollectionCondition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import data.CardInfo;
import io.qameta.allure.*;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.*;
import static com.codeborne.selenide.Condition.text;

/**
 * Page Object for card payment / credit forms.
 */
public class CardPage {

    private SelenideElement getButton(String name) {
        String xpath = String.format("//span[text()='%s']", name);
        return $x(xpath);
    }

    private SelenideElement getFieldByName(String name) {
        String xpath = String.format("//span[text()='%s']/..//input", name);
        return $x(xpath);
    }

    private SelenideElement getErrorForFieldByName(String name) {
        String xpath = String.format("//span[text()='%s']/..//span[@class='input__sub']", name);
        return $x(xpath);
    }

    @Step("Заполнение данных о карте")
    public void fillCardInfo(CardInfo cardInfo) {
        getFieldByName("Номер карты").setValue(cardInfo.getNumber());
        getFieldByName("Месяц").setValue(cardInfo.getMonth());
        getFieldByName("Год").setValue(cardInfo.getYear());
        getFieldByName("Владелец").setValue(cardInfo.getHolder());
        getFieldByName("CVC/CVV").setValue(cardInfo.getCvc());
    }

    @Step("Нажатие кнопки \"{button}\"")
    public void clickButton(String button) {
        getButton(button).click();
    }


    @Step("Проверка наличия в сообщении текста \"{expectedText}\"")
    public void shouldShowTextNotification(String expectedText) {
        ElementsCollection notifications = $$x("//div[contains(@class,'notification notification_visible')]/div[@class='notification__content']");
        notifications.shouldBe(CollectionCondition.sizeGreaterThan(0), Duration.ofSeconds(15)); // Дождётся появления хотя бы одного сообщения

        List<SelenideElement> visibleNotifications = notifications.filter(visible);

        List<String> foundTexts = visibleNotifications.stream()
                .map(SelenideElement::getText)
                .collect(Collectors.toList());

        boolean found = foundTexts.stream().anyMatch(text -> text.contains(expectedText));

        if (!found) {
            String message = "Ожидаемый текст \"" + expectedText + "\" не найден!\n"
                    + "Найденные тексты: " + String.join("\n• ", foundTexts);
            throw new AssertionError(message);
        }
        visibleNotifications.stream()
                .filter(el -> el.getText().contains(expectedText))
                .findFirst()
                .orElseThrow()
                .shouldBe(visible);
    }


    @Step("Проверка ошибки под полями")
    public void shouldHighlightInvalidFields(Map<String, String> expectedErrors) {
        expectedErrors.forEach((field, expectedMessage) ->
                getErrorForFieldByName(field).shouldHave(text(expectedMessage))
        );
    }
}
