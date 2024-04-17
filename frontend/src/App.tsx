import { BrowserRouter, Route, Routes } from "react-router-dom";
import { HomePage } from "./pages/HomePage";
import { AppProvider } from "./providers/AppProvider";
import { ActivePageType } from "./enums/ActivePageType";
import { LoginPage } from "./pages/LoginPage";
import { EventListPageComponent } from "./pages/event/EventListPage.component.tsx";
import { Error404Page } from "./pages/Error404Page";
import { AuthorizedRoute } from "./routing/AuthorizedRoute";
import { EventCreatePageComponent } from "./pages/event/EventCreatePage.component.tsx";
import { IngredientListPageComponent } from "./pages/ingredient/IngredientListPage.component.tsx";
import { IngredientCreatePageComponent } from "./pages/ingredient/IngredientCreatePage.component.tsx";
import { AuthorizedEventRoute } from "./routing/AuthorizedEventRoute.tsx";
import { MenuListPageComponent } from "./pages/event/menu/MenuListPage.component.tsx";
import { MenuCreatePageComponent } from "./pages/event/menu/MenuCreatePage.component.tsx";
import { EventMenuPage } from "./pages/event/menu/EventMenuPage.component.tsx";
import { EventMenuEditPage } from "./pages/event/menu/EventMenuEditPage.component.tsx";

export default function App() {
    return (
        <AppProvider>
            <BrowserRouter>
                <Routes>
                    <Route path="/login" element={<LoginPage />} />
                    <Route path="/" element={<AuthorizedRoute activePageType={ActivePageType.Home}><HomePage /></AuthorizedRoute>} />
                    <Route path="/admin">
                        <Route path="/admin/events" element={<AuthorizedRoute activePageType={ActivePageType.EventList}><EventListPageComponent /></AuthorizedRoute>} />
                        <Route path="/admin/event/create" element={<AuthorizedEventRoute activePageType={ActivePageType.Event}><EventCreatePageComponent /></AuthorizedEventRoute>}>
                            <Route path="/admin/event/create/:eventId" element={<AuthorizedEventRoute activePageType={ActivePageType.Event}><EventCreatePageComponent /></AuthorizedEventRoute>} />
                        </Route>

                        <Route path="/admin/:eventId">
                            <Route path="/admin/:eventId/ingredients" element={<AuthorizedEventRoute activePageType={ActivePageType.IngredientList}><IngredientListPageComponent /></AuthorizedEventRoute>} />
                            <Route path="/admin/:eventId/menu" element={<AuthorizedEventRoute activePageType={ActivePageType.MenuItem}><EventMenuPage /></AuthorizedEventRoute>} />
                            <Route path="/admin/:eventId/menu/create" element={<AuthorizedEventRoute activePageType={ActivePageType.MenuItem}><EventMenuEditPage /></AuthorizedEventRoute>}>
                                <Route path="/admin/:eventId/menu/create/:menuId" element={<AuthorizedEventRoute activePageType={ActivePageType.MenuItem}><EventMenuEditPage /></AuthorizedEventRoute>} />
                            </Route>
                            <Route path="/admin/:eventId/ingredient/create" element={<AuthorizedEventRoute activePageType={ActivePageType.Ingredient}><IngredientCreatePageComponent /></AuthorizedEventRoute>}>
                                <Route path="/admin/:eventId/ingredient/create/:id" element={<AuthorizedEventRoute activePageType={ActivePageType.Ingredient}><IngredientCreatePageComponent /></AuthorizedEventRoute>} />
                            </Route>
                            <Route path="/admin/:eventId/menu" element={<AuthorizedEventRoute activePageType={ActivePageType.MenuList}><MenuListPageComponent /></AuthorizedEventRoute>} />
                            <Route path="/admin/:eventId/menu/create" element={<AuthorizedEventRoute activePageType={ActivePageType.Menu}><MenuCreatePageComponent /></AuthorizedEventRoute>}>
                                <Route path="/admin/:eventId/menu/create/:id" element={<AuthorizedEventRoute activePageType={ActivePageType.Menu}><MenuCreatePageComponent /></AuthorizedEventRoute>} />
                            </Route>
                        </Route>
                    </Route>

                    <Route path="*" element={<Error404Page />} />
                </Routes>
            </BrowserRouter>
        </AppProvider>
    );
}
