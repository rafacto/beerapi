package one.digitalinnovation.beerapi.controller;

import one.digitalinnovation.beerapi.builder.BeerDTOBuilder;
import one.digitalinnovation.beerapi.dto.BeerDTO;
import one.digitalinnovation.beerapi.exception.BeerAlreadyRegisteredException;
import one.digitalinnovation.beerapi.service.BeerService;
import one.digitalinnovation.beerapi.utils.JsonConverterUtils;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

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
    void whenPOSTCalledThenBeerIsCreated() throws Exception {
        //given
        BeerDTO beerToBeSaved = BeerDTOBuilder.builder().build().toBeerDTO();

        //when
        when(beerService.createBeer(beerToBeSaved)).thenReturn(beerToBeSaved);

        //then
        mockMvc.perform(post(BEER_API_URL_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonConverterUtils.DTOtoJsonString(beerToBeSaved)))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name", Matchers.is(beerToBeSaved.getName())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.brand", Matchers.is(beerToBeSaved.getBrand())));
    }

    @Test
    void whenPOSTCalledWithoutRequiredFieldThenBadRequestIsReturned() throws Exception {
        // given
        BeerDTO beerToBeSaved = BeerDTOBuilder.builder().build().toBeerDTO();
        beerToBeSaved.setBrand(null);

        // then
        mockMvc.perform(post(BEER_API_URL_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonConverterUtils.DTOtoJsonString(beerToBeSaved)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }
}
