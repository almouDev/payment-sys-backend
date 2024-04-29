package com.almou.payment.beans;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.Date;
@Entity
@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class  Transaction implements Serializable {
    @Id
    private long transaction_id;
    private Date date;
    private Double amount;
    private Boolean confirmed;
    private String type;

    @Override
    public String toString() {
        return "{" +
                "transaction_id=" + transaction_id +
                ", date=" + date +
                ", amount=" + amount +
                ", confirmed=" + confirmed +
                ", type='" + type + '\'' +
                ',';
    }
}


