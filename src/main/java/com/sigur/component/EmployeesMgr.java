package com.sigur.component;

import com.sigur.model.Department;
import com.sigur.model.Employee;
import com.sigur.model.Type;
import com.sigur.repository.DepartmentRepository;
import com.sigur.repository.EmployeeRepository;
import com.sigur.repository.GuestRepository;
import com.sigur.service.DepartmentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.annotation.ScheduledAnnotationBeanPostProcessor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;



@Component
@Transactional
public class EmployeesMgr {

    //������ � ���� ����� ����������� ��������� ������� ��� ����� � ��� ���������� ��� ����� ����� � ����������
    //�������� ����������� � ���������� ����
    //���� � 11-13 ���������� ����
    //c 13-18 ����������

    //����� ����������(������ ����������)
    private Date actualDate = new Date(2022-1900,0,1,0,0,0);

    private Logger logger = LoggerFactory.getLogger(EmployeesMgr.class);
    private Date dateEnd = new Date(2022-1900,11,31,23,59,59);

    private EmployeeRepository employeeRepository;

    private DepartmentRepository departmentRepository;

    private ScheduledAnnotationBeanPostProcessor scheduledAnnotationBeanPostProcessor;

    private GuestRepository guestRepository;
    private DepartmentService departmentService;

    @Autowired
    public EmployeesMgr(EmployeeRepository employeeRepository, DepartmentRepository departmentRepository, GuestRepository guestRepository, ScheduledAnnotationBeanPostProcessor scheduledAnnotationBeanPostProcessor, DepartmentService departmentService) {
        this.employeeRepository = employeeRepository;
        this.departmentRepository = departmentRepository;
        this.guestRepository = guestRepository;
        this.scheduledAnnotationBeanPostProcessor = scheduledAnnotationBeanPostProcessor;
        this.departmentService = departmentService;
    }

    @Scheduled(fixedRate = 1000)
    public void oneDay(){
        hire();
        nextDay();
        System.out.println("new Day");
    }

    private Date between(Date startInclusive, Date endExclusive) {
        long startMillis = startInclusive.getTime();
        long endMillis = endExclusive.getTime();
        long randomMillisSinceEpoch = ThreadLocalRandom
                .current()
                .nextLong(startMillis, endMillis);

        return new Date(randomMillisSinceEpoch);
    }

    //������ ���������
    //���� ���������� � 8:00-12:00
    private void hire (){
        //��������� ����� Person
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[16];
        random.nextBytes(bytes);
        Department department =departmentService.randomDepartment();
        Employee employee = new Employee(bytes,Type.EMLOYYEE,between(actualDate,dateEnd),null,department);
        employeeRepository.save(employee);
        logger.info(actualDate +"."+" Employee " + employee.getId() + " hire " + employee.getHireTime().toString() + ".Department: " + department.getName());

    }

    private void nextDay(){
        if(actualDate.getMonth() == 11 && actualDate.getDay() == 31){
            //����������� ������ Bean
            scheduledAnnotationBeanPostProcessor.destroy();
        }
        if (((actualDate.getDate() % 5) == 0) && actualDate.getDate()>4 ){
            destroyMethod();
        }
        if(actualDate.getDay() == 31){
            actualDate = new Date(actualDate.getYear(), actualDate.getMonth() + 1,0,0,0,0);
        }else {
            actualDate = new Date(actualDate.getYear(),actualDate.getMonth(),actualDate.getDate() + 1, 0,0,0 );
          //  actualDate.setDate();
        }
    }


    //���������� ���������� � 12:00-15:00
    private void destroyMethod(){
        //�� 1 �� 3 ����� �������
        int randomKolPerson = 1 + (int)(Math.random() * 2);
        List<Employee> employeeList = new ArrayList<>();
        for(int i=0;i<randomKolPerson;) {
            Employee employee = randomEmployee();
            if(employee.getFiredTime()==null){
                employeeList.add(employee);
                i++;
            }
        }

        for(Employee employee : employeeList) {
            Date dateLeftRandom = new Date(actualDate.getYear(), actualDate.getMonth(), actualDate.getDay(), 12, 0, 0);
            Date dateRightRandom = new Date(actualDate.getYear(), actualDate.getMonth(), actualDate.getDay(), 15, 0, 0);
            employee.setFiredTime(between(dateLeftRandom, dateRightRandom));
            employeeRepository.save(employee);
            logger.info(actualDate + ".Employee " + employee.getId() + " fired " + employee.getFiredTime() + ". Department:" + employee.getDepartment().getName() + ". Worked:"+workTime(employee.getFiredTime(),employee.getHireTime()));
        }
    }

    private Employee randomEmployee(){
        List<Employee> employeeList = employeeRepository.findAll();
        int randomNumber = (int)(Math.random() * employeeRepository.count() - 1);
        return employeeList.get(randomNumber);
    }


    private String workTime(Date dateLeft, Date dateRight){
        Date date = new Date(dateLeft.getTime()-dateRight.getTime());
        String year;
        if(date.getYear()>1970){
             year = ""+date.getYear();
             return "y:"+date.getYear()+"w:"+date.getMonth()+"d:"+date.getDate()+"h:"+date.getMinutes();
        }else {
            return "w:"+date.getMonth()+"d:"+date.getDate()+"h:"+date.getMinutes();
        }
    }
}
