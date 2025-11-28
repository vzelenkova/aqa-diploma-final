package data;

import lombok.Value;

/**
 * Immutable value-object describing card information for tests.
 */
@Value
public class CardInfo {
    String number;
    String month;
    String year;
    String holder;
    String cvc;
}
