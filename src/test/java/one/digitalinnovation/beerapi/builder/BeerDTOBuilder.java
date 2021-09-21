package one.digitalinnovation.beerapi.builder;

import lombok.Builder;
import one.digitalinnovation.beerapi.dto.BeerDTO;
import one.digitalinnovation.beerapi.enums.BeerType;

@Builder
public class BeerDTOBuilder {

    @Builder.Default
    private Long id = 1L;

    @Builder.Default
    private String name = "Faxe Witbier";

    @Builder.Default
    private String brand = "Faxe Brewery Denmark";

    @Builder.Default
    private int max = 100;

    @Builder.Default
    private int quantity = 30;

    @Builder.Default
    private BeerType type = BeerType.WITBIER;

    public BeerDTO toBeerDTO() {
        return new BeerDTO(id,
                name,
                brand,
                max,
                quantity,
                type);
    }
}
