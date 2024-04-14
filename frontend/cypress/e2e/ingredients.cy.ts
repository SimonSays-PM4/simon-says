describe("Ingredient CRUD", () => {
    beforeEach(() => {
        cy.visit("http://localhost:3000/login?returnUrl=%2Fadmin%2F1%2Fingredients");
        cy.get("h1").contains("Login");
        cy.get("form").contains("Login").click();

        cy.get("h2").contains("Zutaten").should("exist");
    });

    it("should create a ingredient", () => {
        cy.contains("button", "Erstellen").click();
        cy.url().should("include", "/ingredient/create");

        cy.get("#name").type("Test-Ingredient");
        cy.contains("button", "Erstellen").click();

        cy.url().should("include", "/ingredients");
    });

    it("should load newly created ingredient", () => {
        cy.get("table").should("exist");
        cy.get("table tr").find("td:first-child").contains("Test-Ingredient").should("exist");
    });

    it("should update newly created ingredient", () => {
        cy.get("table")
            .contains("tr", "Test-Ingredient")
            .find("td:last-child")
            .contains("button", "Bearbeiten")
            .click();

        cy.url().should("include", "/ingredient/create/");

        cy.get("#name").clear();
        cy.get("#name").type("Test-Updated-Ingredient");
        cy.contains("button", "Speichern").click();

        cy.url().should("include", "/ingredients");
    });

    it("should load updated ingredient", () => {
        cy.get("table").should("exist");
        cy.get("table tr").find("td:first-child").contains("Test-Updated-Ingredient").should("exist");
    });

    it("should delete newly created ingredient", () => {
        cy.get("table")
            .contains("tr", "Test-Updated-Ingredient")
            .find("td:last-child")
            .contains("button", "Löschen")
            .click();

        cy.wait(500); // wait for the popup to show up

        cy.get('div[tabIndex="-1"].modal').should("exist");
        cy.get('div[tabIndex="-1"].modal').contains("button", "Löschen").click();

        cy.wait(500); // wait for the popup to close
    });

    it("should not load newly created ingredient anymore", () => {
        cy.get("table").should("exist");
        cy.get("tbody").children().should("have.length", 0);
    });
});