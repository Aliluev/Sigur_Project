package com.sigur.component;

import com.sigur.model.Department;
import com.sigur.model.Employee;
import com.sigur.model.Type;
import com.sigur.repository.DepartmentRepository;
import com.sigur.repository.EmployeeRepository;
import com.sigur.repository.GuestRepository;
import com.sigur.service.DepartmentService;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.annotation.ScheduledAnnotationBeanPostProcessor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;



@Component
@Transactional
public class EmployeesMgr {

    //Пускай у меня будут специальные интервалы времени для найма и для увольнения где также будет в промежутке
    //Рандомно увольняться и наниматься люди
    //с 8-12 происходит найм
    //c 12-15 увольнение

    //Время актуальное(работа компонента)
    private Date actualDate = new Date(2022-1900,0,1,0,0,0);

    private Logger logger = Logger.getLogger(EmployeesMgr.class.getName());

    FileHandler fh;


    private Date dateEnd = new Date(2022-1900,11,31,23,59,59);

    private EmployeeRepository employeeRepository;

    private int init = 0;

    private DepartmentRepository departmentRepository;

    private ScheduledAnnotationBeanPostProcessor scheduledAnnotationBeanPostProcessor;


    private DepartmentService departmentService;

    @Autowired
    public EmployeesMgr(EmployeeRepository employeeRepository, DepartmentRepository departmentRepository, ScheduledAnnotationBeanPostProcessor scheduledAnnotationBeanPostProcessor, DepartmentService departmentService) throws IOException {
        this.employeeRepository = employeeRepository;
        this.departmentRepository = departmentRepository;
        this.scheduledAnnotationBeanPostProcessor = scheduledAnnotationBeanPostProcessor;
        this.departmentService = departmentService;
        fh = new FileHandler("EmployeesMgr.log");
        logger.addHandler(fh);
    }

    @Scheduled(fixedRate = 1000)
    public void oneDay(){
        if(init==0){
        for(int i=0;i<10;i++){
            Department department = new Department(i,""+i);
            departmentRepository.save(department);
        }
        init++;
        }

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

    //нанять работника
    //найм происходит с 8:00-12:00
    private void hire (){
        //Рандомная карта Person
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
            //Прекращения работы Bean
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


    //Увольнение происходит с 12:00-15:00
    private void destroyMethod(){
        //от 1 до 3 нужно уволить
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
