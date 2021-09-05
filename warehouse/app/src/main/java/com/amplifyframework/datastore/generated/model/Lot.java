package com.amplifyframework.datastore.generated.model;


import java.util.List;
import java.util.UUID;
import java.util.Objects;

import androidx.core.util.ObjectsCompat;

import com.amplifyframework.core.model.Model;
import com.amplifyframework.core.model.annotations.Index;
import com.amplifyframework.core.model.annotations.ModelConfig;
import com.amplifyframework.core.model.annotations.ModelField;
import com.amplifyframework.core.model.query.predicate.QueryField;

import static com.amplifyframework.core.model.query.predicate.QueryField.field;

/** This is an auto generated class representing the Lot type in your schema. */
@SuppressWarnings("all")
@ModelConfig(pluralName = "Lots")
public final class Lot implements Model {
  public static final QueryField ID = field("Lot", "id");
  public static final QueryField FRUIT = field("Lot", "fruit");
  public static final QueryField VARIETY = field("Lot", "variety");
  public static final QueryField TOTAL_WEIGHT_KG = field("Lot", "totalWeightKg");
  public static final QueryField CALIBER = field("Lot", "caliber");
  public static final QueryField PALLET_WEIGHT_KG = field("Lot", "palletWeightKg");
  public static final QueryField CONDITION = field("Lot", "condition");
  public static final QueryField ORIGIN = field("Lot", "origin");
  public static final QueryField ARRIVAL = field("Lot", "arrival");
  public static final QueryField EXPIRATION = field("Lot", "expiration");
  public static final QueryField RESOURCES = field("Lot", "resources");
  public static final QueryField COMMENT = field("Lot", "comment");
  public static final QueryField PRICE_PER_PALLET = field("Lot", "pricePerPallet");
  public static final QueryField PRICE_CURRENCY = field("Lot", "priceCurrency");
  public static final QueryField STATE = field("Lot", "state");
  private final @ModelField(targetType="ID", isRequired = true) String id;
  private final @ModelField(targetType="String") String fruit;
  private final @ModelField(targetType="String") String variety;
  private final @ModelField(targetType="Float") Double totalWeightKg;
  private final @ModelField(targetType="Int") Integer caliber;
  private final @ModelField(targetType="Float") Double palletWeightKg;
  private final @ModelField(targetType="String") String condition;
  private final @ModelField(targetType="String") String origin;
  private final @ModelField(targetType="String") String arrival;
  private final @ModelField(targetType="String") String expiration;
  private final @ModelField(targetType="AWSJSON") String resources;
  private final @ModelField(targetType="String") String comment;
  private final @ModelField(targetType="Float") Double pricePerPallet;
  private final @ModelField(targetType="String") String priceCurrency;
  private final @ModelField(targetType="String") String state;
  public String getId() {
      return id;
  }
  
  public String getFruit() {
      return fruit;
  }
  
  public String getVariety() {
      return variety;
  }
  
  public Double getTotalWeightKg() {
      return totalWeightKg;
  }
  
  public Integer getCaliber() {
      return caliber;
  }
  
  public Double getPalletWeightKg() {
      return palletWeightKg;
  }
  
  public String getCondition() {
      return condition;
  }
  
  public String getOrigin() {
      return origin;
  }
  
  public String getArrival() {
      return arrival;
  }
  
  public String getExpiration() {
      return expiration;
  }
  
  public String getResources() {
      return resources;
  }
  
  public String getComment() {
      return comment;
  }
  
  public Double getPricePerPallet() {
      return pricePerPallet;
  }
  
  public String getPriceCurrency() {
      return priceCurrency;
  }
  
  public String getState() {
      return state;
  }
  
  private Lot(String id, String fruit, String variety, Double totalWeightKg, Integer caliber, Double palletWeightKg, String condition, String origin, String arrival, String expiration, String resources, String comment, Double pricePerPallet, String priceCurrency, String state) {
    this.id = id;
    this.fruit = fruit;
    this.variety = variety;
    this.totalWeightKg = totalWeightKg;
    this.caliber = caliber;
    this.palletWeightKg = palletWeightKg;
    this.condition = condition;
    this.origin = origin;
    this.arrival = arrival;
    this.expiration = expiration;
    this.resources = resources;
    this.comment = comment;
    this.pricePerPallet = pricePerPallet;
    this.priceCurrency = priceCurrency;
    this.state = state;
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
              ObjectsCompat.equals(getFruit(), lot.getFruit()) &&
              ObjectsCompat.equals(getVariety(), lot.getVariety()) &&
              ObjectsCompat.equals(getTotalWeightKg(), lot.getTotalWeightKg()) &&
              ObjectsCompat.equals(getCaliber(), lot.getCaliber()) &&
              ObjectsCompat.equals(getPalletWeightKg(), lot.getPalletWeightKg()) &&
              ObjectsCompat.equals(getCondition(), lot.getCondition()) &&
              ObjectsCompat.equals(getOrigin(), lot.getOrigin()) &&
              ObjectsCompat.equals(getArrival(), lot.getArrival()) &&
              ObjectsCompat.equals(getExpiration(), lot.getExpiration()) &&
              ObjectsCompat.equals(getResources(), lot.getResources()) &&
              ObjectsCompat.equals(getComment(), lot.getComment()) &&
              ObjectsCompat.equals(getPricePerPallet(), lot.getPricePerPallet()) &&
              ObjectsCompat.equals(getPriceCurrency(), lot.getPriceCurrency()) &&
              ObjectsCompat.equals(getState(), lot.getState());
      }
  }
  
  @Override
   public int hashCode() {
    return new StringBuilder()
      .append(getId())
      .append(getFruit())
      .append(getVariety())
      .append(getTotalWeightKg())
      .append(getCaliber())
      .append(getPalletWeightKg())
      .append(getCondition())
      .append(getOrigin())
      .append(getArrival())
      .append(getExpiration())
      .append(getResources())
      .append(getComment())
      .append(getPricePerPallet())
      .append(getPriceCurrency())
      .append(getState())
      .toString()
      .hashCode();
  }
  
  @Override
   public String toString() {
    return new StringBuilder()
      .append("Lot {")
      .append("id=" + String.valueOf(getId()) + ", ")
      .append("fruit=" + String.valueOf(getFruit()) + ", ")
      .append("variety=" + String.valueOf(getVariety()) + ", ")
      .append("totalWeightKg=" + String.valueOf(getTotalWeightKg()) + ", ")
      .append("caliber=" + String.valueOf(getCaliber()) + ", ")
      .append("palletWeightKg=" + String.valueOf(getPalletWeightKg()) + ", ")
      .append("condition=" + String.valueOf(getCondition()) + ", ")
      .append("origin=" + String.valueOf(getOrigin()) + ", ")
      .append("arrival=" + String.valueOf(getArrival()) + ", ")
      .append("expiration=" + String.valueOf(getExpiration()) + ", ")
      .append("resources=" + String.valueOf(getResources()) + ", ")
      .append("comment=" + String.valueOf(getComment()) + ", ")
      .append("pricePerPallet=" + String.valueOf(getPricePerPallet()) + ", ")
      .append("priceCurrency=" + String.valueOf(getPriceCurrency()) + ", ")
      .append("state=" + String.valueOf(getState()))
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
      null,
      null,
      null,
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
      fruit,
      variety,
      totalWeightKg,
      caliber,
      palletWeightKg,
      condition,
      origin,
      arrival,
      expiration,
      resources,
      comment,
      pricePerPallet,
      priceCurrency,
      state);
  }
  public interface BuildStep {
    Lot build();
    BuildStep id(String id) throws IllegalArgumentException;
    BuildStep fruit(String fruit);
    BuildStep variety(String variety);
    BuildStep totalWeightKg(Double totalWeightKg);
    BuildStep caliber(Integer caliber);
    BuildStep palletWeightKg(Double palletWeightKg);
    BuildStep condition(String condition);
    BuildStep origin(String origin);
    BuildStep arrival(String arrival);
    BuildStep expiration(String expiration);
    BuildStep resources(String resources);
    BuildStep comment(String comment);
    BuildStep pricePerPallet(Double pricePerPallet);
    BuildStep priceCurrency(String priceCurrency);
    BuildStep state(String state);
  }
  

  public static class Builder implements BuildStep {
    private String id;
    private String fruit;
    private String variety;
    private Double totalWeightKg;
    private Integer caliber;
    private Double palletWeightKg;
    private String condition;
    private String origin;
    private String arrival;
    private String expiration;
    private String resources;
    private String comment;
    private Double pricePerPallet;
    private String priceCurrency;
    private String state;
    @Override
     public Lot build() {
        String id = this.id != null ? this.id : UUID.randomUUID().toString();
        
        return new Lot(
          id,
          fruit,
          variety,
          totalWeightKg,
          caliber,
          palletWeightKg,
          condition,
          origin,
          arrival,
          expiration,
          resources,
          comment,
          pricePerPallet,
          priceCurrency,
          state);
    }
    
    @Override
     public BuildStep fruit(String fruit) {
        this.fruit = fruit;
        return this;
    }
    
    @Override
     public BuildStep variety(String variety) {
        this.variety = variety;
        return this;
    }
    
    @Override
     public BuildStep totalWeightKg(Double totalWeightKg) {
        this.totalWeightKg = totalWeightKg;
        return this;
    }
    
    @Override
     public BuildStep caliber(Integer caliber) {
        this.caliber = caliber;
        return this;
    }
    
    @Override
     public BuildStep palletWeightKg(Double palletWeightKg) {
        this.palletWeightKg = palletWeightKg;
        return this;
    }
    
    @Override
     public BuildStep condition(String condition) {
        this.condition = condition;
        return this;
    }
    
    @Override
     public BuildStep origin(String origin) {
        this.origin = origin;
        return this;
    }
    
    @Override
     public BuildStep arrival(String arrival) {
        this.arrival = arrival;
        return this;
    }
    
    @Override
     public BuildStep expiration(String expiration) {
        this.expiration = expiration;
        return this;
    }
    
    @Override
     public BuildStep resources(String resources) {
        this.resources = resources;
        return this;
    }
    
    @Override
     public BuildStep comment(String comment) {
        this.comment = comment;
        return this;
    }
    
    @Override
     public BuildStep pricePerPallet(Double pricePerPallet) {
        this.pricePerPallet = pricePerPallet;
        return this;
    }
    
    @Override
     public BuildStep priceCurrency(String priceCurrency) {
        this.priceCurrency = priceCurrency;
        return this;
    }
    
    @Override
     public BuildStep state(String state) {
        this.state = state;
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
    private CopyOfBuilder(String id, String fruit, String variety, Double totalWeightKg, Integer caliber, Double palletWeightKg, String condition, String origin, String arrival, String expiration, String resources, String comment, Double pricePerPallet, String priceCurrency, String state) {
      super.id(id);
      super.fruit(fruit)
        .variety(variety)
        .totalWeightKg(totalWeightKg)
        .caliber(caliber)
        .palletWeightKg(palletWeightKg)
        .condition(condition)
        .origin(origin)
        .arrival(arrival)
        .expiration(expiration)
        .resources(resources)
        .comment(comment)
        .pricePerPallet(pricePerPallet)
        .priceCurrency(priceCurrency)
        .state(state);
    }
    
    @Override
     public CopyOfBuilder fruit(String fruit) {
      return (CopyOfBuilder) super.fruit(fruit);
    }
    
    @Override
     public CopyOfBuilder variety(String variety) {
      return (CopyOfBuilder) super.variety(variety);
    }
    
    @Override
     public CopyOfBuilder totalWeightKg(Double totalWeightKg) {
      return (CopyOfBuilder) super.totalWeightKg(totalWeightKg);
    }
    
    @Override
     public CopyOfBuilder caliber(Integer caliber) {
      return (CopyOfBuilder) super.caliber(caliber);
    }
    
    @Override
     public CopyOfBuilder palletWeightKg(Double palletWeightKg) {
      return (CopyOfBuilder) super.palletWeightKg(palletWeightKg);
    }
    
    @Override
     public CopyOfBuilder condition(String condition) {
      return (CopyOfBuilder) super.condition(condition);
    }
    
    @Override
     public CopyOfBuilder origin(String origin) {
      return (CopyOfBuilder) super.origin(origin);
    }
    
    @Override
     public CopyOfBuilder arrival(String arrival) {
      return (CopyOfBuilder) super.arrival(arrival);
    }
    
    @Override
     public CopyOfBuilder expiration(String expiration) {
      return (CopyOfBuilder) super.expiration(expiration);
    }
    
    @Override
     public CopyOfBuilder resources(String resources) {
      return (CopyOfBuilder) super.resources(resources);
    }
    
    @Override
     public CopyOfBuilder comment(String comment) {
      return (CopyOfBuilder) super.comment(comment);
    }
    
    @Override
     public CopyOfBuilder pricePerPallet(Double pricePerPallet) {
      return (CopyOfBuilder) super.pricePerPallet(pricePerPallet);
    }
    
    @Override
     public CopyOfBuilder priceCurrency(String priceCurrency) {
      return (CopyOfBuilder) super.priceCurrency(priceCurrency);
    }
    
    @Override
     public CopyOfBuilder state(String state) {
      return (CopyOfBuilder) super.state(state);
    }
  }
  
}
