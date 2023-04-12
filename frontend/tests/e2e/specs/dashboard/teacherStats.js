function populate2023CE() {
    cy.createAndAddCourseExecutionToDemoTeacher("1 Semestre 2023/2024");

    cy.createAndAddQuizToCourseExecution("Quiz 1", "1 Semestre 2023/2024");
    cy.createAndAddQuizToCourseExecution("Quiz 2", "1 Semestre 2023/2024");
    cy.createAndAddQuizToCourseExecution("Quiz 3", "1 Semestre 2023/2024");

    cy.createAndAddStudentToCourseExecution("Student 1", "1 Semestre 2023/2024");
    cy.createAndAddStudentToCourseExecution("Student 2", "1 Semestre 2023/2024");
    cy.createAndAddStudentToCourseExecution("Student 3", "1 Semestre 2023/2024");

    cy.createAndAddQuestionToQuiz("Quiz 1");
    cy.createAndAddQuestionToQuiz("Quiz 2");

    cy.createAndAddQuizAnswer("Quiz 1", "Student 1");
    cy.createAndAddQuizAnswer("Quiz 2", "Student 1");
    cy.createAndAddQuizAnswer("Quiz 3", "Student 1");

    cy.addQuestionAnswer(1, 1);
}

function populate2022CE() {
    cy.createAndAddCourseExecutionToDemoTeacher("1 Semestre 2022/2023");

    cy.createAndAddQuizToCourseExecution("Quiz 4", "1 Semestre 2022/2023");
    cy.createAndAddQuizToCourseExecution("Quiz 5", "1 Semestre 2022/2023");

    cy.createAndAddStudentToCourseExecution("Student 4", "1 Semestre 2022/2023");
    cy.createAndAddStudentToCourseExecution("Student 5", "1 Semestre 2022/2023");

    cy.createAndAddQuestionToQuiz("Quiz 4");
    cy.createAndAddQuestionToQuiz("Quiz 5");
}

function populate2019CE() {
    cy.createAndAddCourseExecutionToDemoTeacher("1 Semestre 2019/2020");
    cy.createAndAddQuizToCourseExecution("Quiz 6", "1 Semestre 2019/2020");
    cy.createAndAddStudentToCourseExecution("Student 6", "1 Semestre 2019/2020");
    cy.createAndAddQuestionToQuiz("Quiz 6");
}

function removeQuizzes() {
    cy.deleteQuizzesFromCourseExecution("1 Semestre 2019/2020");
    cy.deleteQuizzesFromCourseExecution("1 Semestre 2022/2023");
    cy.deleteQuizzesFromCourseExecution("1 Semestre 2023/2024");
}

function removeCourseExecutions() {
    cy.removeCourseExecution("1 Semestre 2019/2020");
    cy.removeCourseExecution("1 Semestre 2022/2023");
    cy.removeCourseExecution("1 Semestre 2023/2024");
}

describe('TeacherStats', () => {
  before(() => {
    cy.request('http://localhost:8080/auth/demo/teacher')
      .as('loginResponse')
      .then((response) => {
        Cypress.env('token', response.body.token);
        return response;
      });

    cy.demoTeacherLogin();

    populate2023CE();
    populate2022CE();
    populate2019CE();

    cy.contains('Logout').click();
  });

  after(() => {
    cy.removeTeacherDashboardFromDemoTeacher("1 Semestre 2023/2024");
    cy.removeAllQuestionAnswers();
    cy.removeAllQuestions();
    cy.removeAllQuizAnswers();
    cy.removeAllStudents();
    removeQuizzes();
    removeCourseExecutions();
  });
});
