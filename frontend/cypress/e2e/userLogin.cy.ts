describe("User Login", () => {
    let eventId = "0";

    it("Should log as admin and create event to join", () => {
        cy.visit("http://localhost:3000");
        cy.get("h1").contains("Login");
        cy.get("#code").type("mysecretpassword");
        cy.get("form").contains("Login").click();

        cy.wait(500);

        cy.get("h1").contains("Home Page");

        cy.get('a[href="/admin/events"]').click();

        cy.wait(500);

        cy.contains("button", "Erstellen").click();
        cy.url().should("include", "/event/create");

        cy.get("#name").type("UnitTest-Event");
        cy.get("#password").type("Test-password");
        cy.get("#numberOfTables").clear();
        cy.get("#numberOfTables").type("12");
        cy.contains("button", "Erstellen").click();

        cy.wait(500);

        cy.url().should("include", "/events");

        cy.contains("tr", "UnitTest-Event").get('button[id="editAction"]').click();
        cy.wait(500);

        cy.url().then((urlString) => {
            const url = new URL(urlString);
            const paths = url.pathname.split("/");
            const eventIdFromUrl = paths[paths.length - 1]; // eventId is the last segment

            cy.log("Event ID is:", eventIdFromUrl);
            eventId = eventIdFromUrl;
        });
    });

    it("Should log as user to event", () => {
        cy.visit(`http://localhost:3000/${eventId}/join`);
        cy.get("h1").contains("Join Event");
        cy.get("#userName").type("UnitTest-User");
        cy.get("#password").type("Test-password");
        cy.get("form").contains("Join").click();

        cy.wait(500);

        cy.get("h1").contains("Hello!");
    });
});
