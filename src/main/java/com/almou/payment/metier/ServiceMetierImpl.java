package com.almou.payment.metier;
import com.almou.payment.beans.*;
import com.almou.payment.dao.ClientRepository;
import com.almou.payment.dao.TransactionRepository;
import com.almou.payment.utils.MainUtils;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Component
@Transactional
@AllArgsConstructor
public class ServiceMetierImpl implements ServiceMetier{
    private ClientRepository clientRepository;
    private TransactionRepository transactionRepository;
    @Override
    public void addClient(Client client) throws RuntimeException{
        if(getClientById(client.getPhone())!=null)
            throw new RuntimeException("User already exists");
        PasswordEncoder passwordEncoder= PasswordEncoderFactories.createDelegatingPasswordEncoder();
        String pwd=passwordEncoder.encode(client.getPassword());
        client.setPassword(pwd);
        Account account=new Account();
        account.setBalance((double) 0);
        client.setAccount(account);
        clientRepository.save(client);
    }
    @Override
    public Client getClientById(String phone) {
        return clientRepository.findById(phone).orElse(null);
    }
    @Override
    public Transfer sendMoney(String sender, String receiver, Double amount) {
        Client Sender=getClientById(sender);
        Account senderAccount=Sender.getAccount();
        Client Receiver=getClientById(receiver);
        if (senderAccount.getBalance()<amount)
            throw new IllegalStateException("Insufficient fund");
        if (Receiver==null)throw new IllegalStateException("The recipient account doesn't exist ");
        Transfer transaction=new Transfer();
        transaction.setTransaction_id(MainUtils.generateUniqueNumber());
        transaction.setConfirmationCode(MainUtils.generateRandomNumber());
        transaction.setCodeExpiration(MainUtils.generateExpirationTime());
        transaction.setDate(new Date());
        transaction.setConfirmed(false);
        transaction.setAmount(amount);
        transaction.setSender(sender);
        transaction.setReceiver(receiver);
        transaction.setType("Transfer");
        Sender.getTransactions().add(transaction);
        transactionRepository.save(transaction);
        clientRepository.save(Sender);
        return transaction;
    }

    @Override
    public void deposit(Double amount, String recipient) {
        Client client=getClientById(recipient);
        if (client==null)throw new  IllegalStateException("No such account");
        Deposit deposit=new Deposit();
        deposit.setTransaction_id(MainUtils.generateUniqueNumber());
        deposit.setAmount(amount);
        deposit.setDate(new Date());
        deposit.setConfirmed(true);
        deposit.setType("Deposit");
        client.getTransactions().add(deposit);
        transactionRepository.save(deposit);
        client.getAccount().setBalance(client.getAccount().getBalance()+amount);
        clientRepository.save(client);
    }
    @Override
    public List<Transaction> getClientTransactions(String phone) {
        return getClientById(phone).getTransactions();
    }

    @Override
    public void confirmTransaction(Long transactionId, Long confirmationCode) {
        Transfer transaction= (Transfer) transactionRepository.findById(transactionId)
                .orElse(null);
        if (transaction==null)
            throw new IllegalStateException("There no transaction with the given transaction ID in our database");
        if (transaction.getConfirmationCode().equals(confirmationCode))
            throw new IllegalStateException("The confirmation code you've provided is invalid");
        if(MainUtils.isCodeExpired(transaction.getCodeExpiration())){
            transactionRepository.deleteById(transactionId);
            throw new IllegalStateException("The confirmation code you've provided expired");
        }
        transaction.setConfirmed(true);
        Client sender=getClientById(transaction.getSender());
        Account senderAccount=sender.getAccount();
        Client receiver=getClientById(transaction.getReceiver());
        Account receiverAccount=receiver.getAccount();
        senderAccount.setBalance(senderAccount.getBalance()-transaction.getAmount());
        receiverAccount.setBalance(receiverAccount.getBalance()+transaction.getAmount());
        receiver.getTransactions().add(transaction);
        transactionRepository.save(transaction);
        clientRepository.saveAll(Arrays.asList(sender,receiver));
    }

}
