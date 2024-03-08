package io.github.palexdev.materialfx.demo.controllers;

import java.rmi.*;
import java.rmi.server.*;

public class CalculatorRemote extends UnicastRemoteObject implements Calculator {

    CalculatorRemote() throws RemoteException {
        super();
    }

    public String calculateGrade(float score) {
        System.out.println("------------------------------");

        System.out.println("Score: " + score);
        String grade = "Invalid";
        if (score < 0) {
            System.out.println("Grade: " + grade);
            return grade;
        }
        if (score < 20)
            grade = "E";
        else if (score < 40)
            grade = "D";
        else if (score < 60)
            grade = "C";
        else if (score < 80)
            grade = "B";
        else if (score < 100)
            grade = "A";
        System.out.println("Grade: " + grade);
        return grade;
    }

}
