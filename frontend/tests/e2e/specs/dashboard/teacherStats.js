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
  cy.createAndAddQuizToCourseExecution("Quiz 7", "1 Semestre 2019/2020");
  
  cy.createAndAddQuestionToQuiz("Quiz 6");
  cy.createAndAddQuestionToQuiz("Quiz 7");
  
  cy.createAndAddStudentToCourseExecution("Student 6", "1 Semestre 2019/2020");
  
  cy.createAndAddQuizAnswer("Quiz 6", "Student 6");
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

function removeAllStudents() {
    cy.removeStudent("Student 1");
    cy.removeStudent("Student 2");
    cy.removeStudent("Student 3");
    cy.removeStudent("Student 4");
    cy.removeStudent("Student 5");
    cy.removeStudent("Student 6");
}

function visualTestChart(filename) {
  const PNG = require('pngjs').PNG;
  const pixelmatch = require('pixelmatch');

  cy.readFile(
    `./tests/e2e/base-screenshots/expected/${filename}`, 'base64'
  ).then(baseImage => {
    cy.readFile(
      `./tests/e2e/screenshots/dashboard/teacherStats.js/${filename}`, 'base64'
    ).then(chartImage => {
      const img1 = PNG.sync.read(Buffer.from(baseImage, 'base64'));
      const img2 = PNG.sync.read(Buffer.from(chartImage, 'base64'));
      const { width, height } = img1;
      const diff = new PNG({ width, height });

      // calculate pixel diff percentage
      const numDiffPixels = pixelmatch(img1.data, img2.data, diff.data, width, height, { threshold: 0.1 });
      const diffPercent = (numDiffPixels / (width * height) * 100);
      expect(diffPercent).to.be.below(5);
    });
  });
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
    removeAllStudents();
    removeQuizzes();
    removeCourseExecutions();
  });

  it('teacher accesses dashboard of the 2023 course execution', () => {
    cy.demoTeacherLogin();

    cy.get('[data-cy="changeCourseButton"]').click();
    cy.get('[data-cy="selectCourseButton"]').eq(0).click();
    cy.get('[data-cy="dashboardMenuButton"]').click();

    cy.get('[data-cy="numStudents"]').should('have.text', '3');
    cy.get('[data-cy="numMore75CorrectQuestions"]').should('have.text', '0');
    cy.get('[data-cy="numAtLeast3Quizzes"]').should('have.text', '1');

    cy.get('[data-cy="numQuizzes"]').should('have.text', '3');
    cy.get('[data-cy="numUniqueAnsweredQuizzes"]').should('have.text', '3');
    cy.get('[data-cy="averageQuizzesSolved"]').should('have.text', '1');

    cy.get('[data-cy="numQuestions"]').should('have.text', '2');
    cy.get('[data-cy="answeredQuestionsUnique"]').should('have.text', '1');
    cy.get('[data-cy="averageQuestionsAnswered"]').should('have.text', '1');

    cy.get('canvas').eq(0).scrollIntoView().wait(5000).screenshot("2023-student-stats-chart")
    cy.get('canvas').eq(1).scrollIntoView().wait(5000).screenshot("2023-quiz-stats-chart")
    cy.get('canvas').eq(2).scrollIntoView().wait(5000).screenshot("2023-question-stats-chart")

    visualTestChart('2023-student-stats-chart.png');
    visualTestChart('2023-quiz-stats-chart.png');
    visualTestChart('2023-question-stats-chart.png');

    cy.contains('Logout').click();
  });

  it('teacher accesses dashboard of the 2019 course execution', () => {
    cy.demoTeacherLogin();

    cy.get('[data-cy="changeCourseButton"]').click();
    cy.get('[data-cy="selectCourseButton"]').eq(0).click();
    cy.get('[data-cy="dashboardMenuButton"]').click();

    cy.get('[data-cy="numStudents"]').should('have.text', '3');
    cy.get('[data-cy="numMore75CorrectQuestions"]').should('have.text', '0');
    cy.get('[data-cy="numAtLeast3Quizzes"]').should('have.text', '1');

    cy.get('[data-cy="numQuizzes"]').should('have.text', '3');
    cy.get('[data-cy="numUniqueAnsweredQuizzes"]').should('have.text', '3');
    cy.get('[data-cy="averageQuizzesSolved"]').should('have.text', '1');

    cy.get('[data-cy="numQuestions"]').should('have.text', '2');
    cy.get('[data-cy="answeredQuestionsUnique"]').should('have.text', '1');
    cy.get('[data-cy="averageQuestionsAnswered"]').should('have.text', '1');

    cy.contains('Logout').click();
  });
});
