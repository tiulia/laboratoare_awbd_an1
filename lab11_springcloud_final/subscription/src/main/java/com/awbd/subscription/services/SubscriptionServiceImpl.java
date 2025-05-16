package com.awbd.subscription.services;

import com.awbd.subscription.exceptions.SubscriptionNotFound;
import com.awbd.subscription.model.Subscription;
import com.awbd.subscription.repositories.SubscriptionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@Service
public class SubscriptionServiceImpl implements SubscriptionService {

    @Autowired
    SubscriptionRepository subscriptionRepository;

    @Override
    public Subscription findByCoachAndSport(String coach, String sport) {
        return subscriptionRepository.findByCoachAndSport(coach,sport);
    }

    @Override
    public Subscription save(Subscription subscription) {
        return subscriptionRepository.save(subscription);

    }

    @Override
    public List<Subscription> findAll(){
        List<Subscription> subscriptions = new LinkedList<>();
        subscriptionRepository.findAll().iterator().forEachRemaining(subscriptions::add);
        return subscriptions;
    }

    @Override
    public boolean delete(Long id){
        Optional<Subscription> subscriptionOptional = subscriptionRepository.findById(id);
        if (! subscriptionOptional.isPresent())
            throw new SubscriptionNotFound("Subscription " + id + " not found!");
        subscriptionRepository.delete(subscriptionOptional.get());
        return true;
    }

    @Override
    public Subscription findById(Long id) {
        Optional<Subscription> subscriptionOptional = subscriptionRepository.findById(id);
        if (! subscriptionOptional.isPresent())
            throw new SubscriptionNotFound("Subscription " + id + " not found!");
        return subscriptionOptional.get();
    }


}
