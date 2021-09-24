package one.digitalinnovation.beerapi.exception;

import one.digitalinnovation.beerapi.entity.Beer;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class BeerExceededStockException extends Exception {

    public BeerExceededStockException(Beer beer, int increment) {
        super(String.format("Beer %s can't be incremented by %s due to its stock capacity (%s)", beer.getName(), increment, beer.getMax()));
    }
}
