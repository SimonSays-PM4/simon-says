describe("Login", () => {
    it("Should log in without issues", () => {
        cy.visit("https://simonsays-stage.pm4.init-lab.ch")
        cy.get('h1').contains('Login')
        cy.get('form').contains('Login').click()
        cy.get('h1').contains('Home Page')
    })
})