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

    it("should create an event", () => {
        cy.contains("button", "Erstellen").click();
        cy.url().should("include", "/event/create");

        cy.get("#name").type("UnitTest-Event");
        cy.get("#password").type("Test-password");
        cy.get("#numberOfTables").clear();
        cy.get("#numberOfTables").type("12");
        cy.contains("button", "Erstellen").click();

        cy.wait(500);

        cy.url().should("include", "/events");
    });

    it("should create an ingredient", () => {
        cy.contains("tr", "UnitTest-Event").contains("button", "Zutaten").click();

        cy.wait(500);
        cy.get("h2").contains("Zutaten").should("exist");

        cy.contains("button", "Erstellen").click();

        cy.url().should("include", "/ingredient/create");

        cy.get("#name").type("Order-Test-Ingredient");
        cy.get("#mustBeProduced").click();
        cy.contains("button", "Erstellen").click();

        cy.wait(500);

        cy.url().should("include", "/ingredients");
    });

    it("should create an ingredient II", () => {
        cy.contains("tr", "UnitTest-Event").contains("button", "Zutaten").click();

        cy.wait(500);
        cy.get("h2").contains("Zutaten").should("exist");

        cy.contains("button", "Erstellen").click();

        cy.url().should("include", "/ingredient/create");

        cy.get("#name").type("Order-Test-Ingredient-2");
        cy.get("#mustBeProduced").click();
        cy.contains("button", "Erstellen").click();

        cy.wait(500);

        cy.url().should("include", "/ingredients");
    });

    it("should create a menu item", () => {
        cy.contains("tr", "UnitTest-Event").contains("button", "Menüpunkte").click();

        cy.wait(500);
        cy.get("h2").contains("Menüpunkte").should("exist");

        cy.contains("button", "Erstellen").click();

        cy.url().should("include", "/menuItem/create");

        cy.get("#name").type("Order-Test-Menu-Item");
        cy.get("#price").type("12");
        cy.get("#ingredientSelector").type("Order-Test-Ingredient\n");
        cy.get("#ingredientSelector").type("Order-Test-Ingredient-2\n");

        cy.contains("button", "Erstellen").click();

        cy.wait(500);

        cy.url().should("include", "/menuItem");
    });

    it("should create a menu item II", () => {
        cy.contains("tr", "UnitTest-Event").contains("button", "Menüpunkte").click();

        cy.wait(500);
        cy.get("h2").contains("Menüpunkte").should("exist");

        cy.contains("button", "Erstellen").click();

        cy.url().should("include", "/menuItem/create");

        cy.get("#name").type("Order-Test-Menu-Item-II");
        cy.get("#price").type("12");
        cy.get("#ingredientSelector").type("Order-Test-Ingredient\n");
        cy.get("#ingredientSelector").type("Order-Test-Ingredient-2\n");

        cy.contains("button", "Erstellen").click();

        cy.wait(500);

        cy.url().should("include", "/menuItem");
    });

    it("should create a menu", () => {
        cy.contains("tr", "UnitTest-Event").contains("button", "Menüs").click();

        cy.wait(500);
        cy.get("h2").contains("Menüs").should("exist");

        cy.contains("button", "Erstellen").click();

        cy.url().should("include", "/menu/create");

        cy.get("#name").type("Order-Test-Menu");
        cy.get("#price").type("34.03");
        cy.get("#menuItemSelector").type("Order-Test-Menu-Item\n");
        cy.get("#menuItemSelector").type("Order-Test-Menu-Item-II\n");

        cy.contains("button", "Erstellen").click();

        cy.wait(500);

        cy.url().should("include", "/menu");
    });

    it("should create an order", () => {
        cy.get("tr").contains("UnitTest-Event").parents("tr").find("#joinAction").click();
        cy.get("h1").contains("Event beitreten");
        cy.get("#userName").type("test");
        cy.get("#password").type("Test-password");
        cy.wait(500);
        cy.get("form").contains("Beitreten").click();
        cy.wait(500);
        cy.contains("h5", "Bestellung").click();

        cy.contains("button", "Erstellen").click();

        cy.url().should("include", "/order/create");

        cy.get("#tableNumber").type("8");

        cy.contains("button", "Menüs").click();
        cy.wait(100);
        cy.contains("div", "Order-Test-Menu").click();
        cy.contains("div", "Order-Test-Menu").click();

        cy.contains("button", "Menüpunkte").click();
        cy.wait(100);
        cy.contains("div", "Order-Test-Menu-Item").click();
        cy.contains("div", "Order-Test-Menu-Item").click();

        cy.wait(500);

        // edit menu
        cy.contains("div", "Selektierte Menüs").find("button", "Bearbeiten").first().click();
        cy.wait(500); // wait for the popup to show up

        cy.get("div#headlessui-portal-root").within(() => {
            cy.contains("button", "Entfernen").first().click();
            cy.contains("button", "Speichern").click();
        });

        cy.wait(500); // wait for the popup to close

        // edit menu item
        cy.contains("div", "Selektierte Menüpunkte").find("button", "Bearbeiten").first().click();
        cy.wait(500); // wait for the popup to show up

        cy.get("div#headlessui-portal-root").within(() => {
            cy.contains("button", "Entfernen").first().click();
            cy.contains("button", "Speichern").click();
        });

        cy.wait(500); // wait for the popup to close

        // delete on menu from order
        cy.contains("div", "Selektierte Menüs").within(() => {
            cy.contains("button", "Entfernen").first().click();
        });
        cy.wait(500);

        // delete on menu item from order
        cy.contains("div", "Selektierte Menüpunkte").within(() => {
            cy.contains("button", "Entfernen").first().click();
        });
        cy.wait(100);

        cy.contains("p", "Preis: 46.03 CHF");

        cy.contains("button", "Bestellung aufgeben").click();

        cy.wait(500);

        cy.url().should("include", "/order");
    });

    it("should delete newly created order", () => {
        cy.get("tr").contains("UnitTest-Event").parents("tr").find("#joinAction").click();
        cy.get("h1").contains("Event beitreten");
        cy.get("#userName").type("test");
        cy.get("#password").type("Test-password");
        cy.wait(500);
        cy.get("form").contains("Beitreten").click();
        cy.wait(500);
        cy.contains("h5", "Bestellung").click();

        cy.wait(500);

        cy.get("table").contains("tr", "8").find("td:last-child").find("button:last-child").click();

        cy.wait(500); // wait for the popup to show up

        cy.get('div[tabIndex="-1"].modal').should("exist");
        cy.get('div[tabIndex="-1"].modal').contains("button", "Löschen").click();

        cy.wait(500); // wait for the popup to close
    });

    it("should delete created menu", () => {
        cy.contains("tr", "UnitTest-Event").contains("button", "Menüs").click();

        cy.wait(500);
        cy.get("h2").contains("Menüs").should("exist");

        cy.contains("tr", "Order-Test-Menu").find("td:last-child").find("button:last-child").click();

        cy.wait(500); // wait for the popup to show up

        cy.get('div[tabIndex="-1"].modal').should("exist");
        cy.get('div[tabIndex="-1"].modal').contains("button", "Löschen").click();

        cy.wait(500); // wait for the popup to close
    });

    it("should delete created menu item II", () => {
        cy.contains("tr", "UnitTest-Event").contains("button", "Menüpunkte").click();

        cy.wait(500);
        cy.get("h2").contains("Menüpunkte").should("exist");

        cy.contains("tr", "Order-Test-Menu-Item-II").find("td:last-child").find("button:last-child").click();

        cy.wait(500); // wait for the popup to show up

        cy.get('div[tabIndex="-1"].modal').should("exist");
        cy.get('div[tabIndex="-1"].modal').contains("button", "Löschen").click();

        cy.wait(500); // wait for the popup to close
    });

    it("should delete created menu item", () => {
        cy.contains("tr", "UnitTest-Event").contains("button", "Menüpunkte").click();

        cy.wait(500);
        cy.get("h2").contains("Menüpunkte").should("exist");

        cy.contains("tr", "Order-Test-Menu-Item").find("td:last-child").find("button:last-child").click();

        cy.wait(500); // wait for the popup to show up

        cy.get('div[tabIndex="-1"].modal').should("exist");
        cy.get('div[tabIndex="-1"].modal').contains("button", "Löschen").click();

        cy.wait(500); // wait for the popup to close
    });

    it("should delete created ingredient", () => {
        cy.contains("tr", "UnitTest-Event").contains("button", "Zutaten").click();

        cy.wait(500);
        cy.get("h2").contains("Zutaten").should("exist");

        cy.contains("tr", "Order-Test-Ingredient").find("td:last-child").find("button:last-child").click();

        cy.wait(500); // wait for the popup to show up

        cy.get('div[tabIndex="-1"].modal').should("exist");
        cy.get('div[tabIndex="-1"].modal').contains("button", "Löschen").click();

        cy.wait(500); // wait for the popup to close
    });

    it("should delete created ingredient II", () => {
        cy.contains("tr", "UnitTest-Event").contains("button", "Zutaten").click();

        cy.wait(500);
        cy.get("h2").contains("Zutaten").should("exist");

        cy.contains("tr", "Order-Test-Ingredient-2").find("td:last-child").find("button:last-child").click();

        cy.wait(500); // wait for the popup to show up

        cy.get('div[tabIndex="-1"].modal').should("exist");
        cy.get('div[tabIndex="-1"].modal').contains("button", "Löschen").click();

        cy.wait(500); // wait for the popup to close
    });

    it("should delete created event", () => {
        cy.contains("tr", "UnitTest-Event").find("td:last-child").find("button:last-child").click();

        cy.wait(500); // wait for the popup to show up

        cy.get('div[tabIndex="-1"].modal').should("exist");
        cy.get('div[tabIndex="-1"].modal').contains("button", "Löschen").click();

        cy.wait(500); // wait for the popup to close
    });
});
