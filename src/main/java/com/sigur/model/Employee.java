package com.sigur.model;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.Date;

@Entity
public class Employee extends Person{

    @Column(name = "HIRE_TIME")
    @Temporal(TemporalType.TIMESTAMP)
    protected Date hireTime;

    @Column(name = "FIRED_TIME")
    @Temporal(TemporalType.TIMESTAMP)
    protected Date firedTime;

    @ManyToOne(fetch = FetchType.EAGER,cascade = CascadeType.ALL)
    @JoinColumn(name = "department_id", referencedColumnName = "id")
    protected Department department;

    public Employee(Integer id, @Size(max = 16) byte[] card, Type type, Date hireTime, Date firedTime, Department department) {
        super(id, card, type);
        this.hireTime = hireTime;
        this.firedTime = firedTime;
        this.department = department;
    }

    public Employee(@Size(max = 16) byte[] card, Type type, Date hireTime, Date firedTime, Department department) {
        super(card, type);
        this.hireTime = hireTime;
        this.firedTime = firedTime;
        this.department = department;
    }

    public Employee(){
    }

    public Date getHireTime() {
        return hireTime;
    }

    public void setHireTime(Date hireTime) {
        this.hireTime = hireTime;
    }

    public Date getFiredTime() {
        return firedTime;
    }

    public void setFiredTime(Date firedTime) {
        this.firedTime = firedTime;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
