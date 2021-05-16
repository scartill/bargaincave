package com.amplifyframework.datastore.generated.model;


import java.util.List;
import java.util.UUID;
import java.util.Objects;

import androidx.core.util.ObjectsCompat;

import com.amplifyframework.core.model.AuthStrategy;
import com.amplifyframework.core.model.Model;
import com.amplifyframework.core.model.ModelOperation;
import com.amplifyframework.core.model.annotations.AuthRule;
import com.amplifyframework.core.model.annotations.Index;
import com.amplifyframework.core.model.annotations.ModelConfig;
import com.amplifyframework.core.model.annotations.ModelField;
import com.amplifyframework.core.model.query.predicate.QueryField;

import static com.amplifyframework.core.model.query.predicate.QueryField.field;

/** This is an auto generated class representing the Lot type in your schema. */
@SuppressWarnings("all")
@ModelConfig(pluralName = "Lots", authRules = {
  @AuthRule(allow = AuthStrategy.PUBLIC, operations = { ModelOperation.CREATE, ModelOperation.UPDATE, ModelOperation.DELETE, ModelOperation.READ })
})
public final class Lot implements Model {
  public static final QueryField ID = field("id");
  public static final QueryField WEIGHT_KG = field("weightKg");
  public static final QueryField COMMENT = field("comment");
  public static final QueryField RESOURCES = field("resources");
  public static final QueryField FRUIT = field("fruit");
  public static final QueryField PRICE = field("price");
  public static final QueryField PRICE_CURRENCY = field("priceCurrency");
  private final @ModelField(targetType="ID", isRequired = true) String id;
  private final @ModelField(targetType="Float") Float weightKg;
  private final @ModelField(targetType="String") String comment;
  private final @ModelField(targetType="AWSJSON") String resources;
  private final @ModelField(targetType="String") String fruit;
  private final @ModelField(targetType="Float") Float price;
  private final @ModelField(targetType="String") String priceCurrency;
  public String getId() {
      return id;
  }
  
  public Float getWeightKg() {
      return weightKg;
  }
  
  public String getComment() {
      return comment;
  }
  
  public String getResources() {
      return resources;
  }
  
  public String getFruit() {
      return fruit;
  }
  
  public Float getPrice() {
      return price;
  }
  
  public String getPriceCurrency() {
      return priceCurrency;
  }
  
  private Lot(String id, Float weightKg, String comment, String resources, String fruit, Float price, String priceCurrency) {
    this.id = id;
    this.weightKg = weightKg;
    this.comment = comment;
    this.resources = resources;
    this.fruit = fruit;
    this.price = price;
    this.priceCurrency = priceCurrency;
  }
  
  @Override
   public boolean equals(Object obj) {
      if (this == obj) {
        return true;
      } else if(obj == null || getClass() != obj.getClass()) {
        return false;
      } else {
      Lot lot = (Lot) obj;
      return ObjectsCompat.equals(getId(), lot.getId()) &&
              ObjectsCompat.equals(getWeightKg(), lot.getWeightKg()) &&
              ObjectsCompat.equals(getComment(), lot.getComment()) &&
              ObjectsCompat.equals(getResources(), lot.getResources()) &&
              ObjectsCompat.equals(getFruit(), lot.getFruit()) &&
              ObjectsCompat.equals(getPrice(), lot.getPrice()) &&
              ObjectsCompat.equals(getPriceCurrency(), lot.getPriceCurrency());
      }
  }
  
  @Override
   public int hashCode() {
    return new StringBuilder()
      .append(getId())
      .append(getWeightKg())
      .append(getComment())
      .append(getResources())
      .append(getFruit())
      .append(getPrice())
      .append(getPriceCurrency())
      .toString()
      .hashCode();
  }
  
  @Override
   public String toString() {
    return new StringBuilder()
      .append("Lot {")
      .append("id=" + String.valueOf(getId()) + ", ")
      .append("weightKg=" + String.valueOf(getWeightKg()) + ", ")
      .append("comment=" + String.valueOf(getComment()) + ", ")
      .append("resources=" + String.valueOf(getResources()) + ", ")
      .append("fruit=" + String.valueOf(getFruit()) + ", ")
      .append("price=" + String.valueOf(getPrice()) + ", ")
      .append("priceCurrency=" + String.valueOf(getPriceCurrency()))
      .append("}")
      .toString();
  }
  
  public static BuildStep builder() {
      return new Builder();
  }
  
  /** 
   * WARNING: This method should not be used to build an instance of this object for a CREATE mutation.
   * This is a convenience method to return an instance of the object with only its ID populated
   * to be used in the context of a parameter in a delete mutation or referencing a foreign key
   * in a relationship.
   * @param id the id of the existing item this instance will represent
   * @return an instance of this model with only ID populated
   * @throws IllegalArgumentException Checks that ID is in the proper format
   */
  public static Lot justId(String id) {
    try {
      UUID.fromString(id); // Check that ID is in the UUID format - if not an exception is thrown
    } catch (Exception exception) {
      throw new IllegalArgumentException(
              "Model IDs must be unique in the format of UUID. This method is for creating instances " +
              "of an existing object with only its ID field for sending as a mutation parameter. When " +
              "creating a new object, use the standard builder method and leave the ID field blank."
      );
    }
    return new Lot(
      id,
      null,
      null,
      null,
      null,
      null,
      null
    );
  }
  
  public CopyOfBuilder copyOfBuilder() {
    return new CopyOfBuilder(id,
      weightKg,
      comment,
      resources,
      fruit,
      price,
      priceCurrency);
  }
  public interface BuildStep {
    Lot build();
    BuildStep id(String id) throws IllegalArgumentException;
    BuildStep weightKg(Float weightKg);
    BuildStep comment(String comment);
    BuildStep resources(String resources);
    BuildStep fruit(String fruit);
    BuildStep price(Float price);
    BuildStep priceCurrency(String priceCurrency);
  }
  

  public static class Builder implements BuildStep {
    private String id;
    private Float weightKg;
    private String comment;
    private String resources;
    private String fruit;
    private Float price;
    private String priceCurrency;
    @Override
     public Lot build() {
        String id = this.id != null ? this.id : UUID.randomUUID().toString();
        
        return new Lot(
          id,
          weightKg,
          comment,
          resources,
          fruit,
          price,
          priceCurrency);
    }
    
    @Override
     public BuildStep weightKg(Float weightKg) {
        this.weightKg = weightKg;
        return this;
    }
    
    @Override
     public BuildStep comment(String comment) {
        this.comment = comment;
        return this;
    }
    
    @Override
     public BuildStep resources(String resources) {
        this.resources = resources;
        return this;
    }
    
    @Override
     public BuildStep fruit(String fruit) {
        this.fruit = fruit;
        return this;
    }
    
    @Override
     public BuildStep price(Float price) {
        this.price = price;
        return this;
    }
    
    @Override
     public BuildStep priceCurrency(String priceCurrency) {
        this.priceCurrency = priceCurrency;
        return this;
    }
    
    /** 
     * WARNING: Do not set ID when creating a new object. Leave this blank and one will be auto generated for you.
     * This should only be set when referring to an already existing object.
     * @param id id
     * @return Current Builder instance, for fluent method chaining
     * @throws IllegalArgumentException Checks that ID is in the proper format
     */
    public BuildStep id(String id) throws IllegalArgumentException {
        this.id = id;
        
        try {
            UUID.fromString(id); // Check that ID is in the UUID format - if not an exception is thrown
        } catch (Exception exception) {
          throw new IllegalArgumentException("Model IDs must be unique in the format of UUID.",
                    exception);
        }
        
        return this;
    }
  }
  

  public final class CopyOfBuilder extends Builder {
    private CopyOfBuilder(String id, Float weightKg, String comment, String resources, String fruit, Float price, String priceCurrency) {
      super.id(id);
      super.weightKg(weightKg)
        .comment(comment)
        .resources(resources)
        .fruit(fruit)
        .price(price)
        .priceCurrency(priceCurrency);
    }
    
    @Override
     public CopyOfBuilder weightKg(Float weightKg) {
      return (CopyOfBuilder) super.weightKg(weightKg);
    }
    
    @Override
     public CopyOfBuilder comment(String comment) {
      return (CopyOfBuilder) super.comment(comment);
    }
    
    @Override
     public CopyOfBuilder resources(String resources) {
      return (CopyOfBuilder) super.resources(resources);
    }
    
    @Override
     public CopyOfBuilder fruit(String fruit) {
      return (CopyOfBuilder) super.fruit(fruit);
    }
    
    @Override
     public CopyOfBuilder price(Float price) {
      return (CopyOfBuilder) super.price(price);
    }
    
    @Override
     public CopyOfBuilder priceCurrency(String priceCurrency) {
      return (CopyOfBuilder) super.priceCurrency(priceCurrency);
    }
  }
  
}
