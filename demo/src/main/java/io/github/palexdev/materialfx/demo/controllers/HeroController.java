package io.github.palexdev.materialfx.demo.controllers;

import java.net.URL;
import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;
import java.util.ResourceBundle;

import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;

public class HeroController implements Initializable {

    @FXML
    private MFXTextField textField;

    @FXML
    private Label result;

    @FXML
    private MFXButton calculate;

    @FXML
    private MFXButton clear;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        startServer();
        calculate.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> add());
        clear.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            textField.setText("");
            result.setText("");
        });

    }

    private void startServer() {
        try {
            System.setProperty("java.rmi.server.hostname", "localhost");
            Calculator stub = new CalculatorRemote();
            LocateRegistry.createRegistry(5000).bind("grader", stub);
            System.out.println("Grade Calculator Server has started!!");
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private void add() {
        try {
            System.setProperty("java.rmi.server.hostname", "localhost");
            Calculator stub = (Calculator) Naming.lookup("rmi://localhost:5000/grader");
            try {
                result.setText("");
                result.setText("The grade is : " + stub.calculateGrade(Float.parseFloat(textField.getText())));
            } catch (Exception e) {
                if (e.toString().contains("java.lang.NumberFormatException")) {
                    result.setText("The score must be number!!");
                } else
                    throw e;
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }

}