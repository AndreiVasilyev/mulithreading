package by.epam.jwdmultithreading.entity;

import by.epam.jwdmultithreading.util.IdGenerator;

public class Terminal {

    private final int id;


    public Terminal(){
        this.id= IdGenerator.getTerminalId();
    }

    public int getId(){
        return id;
    }

    public void handleTruck(){

    }
}
