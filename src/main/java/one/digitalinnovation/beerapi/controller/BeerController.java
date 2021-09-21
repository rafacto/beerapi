package one.digitalinnovation.beerapi.controller;

import lombok.AllArgsConstructor;
import one.digitalinnovation.beerapi.dto.BeerDTO;
import one.digitalinnovation.beerapi.dto.QuantityDTO;
import one.digitalinnovation.beerapi.exception.BeerAlreadyRegisteredException;
import one.digitalinnovation.beerapi.exception.BeerNotFoundException;
import one.digitalinnovation.beerapi.exception.BeerStockExceededException;
import one.digitalinnovation.beerapi.service.BeerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1/beer")
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class BeerController {

    private final BeerService beerService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BeerDTO createBeer(@RequestBody @Valid BeerDTO beerDTO) throws BeerAlreadyRegisteredException {
        return beerService.createBeer(beerDTO);
    }

    @GetMapping("/{name}")
    public BeerDTO findBeerByName(@PathVariable String name) throws BeerNotFoundException {
        return beerService.findBeerByName(name);
    }

    @GetMapping
    public List<BeerDTO> listAllBeers(){
        return beerService.listAllBeers();
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteBeerById(@PathVariable Long id) throws BeerNotFoundException {
        beerService.deleteBeerById(id);
    }

    @PatchMapping("/{id}/increment")
    public BeerDTO incrementStock(@PathVariable Long id, @RequestBody @Valid QuantityDTO quantityDTO) throws BeerNotFoundException, BeerStockExceededException {
        return beerService.incrementStock(id, quantityDTO.getQuantity());
    }
}
