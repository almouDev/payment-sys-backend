package com.almou.payment.beans;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Entity;
import lombok.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
public class Transfer extends Transaction{
    private String sender;
    private String receiver;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Long confirmationCode;
    private long codeExpiration;

    @Override
    public String toString() {
        return super.toString()+
                "sender='" + sender + '\'' +
                ", receiver='" + receiver + '\'' +
                ", confirmationCode=" + confirmationCode +
                ", codeExpiration=" + codeExpiration +
                '}';
    }
}
