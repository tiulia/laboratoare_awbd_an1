package com.awbd.lab4.repositories;

import com.awbd.lab4.domain.Currency;
import com.awbd.lab4.domain.Info;
import com.awbd.lab4.domain.Participant;
import com.awbd.lab4.domain.Product;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("h2")
public class CascadeTypesTest {

    @Autowired
    ProductRepository productRepository;

    @Autowired
    ParticipantRepository participantRepository;

    @Test
    public void updateDescription(){
        Optional<Product> productOpt = productRepository.findById(1L);
        assertTrue(productOpt.isPresent());
        Product product = productOpt.get();
        product.getInfo().setDescription("Painting by Paul Cezanne");
        product.setCurrency(Currency.USD);

        productRepository.save(product);

        productOpt = productRepository.findById(1L);
        assertTrue(productOpt.isPresent());
        product = productOpt.get();
        assertEquals(Currency.USD, product.getCurrency());
        assertEquals("Painting by Paul Cezanne", product.getInfo().getDescription());

    }

    @Test
    public void insertProduct(){
        Product product = new Product();
        product.setName("The Vase of Tulips");
        product.setCurrency(Currency.USD);

        Info info = new Info();
        info.setDescription("Painting by Paul Cezanne");

        product.setInfo(info);

        productRepository.save(product);

        Optional<Product> productOpt = productRepository.findByName("The Vase of Tulips");
        assertTrue(productOpt.isPresent());
        product = productOpt.get();
        assertEquals(Currency.USD, product.getCurrency());
        assertEquals("Painting by Paul Cezanne", product.getInfo().getDescription());

    }


    @Test
    public void updateParticipant(){
        Optional<Product> productOpt = productRepository.findById(2L);
        assertTrue(productOpt.isPresent());
        Participant participant = productOpt.get().getSeller();
        participant.setFirstName("William");

        participant.getProducts().forEach(prod -> prod.setCurrency(Currency.GBP));


        Product product = new Product();
        product.setName("The Vase of Tulips");
        product.setCurrency(Currency.GBP);
        participant.getProducts().add(product);

        participantRepository.save(participant);

        Optional<Participant> participantOpt = participantRepository.findById(2L);
        assertTrue(participantOpt.isPresent());
        participant = participantOpt.get();
        participant.getProducts().forEach(prod ->
            assertEquals(Currency.GBP, prod.getCurrency()));

    }


    @Test
    public void deleteParticipant(){
        participantRepository.deleteById(2L);
        Optional<Product> product = productRepository.findById(2L);

        //orphan removal true
        assertTrue(product.isEmpty());
    }

}
