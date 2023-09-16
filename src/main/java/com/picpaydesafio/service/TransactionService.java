package com.picpaydesafio.service;

import com.picpaydesafio.dto.TransactionDTO;
import com.picpaydesafio.entity.transaction.Transaction;
import com.picpaydesafio.entity.user.User;
import com.picpaydesafio.repository.TransactionRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;

@Service
public class TransactionService {

    private final UserService userService;

    private final TransactionRepository transactionRepository;

    private final RestTemplate restTemplate;

    private final NotificationService notificationService;

    public TransactionService(UserService userService, TransactionRepository transactionRepository,
                              RestTemplate restTemplate, NotificationService notificationService) {
        this.userService = userService;
        this.transactionRepository = transactionRepository;
        this.restTemplate = restTemplate;
        this.notificationService = notificationService;
    }

    public Transaction createTransaction(TransactionDTO transactionDTO) throws Exception {
        User sender = this.userService.findUserById(transactionDTO.senderId());
        User receiver = this.userService.findUserById(transactionDTO.receiverId());

        userService.validateTransaction(sender, transactionDTO.value());

        if (isNotAuthorized(sender, transactionDTO.value())) {
            throw new Exception("Transação não autorizada");
        }

        Transaction transaction = new Transaction();
        transaction.setAmount(transactionDTO.value());
        transaction.setSender(sender);
        transaction.setReceiver(receiver);
        transaction.setTimestamp(LocalDateTime.now());

        sender.setBalance(sender.getBalance().subtract(transactionDTO.value()));
        receiver.setBalance(receiver.getBalance().add(transactionDTO.value()));

        this.transactionRepository.save(transaction);
        this.userService.saveUser(sender);
        this.userService.saveUser(receiver);

        // this.notificationService.sendNotification(sender, "Transação realizada com sucesso");
        // this.notificationService.sendNotification(receiver, "Transação realizada com sucesso");

        return transaction;
    }

    public boolean authorize(User sender, BigDecimal value) {
        try {
            ResponseEntity<Map> authorizationResponse = restTemplate.getForEntity("https://run.mocky.io/v3/0525fa14-535e-4c69-bcfc-34d4dda98f66", Map.class);
            boolean authorizedMessage = Objects.requireNonNull(authorizationResponse.getBody()).get("message").equals("Autorizado");
            return authorizationResponse.getStatusCode() == HttpStatus.OK && authorizedMessage;
        } catch (NullPointerException e) {
            throw new IllegalArgumentException("Body de autorização nulo");
        }
    }

    public boolean isNotAuthorized(User sender, BigDecimal value){
        return !authorize(sender, value);
    }

}
