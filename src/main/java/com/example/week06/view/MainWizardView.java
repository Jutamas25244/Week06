package com.example.week06.view;

import com.example.week06.pojo.Wizard;
import com.example.week06.pojo.Wizards;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.ArrayList;

@Route(value = "mainPage.it")
public class MainWizardView extends VerticalLayout {
    private TextField tf1, tf2;
    private RadioButtonGroup<String> rd1, rd2;
    private ComboBox<String> cb1, cb2, cb3;
    private Button btn1, btn2, btn3, btn4, btn5;
    private HorizontalLayout hl1;

    private Notification nf;

    private Wizards wiz;
    private int wantIndex = 0;

    public MainWizardView(){
        wiz = new Wizards();
        tf1 = new TextField("", "Fullname");
        tf2 = new TextField("Dollars", "$");
        rd1 = new RadioButtonGroup<>("Gender :");
        cb1 = new ComboBox<>();
        cb2 = new ComboBox<>();
        cb3 = new ComboBox<>();
        btn1 = new Button("<<");
        btn2 = new Button("Create");
        btn3 = new Button("Update");
        btn4 = new Button("Delete");
        btn5 = new Button(">>");
        hl1 = new HorizontalLayout();
        nf = new Notification();

        rd1.setItems("Male", "Female");
        cb1.setItems("Student", "Teacher");
        cb1.setPlaceholder("Position");
        cb2.setItems("Hogwarts", "Beauxbatons", "Durmstrang");
        cb2.setPlaceholder("School");
        cb3.setItems("Gryffindor", "Ravenclaw", "Hufflepuff", "Slytherin");
        cb3.setPlaceholder("House");

        hl1.add(btn1, btn2, btn3, btn4, btn5);

        this.add(tf1, rd1, cb1, tf2, cb2, cb3, hl1);
        this.fetchData();

        btn1.addClickListener(event -> {
           // (<<)
            if (wantIndex == 0) {
                wantIndex = 0;
            }else {
                wantIndex -= 1;
            }
            this.onTimeData();
        });

        btn2.addClickListener(event -> {
            //create
            String name = tf1.getValue();
            int money = Integer.parseInt(tf2.getValue());
            String sex = rd1.getValue().equals("Male") ? "m" : "f";
            String position = cb1.getValue().equals("Teacher") ? "teacher" : "student";
            String school = cb2.getValue();
            String house = cb3.getValue();
            Wizard newbie = new Wizard(null, sex, name, school, house, money, position);
            String output = WebClient.create()
                    .post()
                    .uri("http://localhost:8080/addWizard")
                    .body(Mono.just(newbie), Wizard.class)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            nf.show(output);
            this.fetchData();
            this.onTimeData();
        });

        btn3.addClickListener(event -> {
            //update
            String name = tf1.getValue();
            int money = Integer.parseInt(tf2.getValue());
            String sex = rd1.getValue().equals("Male") ? "m" : "f";
            String position = cb1.getValue().equals("Teacher") ? "teacher" : "student";
            String school = cb2.getValue();
            String house = cb3.getValue();
            Wizard updateWis = new Wizard(wiz.getModel().get(wantIndex).get_id(), sex, name, school, house, money, position);
            String output = WebClient.create()
                    .post()
                    .uri("http://localhost:8080/updateWizard")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Mono.just(updateWis), Wizard.class)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            nf.show(output);
            this.fetchData();
            this.onTimeData();
        });

        btn4.addClickListener(event -> {
            //delete
            String output = WebClient.create()
                    .post()
                    .uri("http://localhost:8080/deleteWizard")
                    .body(Mono.just(wiz.getModel().get(wantIndex)), Wizard.class)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            nf.show(output);
            this.wantIndex = this.wantIndex != 0 ? this.wantIndex-1 : this.wantIndex+1;
            this.fetchData();
            this.onTimeData();
        });

        btn5.addClickListener(event -> {
            // (>>)
            if (wantIndex == wiz.getModel().size() - 1) {
                wantIndex = wiz.getModel().size() - 1;
            } else {
                wantIndex += 1;
            }
            this.onTimeData();
        });
    }

    private void onTimeData() {
        if (wiz.getModel().size() != 0){
            this.tf1.setValue(wiz.getModel().get(wantIndex).getName());
            this.rd1.setValue(wiz.getModel().get(wantIndex).getSex().equals("m") ? "Male" : "Female");
            this.tf2.setValue(String.valueOf(wiz.getModel().get(wantIndex).getMoney()));
            this.cb1.setValue(wiz.getModel().get(wantIndex).getPosition().equals("teacher") ? "Teacher" : "Student");
            this.cb2.setValue(wiz.getModel().get(wantIndex).getSchool());
            this.cb3.setValue(wiz.getModel().get(wantIndex).getHouse());
        }else {
            this.tf1.setValue("");
            this.rd1.setValue("");
            this.tf2.setValue("");
            this.cb1.setValue("");
            this.cb2.setValue("");
            this.cb3.setValue("");
        }
    }
    private void fetchData() {
        ArrayList<Wizard> wizardAll = WebClient.create()
                .get()
                .uri("http://localhost:8080/wizards")
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<ArrayList<Wizard>>() {})
                .block();
        wiz.setModel(wizardAll);
    }
}
