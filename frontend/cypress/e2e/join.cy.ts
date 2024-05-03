describe("Join", () => {
    beforeEach(() => {
        cy.visit("http://localhost:3000/login?returnUrl=%2Fadmin%2Fevents");
        cy.get("h1").contains("Login");
        cy.get("#code").type("mysecretpassword");
        cy.get("form").contains("Login").click();

        cy.wait(500);

        cy.get("h2").contains("Events").should("exist");

        cy.wait(500);
    });

    it("should create an event", () => {
        cy.contains("button", "Erstellen").click();
        cy.url().should("include", "/event/create");

        cy.get("#name").type("Test-Event");
        cy.get("#password").type("Test-password");
        cy.get("#numberOfTables").clear();
        cy.get("#numberOfTables").type(12);
        cy.contains("button", "Erstellen").click();

        cy.wait(500);

        cy.url().should("include", "/events");
    });

    it("should join an event", () => {
        cy.visit("http://localhost:3000/1/join");
        cy.get("h1").contains("Join Event");
        cy.get("#userName").type("user");
        cy.get("#password").type("Test-password");
        cy.get("form").contains("Join").click();

        cy.wait(500);

        cy.url().should("not.contain", "/join");
    });
});
