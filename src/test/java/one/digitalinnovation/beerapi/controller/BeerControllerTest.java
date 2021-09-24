package one.digitalinnovation.beerapi.controller;

import one.digitalinnovation.beerapi.builder.BeerDTOBuilder;
import one.digitalinnovation.beerapi.dto.BeerDTO;
import one.digitalinnovation.beerapi.dto.QuantityDTO;
import one.digitalinnovation.beerapi.exception.BeerInsufficientStockException;
import one.digitalinnovation.beerapi.exception.BeerNotFoundException;
import one.digitalinnovation.beerapi.exception.BeerExceededStockException;
import one.digitalinnovation.beerapi.service.BeerService;
import one.digitalinnovation.beerapi.utils.JsonConverterUtils;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import java.util.Collections;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class BeerControllerTest {

    private static final String BEER_API_URL_PATH = "/api/v1/beer";

    @Mock
    private BeerService beerService;

    @InjectMocks
    private BeerController beerController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(beerController)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .setViewResolvers((s, locale) -> new MappingJackson2JsonView())
                .build();
    }

    @Test
    void whenPOSTIsCalledThenBeerIsCreated() throws Exception {
        //given
        BeerDTO beerToBeSaved = BeerDTOBuilder.builder().build().toBeerDTO();

        //when
        when(beerService.createBeer(beerToBeSaved)).thenReturn(beerToBeSaved);

        //then
        mockMvc.perform(post(BEER_API_URL_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonConverterUtils.DTOtoJsonString(beerToBeSaved)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", Matchers.is(beerToBeSaved.getName())))
                .andExpect(jsonPath("$.brand", Matchers.is(beerToBeSaved.getBrand())));
    }

    @Test
    void whenPOSTIsCalledWithoutRequiredFieldThenBadRequestIsReturned() throws Exception {
        // given
        BeerDTO beerToBeSaved = BeerDTOBuilder.builder().build().toBeerDTO();
        beerToBeSaved.setBrand(null);

        // then
        mockMvc.perform(post(BEER_API_URL_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonConverterUtils.DTOtoJsonString(beerToBeSaved)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void whenGETByValidNameIsCalledThenOkStatusIsReturned() throws Exception {
        // given
        BeerDTO informedBeerDTO = BeerDTOBuilder.builder().build().toBeerDTO();

        // when
        when(beerService.findBeerByName(informedBeerDTO.getName())).thenReturn(informedBeerDTO);

        // then
        mockMvc.perform(get(BEER_API_URL_PATH + "/" + informedBeerDTO.getName()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", Matchers.is(informedBeerDTO.getName())))
                .andExpect(jsonPath("$.brand", Matchers.is(informedBeerDTO.getBrand())));
    }

    @Test
    void whenGETByNonexistentNameIsCalledThenNotFoundIsReturned() throws Exception {
        // given
        BeerDTO informedBeerDTO = BeerDTOBuilder.builder().build().toBeerDTO();

        // when
        when(beerService.findBeerByName(informedBeerDTO.getName())).thenThrow(BeerNotFoundException.class);

        // then
        mockMvc.perform(get(BEER_API_URL_PATH + "/" + informedBeerDTO.getName()))
                .andExpect(status().isNotFound());
    }

    @Test
    void whenGETListBeersIsCalledThenOkStatusIsReturned() throws Exception {
        // given
        BeerDTO informedBeerDTO = BeerDTOBuilder.builder().build().toBeerDTO();

        // when
        when(beerService.listAllBeers()).thenReturn(Collections.singletonList(informedBeerDTO));

        // then
        mockMvc.perform(get(BEER_API_URL_PATH))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name", Matchers.is(informedBeerDTO.getName())))
                .andExpect(jsonPath("$[0].brand", Matchers.is(informedBeerDTO.getBrand())));
    }

    @Test
    void whenDELETEBeerWithExistentIdIsCalledThenNoContentIsReturned() throws Exception {
        // given
        Long beerId = BeerDTOBuilder.builder().build().toBeerDTO().getId();

        // when
        doNothing().when(beerService).deleteBeerById(beerId);

        // then
        mockMvc.perform(delete(BEER_API_URL_PATH + "/" + beerId))
                .andExpect(status().isNoContent());
    }

    @Test
    void whenDELETEBeerWithNonexistentIdIsCalledThenNotFoundIsReturned() throws Exception {
        //given
        Long nonExistentId = 1L;

        // when
        doThrow(BeerNotFoundException.class).when(beerService).deleteBeerById(nonExistentId);

        // then
        mockMvc.perform(delete(BEER_API_URL_PATH + "/" + nonExistentId))
                .andExpect(status().isNotFound());
    }

    @Test
    void whenPATCHIncrementQuantityExistentBeerThenOkStatusIsReturned() throws Exception {
        // given
        BeerDTO beerToBeIncremented = BeerDTOBuilder.builder().build().toBeerDTO();
        QuantityDTO increment = QuantityDTO.builder().quantity(10).build();

        // when
        beerToBeIncremented.setQuantity(beerToBeIncremented.getQuantity() + increment.getQuantity());
        when(beerService.incrementBeer(beerToBeIncremented.getId(), increment.getQuantity())).thenReturn(beerToBeIncremented);

        // then
        mockMvc.perform(patch(BEER_API_URL_PATH + "/" + beerToBeIncremented.getId() + "/increment")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonConverterUtils.DTOtoJsonString(increment)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", Matchers.is(beerToBeIncremented.getName())))
                .andExpect(jsonPath("$.brand", Matchers.is(beerToBeIncremented.getBrand())))
                .andExpect(jsonPath("$.quantity", Matchers.is(beerToBeIncremented.getQuantity())));
    }

    @Test
    void whenPATCHIncrementQuantityGreaterThenMaxThenBadRequestIsReturned() throws Exception {
        // given
        BeerDTO beerToBeIncremented = BeerDTOBuilder.builder().build().toBeerDTO();
        QuantityDTO increment = QuantityDTO.builder().quantity(10).build();

        // when
        beerToBeIncremented.setQuantity(beerToBeIncremented.getQuantity() + increment.getQuantity());
        when(beerService.incrementBeer(beerToBeIncremented.getId(), increment.getQuantity())).thenThrow(BeerExceededStockException.class);

        // then
        mockMvc.perform(patch(BEER_API_URL_PATH + "/" + beerToBeIncremented.getId() + "/increment")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonConverterUtils.DTOtoJsonString(increment)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void whenPATCHIncrementQuantityNonexistentBeerThenNotFoundIsReturned() throws Exception {
        // given
        Long beerToBeIncrementedId = BeerDTOBuilder.builder().build().toBeerDTO().getId();
        QuantityDTO increment = QuantityDTO.builder().quantity(10).build();

        // when
        when(beerService.incrementBeer(beerToBeIncrementedId, increment.getQuantity())).thenThrow(BeerNotFoundException.class);

        // then
        mockMvc.perform(patch(BEER_API_URL_PATH + "/" + beerToBeIncrementedId + "/increment")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonConverterUtils.DTOtoJsonString(increment)))
                .andExpect(status().isNotFound());
    }

    @Test
    void whenPATCHDecrementQuantityExistentBeerThenOkStatusIsReturned() throws Exception {
        // given
        BeerDTO beerToBeDecremented = BeerDTOBuilder.builder().build().toBeerDTO();
        QuantityDTO decrement = QuantityDTO.builder().quantity(10).build();

        // when
        beerToBeDecremented.setQuantity(beerToBeDecremented.getQuantity() - decrement.getQuantity());
        when(beerService.decrementBeer(beerToBeDecremented.getId(), decrement.getQuantity())).thenReturn(beerToBeDecremented);

        // then
        mockMvc.perform(patch(BEER_API_URL_PATH + "/" + beerToBeDecremented.getId() + "/decrement")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonConverterUtils.DTOtoJsonString(decrement)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", Matchers.is(beerToBeDecremented.getName())))
                .andExpect(jsonPath("$.brand", Matchers.is(beerToBeDecremented.getBrand())))
                .andExpect(jsonPath("$.quantity", Matchers.is(beerToBeDecremented.getQuantity())));
    }

    @Test
    void whenPATCHDecrementQuantityGreaterThenStockThenBadRequestIsReturned() throws Exception {
        // given
        BeerDTO beerToBeDecremented = BeerDTOBuilder.builder().build().toBeerDTO();
        QuantityDTO decrement = QuantityDTO.builder().quantity(31).build();

        // when
        beerToBeDecremented.setQuantity(beerToBeDecremented.getQuantity() - decrement.getQuantity());
        when(beerService.decrementBeer(beerToBeDecremented.getId(), decrement.getQuantity())).thenThrow(BeerInsufficientStockException.class);

        // then
        mockMvc.perform(patch(BEER_API_URL_PATH + "/" + beerToBeDecremented.getId() + "/decrement")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonConverterUtils.DTOtoJsonString(decrement)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void whenPATCHDecrementQuantityNonexistentBeerThenNotFoundIsReturned() throws Exception {
        // given
        Long beerToBeDecrementedId = BeerDTOBuilder.builder().build().toBeerDTO().getId();
        QuantityDTO decrement = QuantityDTO.builder().quantity(10).build();

        // when
        when(beerService.decrementBeer(beerToBeDecrementedId, decrement.getQuantity())).thenThrow(BeerNotFoundException.class);

        // then
        mockMvc.perform(patch(BEER_API_URL_PATH + "/" + beerToBeDecrementedId + "/decrement")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonConverterUtils.DTOtoJsonString(decrement)))
                .andExpect(status().isNotFound());
    }
}
