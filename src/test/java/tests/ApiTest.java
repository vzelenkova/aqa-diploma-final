package tests;

import io.restassured.http.ContentType;
import org.junit.jupiter.api.*;

import static com.codeborne.selenide.Selenide.open;
import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

import db.DbUtils;
import page.CardPage;

import java.sql.SQLException;

@DisplayName("API тесты")
public class ApiTest {

    @BeforeAll
    static void setup() {
        // читаем адрес SUT из системного свойства (по умолчанию localhost:8080)
        baseURI = System.getProperty("sut.url", "http://localhost:8080");
    }

    @Test
    @DisplayName("Оплата тура — одобренная карта")
    void shouldReturnSuccessForApprovedCardPayment() throws SQLException {
        given()
                .contentType(ContentType.JSON)
                .body("{\"number\":\"4444 4444 4444 4441\",\"month\":\"12\",\"year\":\"25\",\"holder\":\"IVAN IVANOV\",\"cvc\":\"123\"}")
                .when()
                .post("/api/v1/pay")
                .then()
                .statusCode(200)
                .body("status", equalTo("APPROVED"));
        String actualStatus = DbUtils.getValueFromDatabase("status", "payment_entity");
        assertEquals("APPROVED", actualStatus);
    }

    @Test
    @DisplayName("Оплата тура — отклонённая карта")
    void shouldReturnErrorForDeclinedCardPayment() throws SQLException {
        given()
                .contentType(ContentType.JSON)
                .body("{\"number\":\"4444 4444 4444 4442\",\"month\":\"12\",\"year\":\"25\",\"holder\":\"IVAN IVANOV\",\"cvc\":\"123\"}")
                .when()
                .post("/api/v1/pay")
                .then()
                .statusCode(200)
                .body("status", equalTo("DECLINED"));
        String actualStatus = DbUtils.getValueFromDatabase("status", "payment_entity");
        assertEquals("DECLINED", actualStatus);
    }

    @Test
    @DisplayName("Кредит — одобренная карта")
    void shouldReturnSuccessForApprovedCardCredit() throws SQLException {
        given()
                .contentType(ContentType.JSON)
                .body("{\"number\":\"4444 4444 4444 4441\",\"month\":\"12\",\"year\":\"25\",\"holder\":\"IVAN IVANOV\",\"cvc\":\"123\"}")
                .when()
                .post("/api/v1/credit")
                .then()
                .statusCode(200)
                .body("status", equalTo("APPROVED"));
        String actualStatus = DbUtils.getValueFromDatabase("status", "credit_request_entity");
        assertEquals("APPROVED", actualStatus);
    }

    @Test
    @DisplayName("Кредит — отклонённая карта")
    void shouldReturnErrorForDeclinedCardCredit() throws SQLException {
        given()
                .contentType(ContentType.JSON)
                .body("{\"number\":\"4444 4444 4444 4442\",\"month\":\"12\",\"year\":\"25\",\"holder\":\"IVAN IVANOV\",\"cvc\":\"123\"}")
                .when()
                .post("/api/v1/credit")
                .then()
                .statusCode(200)
                .body("status", equalTo("DECLINED"));
        String actualStatus = DbUtils.getValueFromDatabase("status", "credit_request_entity");
        assertEquals("DECLINED", actualStatus);
    }

    @Test
    @DisplayName("Оплата — пустое тело запроса ➜ 400")
    void shouldReturnBadRequestForEmptyBody() {
        given()
                .contentType(ContentType.JSON)
                .body("{}")
                .when()
                .post("/api/v1/pay")
                .then()
                .statusCode(500)
                .body("message", equalTo("400 Bad Request"));
    }


    @Test
    @DisplayName("Ошибка при передаче некорректного номера карты")
    void shouldReturnBadRequestForInvalidNumberCard() {
        given()
                .contentType(ContentType.JSON)
                .body("{\"number\":\"0000 0000 0000 0000\",\"month\":\"12\",\"year\":\"25\",\"holder\":\"IVAN IVANOV\",\"cvc\":\"123\"}")
                .when()
                .post("/api/v1/credit")
                .then()
                .statusCode(500)
                .body("message", equalTo("400 Bad Request"));
    }

    @Test
    @DisplayName("Проверка связанности таблиц payment_entity и order_entity")
    void checkTableConnectivityForPay() throws SQLException {
        given()
                .contentType(ContentType.JSON)
                .body("{\"number\":\"4444 4444 4444 4441\",\"month\":\"12\",\"year\":\"25\",\"holder\":\"IVAN IVANOV\",\"cvc\":\"123\"}")
                .when()
                .post("/api/v1/pay")
                .then()
                .statusCode(200)
                .body("status", equalTo("APPROVED"));
        String transaction_id = DbUtils.getValueFromDatabase("transaction_id", "payment_entity");
        String payment_id = DbUtils.getValueFromDatabase("payment_id", "order_entity");
        assertEquals(transaction_id, payment_id);
    }

    @Test
    @DisplayName("Проверка связанности таблиц credit_request_entity и order_entity")
    void checkTableConnectivityForCredit() throws SQLException {
        given()
                .contentType(ContentType.JSON)
                .body("{\"number\":\"4444 4444 4444 4441\",\"month\":\"12\",\"year\":\"25\",\"holder\":\"IVAN IVANOV\",\"cvc\":\"123\"}")
                .when()
                .post("/api/v1/credit")
                .then()
                .statusCode(200)
                .body("status", equalTo("APPROVED"));
        String bank_id = DbUtils.getValueFromDatabase("id", "credit_request_entity");
        String payment_id = DbUtils.getValueFromDatabase("credit_id", "order_entity");
        assertEquals(bank_id, payment_id);
    }

    @AfterEach
    void openPage() throws Exception {
        DbUtils.clearTables();
    }
}