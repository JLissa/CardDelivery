package ru.netology;

import com.codeborne.selenide.Condition;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selectors.*;
import static com.codeborne.selenide.Selenide.*;

public class CardDeliveryTest {
    private int random_num = (int) (Math.random() * 15);
    LocalDate date = LocalDate.now();
    LocalDate bookingDate = date.plusDays(3 + random_num);
    private DateTimeFormatter formatDate = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    public String generateDate(int days) {
        return LocalDate.now().plusDays(days).format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
    }

    @Test
    void shouldMakeBookingAndReturnSuccessMessage(){
        open("http://localhost:9999");
        $("[data-test-id='city'] input").setValue("Москва");
        //*span
        $("[data-test-id=date] input").sendKeys(Keys.chord(Keys.SHIFT, Keys.HOME), Keys.BACK_SPACE);
        $("[data-test-id='date'] input").sendKeys(formatDate.format(bookingDate));
        $("[data-test-id='name'] input").setValue("Федор Достоевкий");
        $("[data-test-id='phone'] input").setValue("+79052447564");
        $("[data-test-id='agreement'] .checkbox__box").click();
        $(".button").click();
        $(".notification__content")
                .shouldHave(Condition.text("Встреча успешно забронирована на " + formatDate.format(bookingDate)), Duration.ofSeconds(20))
                .shouldBe(Condition.visible);

    }

    @Test
    void shouldSendFormWithValidData() {
        open("http://localhost:9999");
        String planningDate = generateDate(5);
        $("[data-test-id=city] input").setValue("Москва");
        $("[data-test-id=date] input").sendKeys(Keys.chord(Keys.SHIFT, Keys.HOME), Keys.BACK_SPACE);
        $("[data-test-id='date'] input").setValue(planningDate);
        $("[data-test-id=name] input").setValue("Иванов Иван");
        $("[data-test-id=phone] input").setValue("+79999999999");
        $("[data-test-id=agreement]").click();
        $(".button").click();
        $(".notification__content")
                .shouldHave(Condition.text("Встреча успешно забронирована на " + planningDate), Duration.ofSeconds(15))
                .shouldBe(Condition.visible);
    }

    @Test
    void shouldReturnAlertMessageIfWrongCity(){
        open("http://localhost:9999");
        $("[data-test-id='city'] input").setValue("Лондон");
        $("[data-test-id='date'] input").doubleClick().sendKeys(Keys.BACK_SPACE, formatDate.format(bookingDate));
        $("[data-test-id='date'] input").sendKeys(formatDate.format(bookingDate));
        $("[data-test-id='name'] input").setValue("Петр Первый");
        $("[data-test-id='phone'] input").setValue("+79083687564");
        $("[data-test-id='agreement'] .checkbox__box").click();
        $(".button").click();
        $("[data-test-id='city'] .input__sub").shouldHave(exactText("Доставка в выбранный город недоступна"));
    }

    @Test
    void shouldReturnAlertMessageIfDateIsBeforeThreeDays(){
        open("http://localhost:9999");
        $("[data-test-id='city'] input").setValue("Пермь");
        $("[data-test-id='date'] input").doubleClick().sendKeys(Keys.BACK_SPACE);
        $("[data-test-id='date'] input").sendKeys("22.04.2023");
        $("[data-test-id='name'] input").setValue("Иван Грозный");
        $("[data-test-id='phone'] input").setValue("+78766487564");
        $("[data-test-id='agreement'] .checkbox__box").click();
        $(".button").click();
        $(" [data-test-id='date'] .input__sub").shouldHave(exactText("Заказ на выбранную дату невозможен"));
    }

    @Test
    void shouldReturnAlertMessageIfNameInEnglish(){
        open("http://localhost:9999");
        $("[data-test-id='city'] input").setValue("Пермь");
        $("[data-test-id='date'] input").doubleClick().sendKeys(Keys.BACK_SPACE);
        $("[data-test-id='date'] input").sendKeys(formatDate.format(bookingDate));
        $("[data-test-id='name'] input").setValue("Ivan");
        $("[data-test-id='phone'] input").setValue("+79056487564");
        $("[data-test-id='agreement'] .checkbox__box").click();
        $(".button").click();
        $(" [data-test-id='name'] .input__sub").shouldHave(exactText("Имя и Фамилия " +
                "указаные неверно. Допустимы только русские буквы, пробелы и дефисы."));
    }

    @Test
    void shouldReturnAlertMessageIfNumberIsWrong(){
        open("http://localhost:9999");
        $("[data-test-id='city'] input").setValue("Пермь");
        $("[data-test-id='date'] input").doubleClick().sendKeys(Keys.BACK_SPACE);
        $("[data-test-id='date'] input").sendKeys(formatDate.format(bookingDate));
        $("[data-test-id='name'] input").setValue("Владислав Ващенко");
        $("[data-test-id='phone'] input").setValue("+79056487564756");
        $("[data-test-id='agreement'] .checkbox__box").click();
        $(".button").click();
        $(" [data-test-id='phone'] .input__sub").shouldHave(exactText("Телефон указан неверно. " +
                "Должно быть 11 цифр, например, +79012345678."));
    }

    @Test
    void shouldSearchFromCitiesWithTwoChars(){
        open("http://localhost:9999");
        $("[data-test-id='city'] input").sendKeys("ко");
        $$(" .menu-item").find(exactText("Кострома")).click();
        $(".input__icon").click();
        $("[data-test-id='date'] input").doubleClick().sendKeys(Keys.BACK_SPACE);
        $("[data-test-id='date'] input").sendKeys(formatDate.format(bookingDate));
        $("[data-test-id='name'] input").setValue("Кирилл Киров");
        $("[data-test-id='phone'] input").setValue("+79056487564");
        $("[data-test-id='agreement'] .checkbox__box").click();
        $(".button").click();
        $(".notification__content")
                .shouldHave(Condition.text("Встреча успешно забронирована на " + formatDate.format(bookingDate)), Duration.ofSeconds(20))
                .shouldBe(Condition.visible);
    }

    @Test
    void shouldChangeColorOfCheckboxMessageIfNotChecked() {
        open("http://localhost:9999");
        $("[data-test-id='city'] input").setValue("Абакан");
        $("[data-test-id='date'] input").doubleClick().sendKeys(Keys.BACK_SPACE);
        $("[data-test-id='date'] input").sendKeys(formatDate.format(bookingDate));
        $("[data-test-id='name'] input").setValue("Иван Грозный");
        $("[data-test-id='phone'] input").setValue("+79056487564");
        //$(".checkbox__box").click();
        $(".button").click();
        $(".input_invalid").shouldHave(exactText("Я соглашаюсь с условиями обработки " +
                "и использования моих персональных данных"));
    }
}