package one.digitalinnovation.beerapi.service;

import one.digitalinnovation.beerapi.builder.BeerDTOBuilder;
import one.digitalinnovation.beerapi.dto.BeerDTO;
import one.digitalinnovation.beerapi.entity.Beer;
import one.digitalinnovation.beerapi.exception.BeerAlreadyRegisteredException;
import one.digitalinnovation.beerapi.exception.BeerNotFoundException;
import one.digitalinnovation.beerapi.exception.BeerStockExceededException;
import one.digitalinnovation.beerapi.mapper.BeerMapper;
import one.digitalinnovation.beerapi.repository.BeerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BeerServiceTest {

    @Mock
    private BeerRepository beerRepository;
    private BeerMapper beerMapper = BeerMapper.INSTANCE;

    @InjectMocks
    private BeerService beerService;

    @Test
    void whenBeerInformedThenItIsCreated() throws BeerAlreadyRegisteredException {
        //given
        BeerDTO informedBeerDTO = BeerDTOBuilder.builder().build().toBeerDTO();
        Beer beerToBeCreated = beerMapper.toModel(informedBeerDTO);

        //when
        when(beerRepository.findBeerByName(informedBeerDTO.getName())).thenReturn(Optional.empty());
        when(beerRepository.save(beerToBeCreated)).thenReturn(beerToBeCreated);

        //then
        BeerDTO createdBeerDTO = beerService.createBeer(informedBeerDTO);
        assertThat(createdBeerDTO, is(equalTo(informedBeerDTO)));
    }

    @Test
    void whenAlreadyRegisteredBeerInformedThenExceptionIsThrown(){
        //given
        BeerDTO informedBeerDTO = BeerDTOBuilder.builder().build().toBeerDTO();
        Beer duplicatedBeer = beerMapper.toModel(informedBeerDTO);

        //when
        when(beerRepository.findBeerByName(informedBeerDTO.getName())).thenReturn(Optional.of(duplicatedBeer));

        //then
        assertThrows(BeerAlreadyRegisteredException.class, () -> beerService.createBeer(informedBeerDTO));
    }

    @Test
    void whenExistentBeerNameInformedThenReturnFoundedBeer() throws BeerNotFoundException {
        //given
        BeerDTO informedBeerDTO = BeerDTOBuilder.builder().build().toBeerDTO();
        Beer informedBeer = beerMapper.toModel(informedBeerDTO);

        //when
        when(beerRepository.findBeerByName(informedBeerDTO.getName())).thenReturn(Optional.of(informedBeer));

        //then
        BeerDTO returnedBeerDTO = beerService.findBeerByName(informedBeerDTO.getName());
        assertThat(returnedBeerDTO, is(equalTo(informedBeerDTO)));
    }

    @Test
    void whenNonexistentBeerNameInformedThenExceptionIsThrown(){
        //given
        String informedBeerName = BeerDTOBuilder.builder().build().toBeerDTO().getName();

        //when
        when(beerRepository.findBeerByName(informedBeerName)).thenReturn(Optional.empty());

        //then
        assertThrows(BeerNotFoundException.class, () -> beerService.findBeerByName(informedBeerName));

    }

    @Test
    void whenListBeersIsRequiredThenAListOfBeersIsReturned(){
        //given
        BeerDTO expectedBeerDTO = BeerDTOBuilder.builder().build().toBeerDTO();
        Beer expectedBeer = beerMapper.toModel(expectedBeerDTO);

        //when
        when(beerRepository.findAll()).thenReturn(Collections.singletonList(expectedBeer));

        //then
        List<BeerDTO> returnedListBeerDTO = beerService.listAllBeers();
        assertThat(returnedListBeerDTO, is(equalTo(Collections.singletonList(expectedBeerDTO))));
    }

    @Test
    void whenListBeersIsRequiredAndThereIsNotBeerSavedThenAnEmptyListIsReturned(){
        //when
        when(beerRepository.findAll()).thenReturn(Collections.EMPTY_LIST);

        //then
        List<BeerDTO> returnedListBeerDTO = beerService.listAllBeers();
        assertThat(returnedListBeerDTO, is(empty()));
    }

    @Test
    void whenDeleteBeerWithExistentIdIsCalledThenBeerIsDeleted() throws BeerNotFoundException {
        //given
        BeerDTO expectedDeletedBeerDTO = BeerDTOBuilder.builder().build().toBeerDTO();
        Beer expectedDeletedBeer = beerMapper.toModel(expectedDeletedBeerDTO);

        //when
        when(beerRepository.findById(expectedDeletedBeerDTO.getId())).thenReturn(Optional.of(expectedDeletedBeer));
        doNothing().when(beerRepository).deleteById(expectedDeletedBeerDTO.getId());

        //then
        beerService.deleteBeerById(expectedDeletedBeerDTO.getId());
        verify(beerRepository, times(1)).findById(expectedDeletedBeerDTO.getId());
        verify(beerRepository, times(1)).deleteById(expectedDeletedBeerDTO.getId());
    }

    @Test
    void whenDeleteBeerWithNonexistentIdIsCalledThenExceptionIsThrown() throws BeerNotFoundException {
        //given
        Long nonExistentId = 1L;

        //when
        when(beerRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        //then
        assertThrows(BeerNotFoundException.class, () -> beerService.deleteBeerById(nonExistentId));
    }

    @Test
    void whenIncrementExistentBeerThenIncrementItsQuantity() throws BeerNotFoundException, BeerStockExceededException {
        //given
        BeerDTO informedBeerDTO = BeerDTOBuilder.builder().build().toBeerDTO();
        Beer beerToBeIncremented = beerMapper.toModel(informedBeerDTO);

        //when
        when(beerRepository.findById(informedBeerDTO.getId())).thenReturn(Optional.of(beerToBeIncremented));
        when(beerRepository.save(beerToBeIncremented)).thenReturn(beerToBeIncremented);

        //then
        int increment = 10;
        int quantityAfterIncrement = beerToBeIncremented.getQuantity() + increment;

        BeerDTO incrementedBeerDTO = beerService.incrementBeer(informedBeerDTO.getId(), increment);
        assertThat(incrementedBeerDTO.getQuantity(), is(equalTo(quantityAfterIncrement)));
        assertThat(incrementedBeerDTO.getQuantity(), is(lessThanOrEqualTo(incrementedBeerDTO.getMax())));
    }

    @Test
    void whenIncrementExistentBeerExceedingMaxQuantityThenExceptionIsThrown() {
        //given
        BeerDTO informedBeerDTO = BeerDTOBuilder.builder().build().toBeerDTO();
        Beer beerToBeIncremented = beerMapper.toModel(informedBeerDTO);
        int increment = 71;

        //when
        when(beerRepository.findById(informedBeerDTO.getId())).thenReturn(Optional.of(beerToBeIncremented));

        //then
        assertThrows(BeerStockExceededException.class, () -> beerService.incrementBeer(informedBeerDTO.getId(), increment));
    }

    @Test
    void whenIncrementNonexistentBeerThenExceptionIsThrown() throws BeerNotFoundException, BeerStockExceededException {
        //given
        BeerDTO informedBeerDTO = BeerDTOBuilder.builder().build().toBeerDTO();
        int increment = 10;

        //when
        when(beerRepository.findById(informedBeerDTO.getId())).thenReturn(Optional.empty());

        //then
        assertThrows(BeerNotFoundException.class, () -> beerService.incrementBeer(informedBeerDTO.getId(), increment));
    }

}
