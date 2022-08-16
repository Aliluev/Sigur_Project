package com.sigur.model;

import com.sun.istack.NotNull;

import javax.persistence.*;
import javax.validation.constraints.Size;

@Entity
@Table(name = "users")
public class Person {

    @Id
    @NotNull
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    protected Integer id;

    @Size(max = 16)
    @Column(name = "card",unique = true)
    protected byte[] card;

    @Column(name = "person_type")
    protected Type type;
    public Person() {
    }

    public Person(Integer id, @Size(max = 16) byte[] card, Type type) {
        this.id = id;
        this.card = card;
        this.type = type;
    }

    public Person(@Size(max = 16) byte[] card, Type type) {
        this.card = card;
        this.type = type;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public byte[] getCard() {
        return card;
    }

    public void setCard(byte[] card) {
        this.card = card;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

}
