package com.sigur.model;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.Date;

@Entity
public class Guest extends Person {

    @Column(name = "VISIT_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date visitDate;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "employee_id", referencedColumnName = "id")
    private Employee employee;

    public Guest(Date visitDate, Employee employee, byte[] card) {
        this.visitDate = visitDate;
        this.employee = employee;
        this.card=card;
        this.type=Type.GUEST;
    }

    public Guest() {
    }

    public Guest(Integer id, @Size(max = 16) byte[] card, Type type, Date visitDate, Employee employee) {
        super(id, card, type);
        this.visitDate = visitDate;
        this.employee = employee;
    }

    public Guest(@Size(max = 16) byte[] card, Type type, Date visitDate, Employee employee) {
        super(card, type);
        this.visitDate = visitDate;
        this.employee = employee;
    }

    public Date getVisitDate() {
        return visitDate;
    }

    public void setVisitDate(Date visitDate) {
        this.visitDate = visitDate;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }
}
