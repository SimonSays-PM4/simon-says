describe("Ingredient CRUD", () => {
    beforeEach(() => {
        cy.visit("http://localhost:3000");
        cy.get("h1").contains("Login");
        cy.get("form").contains("Login").click();
        cy.get("h1").contains("Home Page");

        cy.get('a[href="/admin/1/ingredients"]').click();
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

        cy.get("table").should("exist");
        cy.get("table tr").find("td:first-child").contains("Test-Updated-Ingredient").should("not.exist");
    });

    it("should not load newly created ingredient anymore", () => {
        cy.get("table").should("exist");
        cy.get("table tr").find("td:first-child").contains("Test-Updated-Ingredient").should("not.exist");
    });
});
