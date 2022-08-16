package com.sigur.component;

import com.sigur.model.Employee;
import com.sigur.model.Guest;
import com.sigur.model.Type;
import com.sigur.repository.EmployeeRepository;
import com.sigur.repository.GuestRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.FileHandler;

@Component
@Transactional
public class GuestMgr {

    private long kolEmployee;

    private int iteration = 0;


    private java.util.logging.Logger logger = java.util.logging.Logger.getLogger(GuestMgr.class.getName());

    FileHandler fh;

    private Date actualDate = new Date(2022-1900,0,1,0,0,0);

    private int kol = 0;
    private int iterationCheckMeeting = 0;

    EmployeeRepository employeeRepository;

    GuestRepository guestRepository;

    @Autowired
    public GuestMgr(EmployeeRepository employeeRepository, GuestRepository guestRepository) throws IOException {
        this.employeeRepository = employeeRepository;
        this.guestRepository = guestRepository;
        fh = new FileHandler("GuestMgr.log");
        logger.addHandler(fh);
    }

    //10 раз в виртуальные сутки коспонент будет проверять данные в таблице
    //конечно погуглив правильнее наверное бы было организовывать сообщения о изменение в брокере
    //который бы слал уведомления подписчикам, но я не стал сильно усложнять логику
    //так как этот компонент и так вполне справляется с поставленной задачей
    //Метод проверки увольнения
    @Scheduled(fixedRate = 100)
    public void checkdismissal(){
        if(iterationCheckMeeting>10) {
            List<Guest> guestList = guestRepository.findAll();
            for (Guest guest : guestList) {
                if (guest.getEmployee().getFiredTime() != null) {
                    //логично что если сотрудника уволили то гость который шёл к конкретному сотруднику нам не нужен
                    guestRepository.delete(guest);
                    logger.info("Guest meeting "+guest.getId()+" with Employee "+guest.getEmployee().getId()+" canceled. Department: "+guest.getEmployee().getDepartment().getName()+" .Visit date "+guest.getVisitDate()+", date of fired time:"+guest.getEmployee().getFiredTime());
                }
            }
        }
        iterationCheckMeeting++;
    }

    //Так как каждый день нанимается новый сотрудник, то значит каждый день мы два раза в сутки смотрим на наличее нового employee и с вероятностью 1/2 устраиваем встречу
    @Scheduled(fixedRate = 500)
    public void addNewGuest() throws InterruptedException {

        if( iteration <= 2 ){
            kolEmployee = employeeRepository.count();
            iteration++;
        }
        Thread.sleep(100);
        long mykol=employeeRepository.count();
        if (kolEmployee<employeeRepository.count()){
            List<Employee> employeeList = employeeRepository.findAll();
            int[] array = new int[employeeList.size()];
            int ii=0;
            for(Employee employee : employeeList){
                array[ii]=employee.getId();
                ii++;
            }
            Arrays.sort(array);
            //найти самого последнего по добавлению
            Employee employee = employeeRepository.getById(array[array.length-1]);

                //не учёл что через 6 месяцев только встреча
                randomMeeting(employee);

        }
        kolEmployee = employeeRepository.count();
        kol++;
        if(kol==2){
            if(actualDate.getDay() == 31){
                actualDate = new Date(actualDate.getYear(), actualDate.getMonth() + 1,0,0,0,0);
            }else {
                actualDate = new Date(actualDate.getYear(),actualDate.getMonth(),actualDate.getDate() + 1, 0,0,0 );
                //  actualDate.setDate();
            }
            kol=0;
        }

    }

    //нужно с вероятностью 1/2 нанимать сотрудника
    //так как обычный Math.Random() не совсем подходит с небольшим интервалом от 0 до 1
    //реализовал свой рандом у которого разброс значений больше и значения более рандомные
    public void randomMeeting(Employee employee){

        int randomValue = (int)(Math.random() * 10);
        if(randomValue>=5){
            //устраиваем встречу
            meeting(employee);
        }
    }

    public void meeting(Employee employee){
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[16];
        random.nextBytes(bytes);

        //в пределах 6 месяцев должна состояться встреча
       // Employee employee1=employeeRepository.get
        Date dateMeetingLeft = employee.getHireTime();
       // Date dateMeetingRight = employee.getHireTime();
        Date dateMeetingRight;
        if((dateMeetingLeft.getMonth() + 6)<12) {
             dateMeetingRight = new Date(2022,dateMeetingLeft.getMonth() + 6,dateMeetingLeft.getDate());
        }else {
            int nextYear = dateMeetingLeft.getYear();
            nextYear++;
             dateMeetingRight = new Date(nextYear,(dateMeetingLeft.getMonth() + 6) % 11,dateMeetingLeft.getDate());

        }

        Guest guest = new Guest(bytes, Type.GUEST,between(dateMeetingLeft,dateMeetingRight),employee);
        guestRepository.save(guest);
        logger.info("Guest"+employee.getId()+" has meeting with an employee "+guest.getEmployee().getId()+". Department:"+guest.getEmployee().getDepartment().getName()+". Date:"+guest.getVisitDate()+". Until the meeting is left:"+workTime(guest.getVisitDate(),actualDate));
    }

    private Date between(Date startInclusive, Date endExclusive) {
        long startMillis = startInclusive.getTime();
        long endMillis = endExclusive.getTime();
        long randomMillisSinceEpoch = ThreadLocalRandom
                .current()
                .nextLong(startMillis, endMillis);

        return new Date(randomMillisSinceEpoch);
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
