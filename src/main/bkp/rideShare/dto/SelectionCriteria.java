package com.test.dto;


import com.test.strategy.SelectionStrategy;
import lombok.Data;

@Data
public class SelectionCriteria {
  private final String name;
  private String model;
  public SelectionCriteria(String name){
    if(name.contains("Preferred Vehicle")){
      this.name = "Preferred Vehicle";
      this.model = name.split("=")[1];
    }else{
      this.name = name;
    }
  }
}
