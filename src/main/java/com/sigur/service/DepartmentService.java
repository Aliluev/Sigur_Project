package com.sigur.service;

import com.sigur.model.Department;
import com.sigur.repository.DepartmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class DepartmentService {

    private DepartmentRepository departmentRepository;

    @Autowired
    public DepartmentService(DepartmentRepository departmentRepository) {
        this.departmentRepository = departmentRepository;
    }

    public Department randomDepartment(){
        int randomValue = (int)(Math.random() * 9);
        return departmentRepository.getById(randomValue);
    }



}
