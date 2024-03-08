package io.github.palexdev.materialfx.demo.controllers;

import java.rmi.*;

public interface Calculator extends Remote {
    public String calculateGrade(float score) throws RemoteException;
}
