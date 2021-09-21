package one.digitalinnovation.beerapi.mapper;

import one.digitalinnovation.beerapi.dto.BeerDTO;
import one.digitalinnovation.beerapi.entity.Beer;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface BeerMapper {

    BeerMapper INSTANCE = Mappers.getMapper(BeerMapper.class);

    Beer toModel(BeerDTO beerDTO);

    BeerDTO toDTO(Beer beer);
}
