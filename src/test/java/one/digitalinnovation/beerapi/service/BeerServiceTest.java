package one.digitalinnovation.beerapi.service;

import one.digitalinnovation.beerapi.builder.BeerDTOBuilder;
import one.digitalinnovation.beerapi.dto.BeerDTO;
import one.digitalinnovation.beerapi.entity.Beer;
import one.digitalinnovation.beerapi.exception.BeerAlreadyRegisteredException;
import one.digitalinnovation.beerapi.mapper.BeerMapper;
import one.digitalinnovation.beerapi.repository.BeerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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

        assertThat(createdBeerDTO.getId(), is(equalTo(informedBeerDTO.getId())));
        assertThat(createdBeerDTO.getName(), is(equalTo(informedBeerDTO.getName())));
    }

    @Test
    void whenAlreadyRegistredBeerInformedThrowException(){
        //given
        BeerDTO informedBeerDTO = BeerDTOBuilder.builder().build().toBeerDTO();
        Beer duplicatedBeer = beerMapper.toModel(informedBeerDTO);

        //when
        when(beerRepository.findBeerByName(informedBeerDTO.getName())).thenReturn(Optional.of(duplicatedBeer));

        //then
        assertThrows(BeerAlreadyRegisteredException.class, () -> beerService.createBeer(informedBeerDTO));
    }
}
