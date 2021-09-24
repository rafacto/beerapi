package one.digitalinnovation.beerapi.exception;

import one.digitalinnovation.beerapi.entity.Beer;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class BeerInsufficientStockException extends Exception{

    public BeerInsufficientStockException(Beer beer, int decrement) {
        super(String.format("Beer %s can't be decremented by %s due to its stock capacity (%s)", beer.getName(), decrement, beer.getQuantity()));
    }
}
