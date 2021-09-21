package one.digitalinnovation.beerapi.service;

import one.digitalinnovation.beerapi.builder.BeerDTOBuilder;
import one.digitalinnovation.beerapi.dto.BeerDTO;
import one.digitalinnovation.beerapi.entity.Beer;
import one.digitalinnovation.beerapi.exception.BeerAlreadyRegisteredException;
import one.digitalinnovation.beerapi.mapper.BeerMapper;
import one.digitalinnovation.beerapi.repository.BeerRepository;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

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
        Mockito.when(beerRepository.findBeerByName(informedBeerDTO.getName())).thenReturn(Optional.empty());
        Mockito.when(beerRepository.save(beerToBeCreated)).thenReturn(beerToBeCreated);

        //then
        BeerDTO createdBeerDTO = beerService.createBeer(informedBeerDTO);

        assertThat(createdBeerDTO.getId(), is(equalTo(informedBeerDTO.getId())));
        assertThat(createdBeerDTO.getName(), is(equalTo(informedBeerDTO.getName())));
    }
}
