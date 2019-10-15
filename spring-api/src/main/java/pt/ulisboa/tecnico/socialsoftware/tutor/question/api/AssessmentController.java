package pt.ulisboa.tecnico.socialsoftware.tutor.question.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.AssessmentService;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Assessment;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.AssessmentDto;

import javax.validation.Valid;
import java.util.List;

@RestController
@Secured({ "ROLE_ADMIN", "ROLE_TEACHER"})
public class AssessmentController {
    private static Logger logger = LoggerFactory.getLogger(AssessmentController.class);

    private AssessmentService assessmentService;
    
    AssessmentController(AssessmentService assessmentService) {
        this.assessmentService = assessmentService;
    }

    @GetMapping("/assessments")
    public List<AssessmentDto> getAssessment(){
        return this.assessmentService.findAll();
    }

    @Secured({ "ROLE_ADMIN", "ROLE_TEACHER", "ROLE_STUDENT" })
    @GetMapping("/assessments/available")
    public List<AssessmentDto> getAvailableAssessment(){
        return this.assessmentService.findAllAvailable();
    }

    @PostMapping("/assessments")
    public AssessmentDto createAssessment(@Valid @RequestBody AssessmentDto assessment) {
        return this.assessmentService.createAssessment(assessment);
    }

    @PutMapping("/assessments/{assessmentId}")
    public ResponseEntity updateAssessment(@PathVariable Integer assessmentId, @Valid @RequestBody AssessmentDto assessment) {
        this.assessmentService.updateAssessment(assessmentId, assessment);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/assessments/{assessmentId}")
    public ResponseEntity removeAssessment(@PathVariable Integer assessmentId) {
        assessmentService.removeAssessment(assessmentId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/assessments/{assessmentId}/set-status")
    public ResponseEntity assessmentSetStatus(@PathVariable Integer assessmentId, @Valid @RequestBody String status) {
        assessmentService.assessmentSetStatus(assessmentId, Assessment.Status.valueOf(status));
        return ResponseEntity.ok().build();
    }
}