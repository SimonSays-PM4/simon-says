import "@testing-library/jest-dom";
import { render, screen } from "@testing-library/react";
import { HomePage } from "../src/pages/HomePage";

test("renders home page title", () => {
    render(<HomePage />);
    const h1Element = screen.getByText(/Home Page/i);
    expect(h1Element).toBeInTheDocument();
});
