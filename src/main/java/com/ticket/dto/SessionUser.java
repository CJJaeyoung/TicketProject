package com.ticket.dto;

import com.ticket.entity.Member;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class SessionUser implements Serializable {
   
   private String name;
   private String email;
   private String picture;
   private String provide;
   
   public SessionUser(Member member) {
      this.name = member.getName();
      this.email =member.getEmail();
      this.picture=member.getPicture();
      this.provide=member.getProvider();
   }
}
