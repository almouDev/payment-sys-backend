package com.almou.payment.metier;

import com.almou.payment.beans.Client;
import com.almou.payment.beans.Transaction;
import com.almou.payment.beans.Transfer;

import java.util.List;

public interface ServiceMetier {
    void addClient(Client client)throws RuntimeException;
    Client getClientById(String phone);
    Transfer sendMoney(String sender, String receiver , Double amount);
    void deposit(Double amount,String recipient);

    List<Transaction> getClientTransactions(String phone);

    void confirmTransaction(Long transactionId, Long confirmationCode);
}
