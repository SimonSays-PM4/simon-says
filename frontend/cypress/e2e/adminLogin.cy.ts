describe("Admin Login", () => {
    it("Should log in without issues", () => {
        cy.visit("http://localhost:3000/login");
        cy.get("h1").contains("Login");
        cy.get("#code").type("mysecretpassword");
        cy.get("form").contains("Login").click();

        cy.wait(500);

        cy.get("h2").contains("Events");

    });

    it("Should log out in without issues", () => {
        cy.visit("http://localhost:3000/");
        cy.get("h1").contains("Login");
        cy.get("#code").type("mysecretpassword");
        cy.get("form").contains("Login").click();

        cy.wait(500);
        cy.get("h2").contains("Events");

        cy.contains("button", "Logout").click();

        cy.wait(500);

        cy.url().should("include", "/login");
        cy.get("h1").contains("Login");
    });
});
