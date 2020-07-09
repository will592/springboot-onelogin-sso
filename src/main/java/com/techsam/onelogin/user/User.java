package com.techsam.onelogin.user;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class User {

  private String id;

  public String getId() {
    return this.id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public User(String id) {
    this.id = id;
  }


}
