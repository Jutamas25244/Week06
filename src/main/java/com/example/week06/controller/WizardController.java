package com.example.week06.controller;

import com.example.week06.pojo.Wizard;
import com.example.week06.repository.WizardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class WizardController {
    @Autowired
    private WizardService wizardService;

    @RequestMapping(value = "/wizards", method = RequestMethod.GET)
    public ResponseEntity<?> getWizards(){
        List<Wizard> wizards = wizardService.retrieveWizards();
        System.out.println(wizards);
        return ResponseEntity.ok(wizards);
    }

    @RequestMapping(value ="/addWizard", method = RequestMethod.POST)
    public String addWizards(@RequestBody Wizard wizard){
        Wizard wizards = wizardService.createWizard(wizard);
        return "Wizard has been created.";
    }

    @RequestMapping(value ="/updateWizard", method = RequestMethod.POST)
    public String updateWizards(@RequestBody Wizard wizard){
        Wizard wizards = wizardService.retrieveByID(wizard.get_id());
        if (wizards != null){
            wizardService.updateWizard(wizard);
            return "Wizard has been updated.";
        } else {
            return "Update fail.";
        }
    }

    @RequestMapping(value ="/deleteWizard", method = RequestMethod.POST)
    public String deleteWizards (@RequestBody Wizard wizard){
        Wizard wizards = wizardService.retrieveByID(wizard.get_id());
        boolean status = wizardService.deleteWizard(wizard);
        return status?"Wizard has been deleted":"Noting for delete";
    }
}
