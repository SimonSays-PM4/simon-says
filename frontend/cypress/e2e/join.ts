describe("Login", () => {
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

    it("should create a ingredient", () => {
        cy.contains("button", "Zutaten").click();
        cy.wait(500);
        cy.get("h2").contains("Zutaten").should("exist");

        cy.contains("button", "Erstellen").click();

        cy.url().should("include", "/ingredient/create");

        cy.get("#name").type("Test-Ingredient");
        cy.contains("button", "Erstellen").click();

        cy.wait(500);

        cy.url().should("include", "/ingredients");
    });

    it("should create a station", () => {
        cy.contains("button", "Station").click();

        cy.wait(500);
        cy.get("h2").contains("Stationen").should("exist");

        cy.contains("button", "Erstellen").click();

        cy.url().should("include", "/station/create");

        cy.get("#name").type("Test-Menu-Item");
        cy.get("#ingredientSelector").type("Test-Ingredient\n");
        cy.contains("button", "Erstellen").click();

        cy.wait(500);

        cy.url().should("include", "/station");
    });
});
