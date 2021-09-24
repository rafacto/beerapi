package one.digitalinnovation.beerapi.service;

import lombok.AllArgsConstructor;
import one.digitalinnovation.beerapi.dto.BeerDTO;
import one.digitalinnovation.beerapi.entity.Beer;
import one.digitalinnovation.beerapi.exception.BeerAlreadyRegisteredException;
import one.digitalinnovation.beerapi.exception.BeerInsufficientStockException;
import one.digitalinnovation.beerapi.exception.BeerNotFoundException;
import one.digitalinnovation.beerapi.exception.BeerExceededStockException;
import one.digitalinnovation.beerapi.mapper.BeerMapper;
import one.digitalinnovation.beerapi.repository.BeerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class BeerService {

    private final BeerRepository beerRepository;
    private final BeerMapper beerMapper = BeerMapper.INSTANCE;

    public BeerDTO createBeer(BeerDTO beerDTO) throws BeerAlreadyRegisteredException {
        verifyIfBeerIsAlreadyRegistered(beerDTO.getName());
        Beer beerToBeSaved = beerMapper.toModel(beerDTO);
        Beer savedBeer = beerRepository.save(beerToBeSaved);
        return beerMapper.toDTO(savedBeer);
    }

    public BeerDTO findBeerByName(String name) throws BeerNotFoundException{
        Beer foundBeer = beerRepository.findBeerByName(name).orElseThrow(() -> new BeerNotFoundException(name));
        return beerMapper.toDTO(foundBeer);
    }

    public List<BeerDTO> listAllBeers() {
        List<Beer> foundBeers = beerRepository.findAll();
        return foundBeers.stream().map(beerMapper::toDTO).collect(Collectors.toList());
    }

    public void deleteBeerById(Long id) throws BeerNotFoundException{
        returnBeerIfExist(id);
        beerRepository.deleteById(id);
    }

    public BeerDTO incrementBeer(Long id, int increment) throws BeerNotFoundException, BeerExceededStockException {

        Beer beerToBeIncremented = returnBeerIfExist(id);
        int quantityAfterIncrement = beerToBeIncremented.getQuantity() + increment;

        if(quantityAfterIncrement <= beerToBeIncremented.getMax()){
            beerToBeIncremented.setQuantity(quantityAfterIncrement);
            return beerMapper.toDTO(beerRepository.save(beerToBeIncremented));
        }

        throw new BeerExceededStockException(beerToBeIncremented, increment);
    }

    private void verifyIfBeerIsAlreadyRegistered(String beerName) throws BeerAlreadyRegisteredException {
        Optional<Beer> savedBeer = beerRepository.findBeerByName(beerName);
        if(savedBeer.isPresent()){
            throw new BeerAlreadyRegisteredException(beerName);
        }
    }

    private Beer returnBeerIfExist(Long id) throws BeerNotFoundException {
        return beerRepository.findById(id).orElseThrow(() -> new BeerNotFoundException(id));
    }


    public BeerDTO decrementBeer(Long id, int decrement) throws BeerNotFoundException, BeerInsufficientStockException {
        Beer beerToBeDecremented = returnBeerIfExist(id);
        int quantityAfterIncrement = beerToBeDecremented.getQuantity() - decrement;

        if(quantityAfterIncrement >= 0){
            beerToBeDecremented.setQuantity(quantityAfterIncrement);
            return beerMapper.toDTO(beerRepository.save(beerToBeDecremented));
        }

        throw new BeerInsufficientStockException(beerToBeDecremented, decrement);
    }
}
