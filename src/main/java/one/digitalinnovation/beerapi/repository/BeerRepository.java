package one.digitalinnovation.beerapi.repository;

import one.digitalinnovation.beerapi.entity.Beer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BeerRepository extends JpaRepository<Beer, Long> {

    Optional<Beer> findBeerByName(String name);

}
