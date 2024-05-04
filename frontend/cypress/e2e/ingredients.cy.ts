describe("Ingredient CRUD", () => {
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

    it("should create an ingredient", () => {
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

    it("should load newly created ingredient", () => {
        cy.contains("button", "Zutaten").click();
        cy.wait(500);
        cy.get("h2").contains("Zutaten").should("exist");

        cy.get("table").should("exist");
        cy.get("table tr").find("td:first-child").contains("Test-Ingredient").should("exist");
    });

    it("should update newly created ingredient", () => {
        cy.contains("button", "Zutaten").click();
        cy.wait(500);
        cy.get("h2").contains("Zutaten").should("exist");

        cy.get("table")
            .contains("tr", "Test-Ingredient")
            .find("td:last-child")
            .get('button[id="editAction"]')
            .click();

        cy.url().should("include", "/ingredient/create/");

        cy.get("#name").clear();
        cy.get("#name").type("Test-Updated-Ingredient");
        cy.contains("button", "Speichern").click();

        cy.url().should("include", "/ingredients");
    });

    it("should load updated ingredient", () => {
        cy.contains("button", "Zutaten").click();
        cy.wait(500);
        cy.get("h2").contains("Zutaten").should("exist");

        cy.get("table").should("exist");
        cy.get("table tr").find("td:first-child").contains("Test-Updated-Ingredient").should("exist");
    });

    it("should delete newly created ingredient", () => {
        cy.contains("button", "Zutaten").click();
        cy.wait(500);
        cy.get("h2").contains("Zutaten").should("exist");

        cy.get("table")
            .contains("tr", "Test-Updated-Ingredient")
            .find("td:last-child")
            .find('button[id="deleteAction"]')
            .click();

        cy.wait(500); // wait for the popup to show up

        cy.get('div[tabIndex="-1"].modal').should("exist");
        cy.get('div[tabIndex="-1"].modal').contains("button", "Löschen").click();

        cy.wait(500); // wait for the popup to close
    });

    it("should not load newly created ingredient anymore", () => {
        cy.contains("button", "Zutaten").click();
        cy.wait(500);
        cy.get("h2").contains("Zutaten").should("exist");

        cy.get("table").should("exist");
        cy.get("tbody").children().should("have.length", 1);
    });

    it("should delete created event", () => {
        cy.get('button[id="deleteAction"]').click()

        cy.wait(500); // wait for the popup to show up

        cy.get('div[tabIndex="-1"].modal').should("exist");
        cy.get('div[tabIndex="-1"].modal').contains("button", "Löschen").click();

        cy.wait(500); // wait for the popup to close
    });
});
