describe("MenuItem", () => {
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
        cy.get("#numberOfTables").type("12");
        cy.contains("button", "Erstellen").click();

        cy.wait(500);

        cy.url().should("include", "/events");
    });

    it("should create an ingredient", () => {
        cy.contains("tr", "Test-Event").contains("button", "Zutaten").click();
        cy.wait(500);
        cy.get("h2").contains("Zutaten").should("exist");

        cy.contains("button", "Erstellen").click();

        cy.url().should("include", "/ingredient/create");

        cy.get("#name").type("Test-Ingredient");
        cy.get("#mustBeProduced").click();
        cy.contains("button", "Erstellen").click();

        cy.wait(500);

        cy.url().should("include", "/ingredients");
    });

    it("should create a menuItem", () => {
        cy.contains("tr", "Test-Event").contains("button", "Menüpunkte").click();

        cy.wait(500);
        cy.get("h2").contains("Menüpunkte").should("exist");

        cy.contains("button", "Erstellen").click();

        cy.url().should("include", "/menuItem/create");

        cy.get("#name").type("Test-Menu-Item");
        cy.get("#price").type("1.00");
        cy.get("#ingredientSelector").type("Test-Ingredient\n");

        cy.contains("button", "Erstellen").click();

        cy.wait(500);

        cy.url().should("include", "/menuItem");
    });

    it("should delete created menuItem", () => {
        cy.contains("tr", "Test-Event").contains("button", "Menüpunkte").click();

        cy.wait(500);
        cy.get("h2").contains("Menüpunkte").should("exist");

        cy.contains("tr", "Test-Menu-Item").find("td:last-child").find("button:last-child").click();

        cy.wait(500); // wait for the popup to show up

        cy.get('div[tabIndex="-1"].modal').should("exist");
        cy.get('div[tabIndex="-1"].modal').contains("button", "Löschen").click();

        cy.wait(500); // wait for the popup to close

        cy.url().should("include", "/menuItem");
    });

    it("should delete created ingredient", () => {
        cy.contains("tr", "Test-Event").contains("button", "Zutaten").click();

        cy.wait(500);
        cy.get("h2").contains("Zutaten").should("exist");

        cy.contains("tr", "Test-Ingredient").find("td:last-child").find("button:last-child").click();

        cy.wait(500); // wait for the popup to show up

        cy.get('div[tabIndex="-1"].modal').should("exist");
        cy.get('div[tabIndex="-1"].modal').contains("button", "Löschen").click();

        cy.wait(500); // wait for the popup to close

        cy.url().should("include", "/ingredients");
    });

    it("should delete created event", () => {
        cy.contains("tr", "Test-Event").find("td:last-child").find("button:last-child").click();

        cy.wait(500); // wait for the popup to show up

        cy.get('div[tabIndex="-1"].modal').should("exist");
        cy.get('div[tabIndex="-1"].modal').contains("button", "Löschen").click();
    });
});
