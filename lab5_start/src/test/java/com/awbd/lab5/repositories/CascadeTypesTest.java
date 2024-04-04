package com.awbd.lab5.repositories;

import com.awbd.lab5.domain.Currency;
import com.awbd.lab5.domain.Info;
import com.awbd.lab5.domain.Participant;
import com.awbd.lab5.domain.Product;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("mysql")
public class CascadeTypesTest {

    @Autowired
    ProductRepository productRepository;

    @Autowired
    ParticipantRepository participantRepository;

    @Test
    public void updateDescription(){
        Optional<Product> productOpt = productRepository.findById(1L);
        Product product = productOpt.get();
        product.getInfo().setDescription("Painting by Paul Cezanne");
        product.setCurrency(Currency.USD);

        productRepository.save(product);

        productOpt = productRepository.findById(1L);
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
        product = productOpt.get();
        assertEquals(Currency.USD, product.getCurrency());
        assertEquals("Painting by Paul Cezanne", product.getInfo().getDescription());

    }


    @Test
    public void updateParticipant(){
        Optional<Product> productOpt = productRepository.findById(2L);

        Participant participant = productOpt.get().getSeller();
        participant.setFirstName("William");

        participant.getProducts().forEach(prod -> {prod.setCurrency(Currency.GBP);});
        Product product = new Product();
        product.setName("The Vase of Tulips");
        product.setCurrency(Currency.GBP);
        participant.getProducts().add(product);

        participantRepository.save(participant);

        Optional<Participant> participantOpt = participantRepository.findById(2L);
        participant = participantOpt.get();
        participant.getProducts().forEach(prod -> {
            assertEquals(Currency.GBP, prod.getCurrency());});

    }


    @Test
    public void deleteParticipant(){
        participantRepository.deleteById(2L);
        Optional<Product> product = productRepository.findById(2L);

        //orphan removal true
        assertTrue(product.isEmpty());
    }

}
