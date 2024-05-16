package com.awbd.subscription.controller;

import com.awbd.subscription.model.Subscription;
import com.awbd.subscription.services.SubscriptionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
public class SubscriptionController {
    @Autowired
    SubscriptionService subscriptionService;

    @GetMapping("/subscription/list")
    public List<Subscription> findAll(){

        return subscriptionService.findAll();
    }

    @GetMapping("/subscription/coach/{coach}/sport/{sport}")
    public Subscription findByCoachAndSport(@PathVariable String coach,
                                     @PathVariable String sport){
        Subscription subscription = subscriptionService.findByCoachAndSport(coach, sport);
        return subscription;
    }


    @PostMapping("/subscription")
    public ResponseEntity<Subscription> saveSubscription(@Valid @RequestBody Subscription subscription){
        Subscription savedSubscription = subscriptionService.save(subscription);
        URI locationUri =ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{subscriptionId}").buildAndExpand(savedSubscription.getId())
                .toUri();

        return ResponseEntity.created(locationUri).body(savedSubscription);
    }

    @PutMapping("/subscription")
    public ResponseEntity<Subscription> updateSubscription(@Valid @RequestBody Subscription subscription){
        Subscription updatedSubscription = subscriptionService.save(subscription);
        return ResponseEntity.ok(updatedSubscription);
    }
.
    @DeleteMapping("/subscription/{subscriptionId}")
    public ResponseEntity<Void> deleteSubscription(@PathVariable Long subscriptionId){

        boolean deleted = subscriptionService.delete(subscriptionId);

        if (deleted) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }


    @GetMapping("/subscription/{subscriptionId}")
    public Subscription getSubscription(@PathVariable Long subscriptionId) {

        Subscription subscription = subscriptionService.findById(subscriptionId);
        return subscription;

    }
}
