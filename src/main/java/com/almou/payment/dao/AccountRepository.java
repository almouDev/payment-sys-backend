package com.almou.payment.dao;

import com.almou.payment.beans.Account;
import org.springframework.data.repository.CrudRepository;

public interface AccountRepository extends CrudRepository<Account,Long> {
}
