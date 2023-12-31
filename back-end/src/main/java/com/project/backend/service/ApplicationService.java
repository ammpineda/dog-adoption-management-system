package com.project.backend.service;

import com.project.backend.model.Adopter;
import com.project.backend.model.Application;
import com.project.backend.model.Dog;
import com.project.backend.repository.AdopterRepository;
import com.project.backend.repository.ApplicationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class ApplicationService {
    @Autowired
    private ApplicationRepository applicationRepository;
    @Autowired
    private AdopterService adopterService;
    @Autowired
    private DogService dogService;
    public Application createApplication(Application application) {
        Adopter applicant = adopterService.getAdopterById(application.getApplicant().getId());
        Dog dog = dogService.getDogById(application.getDog().getId());

        application.setStatus("Submitted");
        application.setApplicant(applicant);

        dog.setAdoptionStatus("Reserved");
        application.setDog(dog);

        LocalDateTime currentDateTime = LocalDateTime.now();
        application.setSubmittedDate(currentDateTime);
        return applicationRepository.save(application);
    }

    public Application updateApplication(int applicationId, Application update){
        Optional<Application> existingOptional = applicationRepository.findById(applicationId);
        Application application = applicationRepository.findById(applicationId).orElse(null);
        Dog dog = dogService.getDogById(application.getDog().getId());

        if(existingOptional.isPresent()){
            Application existing = existingOptional.get();

            if (update.getStatus()!=null){
                if(existing.getStatus().equalsIgnoreCase("Under Review" )&& update.getStatus().trim().equalsIgnoreCase("Submitted")){
                    existing.setStatus("Under Review");
                } else if (existing.getStatus().equalsIgnoreCase("Approved" )){
                    existing.setStatus("Approved");
                } else {
                    existing.setStatus(update.getStatus());
                }
            }
            if (update.getReviewDate()!=null){
                existing.setReviewDate(update.getReviewDate());
            } else if (update.getStatus().trim().equalsIgnoreCase("Under Review")){
                dog.setAdoptionStatus("Reserved");
                LocalDateTime currentDateTime = LocalDateTime.now();
                existing.setReviewDate(currentDateTime);
            }
            if (update.getApprovalDate()!=null){
                existing.setApprovalDate(update.getApprovalDate());
            } else if (update.getStatus().trim().equalsIgnoreCase("Approved")){
                dog.setAdoptionStatus("Adopted");
                LocalDateTime currentDateTime = LocalDateTime.now();
                existing.setApprovalDate(currentDateTime);
            }
            if (update.getApplicant()!=null){
                existing.setApplicant(update.getApplicant());
            }
            if (update.getDog()!=null){
                existing.setDog(update.getDog());
            }
            existing.setDog(dog);
            return applicationRepository.save(existing);
        }else{
            return null;
        }
    }

    public List<Application> getAllApplications() {
        return (List<Application>) applicationRepository.findAll();
    }

    public Application getApplicationById(int id) {
        return applicationRepository.findById(id).orElse(null);
    }

    public List<Application> searchByApplicantName(String applicantName) {
        return applicationRepository.findByApplicantName(applicantName);
    }

    public List<Application> searchByDogName(String dogName) {
        return applicationRepository.findByDogName(dogName);
    }

    public List<Application> searchByApplicantId(int id) {
        return applicationRepository.findByApplicantId(id);
    }

    public void deleteApplication(int applicationId){
        Application application = applicationRepository.findById(applicationId).orElse(null);
        Dog dog = dogService.getDogById(application.getDog().getId());

        dog.setAdoptionStatus("Open");

        applicationRepository.deleteById(applicationId);
    }

}
