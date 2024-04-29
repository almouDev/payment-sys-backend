package com.almou.payment.dao;

import com.almou.payment.beans.Transaction;
import org.springframework.data.repository.CrudRepository;
public interface TransactionRepository extends CrudRepository<Transaction, Long>{
}
