package org.golovko.telegramshop.service;

import org.golovko.telegramshop.domain.Customer;
import org.golovko.telegramshop.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;

@Service
public class CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    public void checkUserIfExists(Message message) {
        if (!customerRepository.existsByChatId(message.getChatId())) {
            createCustomer(message.getFrom());
        };
    }

    private void createCustomer(User user) {
        Customer customer = new Customer();
        customer.setName(user.getFirstName());
        customer.setSurname(user.getLastName());
        customer.setUsername(user.getUserName());
        customer.setChatId(user.getId().longValue());
        customerRepository.save(customer);
    }

    public Customer getByChatId(long chatId) {
        return customerRepository.findByChatId(chatId);
    }

    public void saveCustomer(Customer customer) {
        customerRepository.save(customer);
    }
}
