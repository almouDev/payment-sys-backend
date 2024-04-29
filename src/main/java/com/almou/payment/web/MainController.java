package com.almou.payment.web;
import com.almou.payment.beans.Account;
import com.almou.payment.beans.Client;
import com.almou.payment.beans.Transaction;
import com.almou.payment.metier.ServiceMetier;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class MainController {
    private ServiceMetier serviceMetier;
    private AuthenticationManager authenticationManager;
    @PostMapping("/signup")
    public void signup(@RequestBody Client client, HttpServletResponse response) throws IOException {
        try {
            serviceMetier.addClient(client);
        }catch (RuntimeException exception){
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.getWriter().write(exception.getMessage());
        }
    }

    public MainController(ServiceMetier serviceMetier,AuthenticationManager authenticationManager) {
        this.serviceMetier = serviceMetier;
        this.authenticationManager=authenticationManager;
    }

    @PostMapping("/transfer")
    public void transfer(@RequestParam("amount")Double amount,@RequestParam("sender")String sender,@RequestParam("receiver")String receiver,HttpServletResponse response) throws IOException {
        try {
            serviceMetier.sendMoney(sender,receiver,amount);
        }catch (IllegalStateException exception){
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.getWriter().write(exception.getMessage());
        }
    }
    @PostMapping("deposit")
    public void deposit(@RequestParam("amount")Double amount,String recipient ,HttpServletResponse response) throws IOException {
        try {
            serviceMetier.deposit(amount,recipient);
        }catch (IllegalStateException exception){
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.getWriter().write(exception.getMessage());
        }
    }
    @GetMapping("my_transactions")
    public List<Transaction> myTransactions(){
        return serviceMetier.getClientTransactions(getAuthenticatedUser());
    }
    String getAuthenticatedUser(){
        Authentication authentication=SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof AnonymousAuthenticationToken){
            return null;
        }
        return authentication.getName();
    }
    @GetMapping("pending_transactions")
    List<Transaction> pendingTransactions(){
        return serviceMetier.getClientTransactions(getAuthenticatedUser())
                .stream()
                .filter(transaction -> !transaction.getConfirmed())
                .collect(Collectors.toList());
    }
    @PostMapping("confirm_transaction")
    void confirmTransaction(@RequestParam("transaction_id") long transaction_id,@RequestParam("confirmation_code") long confirmation_code,HttpServletResponse response) throws IOException {
        try{
            serviceMetier.confirmTransaction(transaction_id,confirmation_code);
        }catch (IllegalStateException e){
            response.setStatus(500);
            response.getWriter().write(e.getMessage());
        }
    }
    @GetMapping("my_account")
    Account myAccount(){
        return serviceMetier.getClientById(getAuthenticatedUser()).getAccount();
    }
    @GetMapping("my_profile")
    Client myProfile(){
        return serviceMetier.getClientById(getAuthenticatedUser());
    }
}
