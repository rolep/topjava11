package ru.javawebinar.topjava.model;

public class BaseEntity {
  protected Integer id;

  public BaseEntity() {
  }

  public BaseEntity(Integer id) {
    this.id = id;
  }

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public boolean isNew() {
    return this.id == null;
  }

  @Override
  public String toString() {
    return String.format("Entity %s (%s)", getClass().getName(), getId());
  }
}
