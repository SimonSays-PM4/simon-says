/*
describe("Order CRUD", () => {
    beforeEach(() => {
        cy.visit("http://localhost:3000/login?returnUrl=%2Fadmin%2Fevents");
        cy.get("h1").contains("Login");
        cy.get("#code").type("mysecretpassword");
        cy.get("form").contains("Login").click();

        cy.wait(500);

        cy.get("h2").contains("Events").should("exist");

        cy.wait(500);
    });

    it("should create an ingredient", () => {
        cy.contains("tr", "TestEventPatrick").contains("button", "Zutaten").click();

        cy.wait(500);
        cy.get("h2").contains("Zutaten").should("exist");

        cy.contains("button", "Erstellen").click();

        cy.url().should("include", "/ingredient/create");

        cy.get("#name").type("Order-Test-Ingredient");
        cy.contains("button", "Erstellen").click();

        cy.wait(500);

        cy.url().should("include", "/ingredients");
    });

    it("should create a menu item", () => {
        cy.contains("button", "Menu Item").click();

        cy.wait(500);
        cy.get("h2").contains("Menu Items").should("exist");

        cy.contains("button", "Erstellen").click();

        cy.url().should("include", "/menuItem/create");

        cy.get("#name").type("Order-Test-Menu-Item");
        cy.get("#price").type("12");
        cy.get("#ingredientSelector").type("Order-Test-Ingredient\n");

        cy.contains("button", "Erstellen").click();

        cy.wait(500);

        cy.url().should("include", "/menuItem");
    });

    it("should create a menu", () => {
        cy.contains("button", "Menus").click();

        cy.wait(500);
        cy.get("h2").contains("Menus").should("exist");

        cy.contains("button", "Erstellen").click();

        cy.url().should("include", "/menu/create");

        cy.get("#name").type("Order-Test-Menu");
        cy.get("#menuItemSelector").type("Order-Test-Menu-Item\n");

        cy.contains("button", "Erstellen").click();

        cy.wait(500);

        cy.url().should("include", "/menu");
    });

    it("should create an order", () => {
        cy.contains("button", "Bestellungen").click();

        cy.wait(500);
        cy.get("h2").contains("Bestellungen").should("exist");

        cy.contains("button", "Erstellen").click();

        cy.url().should("include", "/order/create");

        cy.get("#tableNumber").type("8");

        cy.contains("button", "Menus").click();
        cy.wait(100);
        cy.contains("div", "Order-Test-Menu").click();
        cy.contains("div", "Order-Test-Menu").click();

        cy.contains("button", "Menu Items").click();
        cy.wait(100);
        cy.contains("div", "Order-Test-Menu-Item").click();

        cy.wait(100);
        cy.contains("p", "Preis: 36.00 CHF");

        cy.contains("button", "Bestellung aufgeben").click();

        cy.wait(500);

        cy.url().should("include", "/order");
    });
});
*/
