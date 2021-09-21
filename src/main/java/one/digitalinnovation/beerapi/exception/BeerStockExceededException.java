package one.digitalinnovation.beerapi.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class BeerStockExceededException extends Exception {

    public BeerStockExceededException(Long id, Integer quantityToIncrement) {
        super(String.format("Beer of ID %s has exceeded the stock capacity: %s", id, quantityToIncrement));
    }
}
