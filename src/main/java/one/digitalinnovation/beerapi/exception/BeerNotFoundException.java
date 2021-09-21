package one.digitalinnovation.beerapi.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class BeerNotFoundException extends Exception {

    public BeerNotFoundException(String name) {
        super(String.format("Beer of name %s does not exist", name));
    }

    public BeerNotFoundException(Long id) {
        super(String.format("Beer of id %d does not exist", id));
    }
}
