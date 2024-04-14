describe("Login", () => {
    it("Should log in without issues", () => {
        cy.visit("http://localhost:3000");
        cy.get("h1").contains("Login");
        cy.get("form").contains("Login").click();
        cy.get("h1").contains("Home Page");
    });
});
