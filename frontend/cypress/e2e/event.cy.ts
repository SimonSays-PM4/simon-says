describe("Event CRUD", () => {
    beforeEach(() => {
        cy.visit("http://localhost:3000");
        cy.get("h1").contains("Login");
        cy.get("form").contains("Login").click();
        cy.get("h1").contains("Home Page");

        cy.get('a[href="/admin/events"]').click();
    });

    it("should create an event", () => {
        cy.contains("button", "Erstellen").click();
        cy.url().should("include", "/event/create");

        cy.get("#name").type("Test-Event");
        cy.get("#password").type("Test-password");
        cy.get("#numberOfTables").clear();
        cy.get("#numberOfTables").type(12);
        cy.contains("button", "Erstellen").click();

        cy.url().should("include", "/events");
    });

    it("should load newly created ingredient", () => {
        cy.get("table").should("exist");
        cy.get("table tr").find("td:first-child").contains("Test-Event").should("exist");
    });
});
